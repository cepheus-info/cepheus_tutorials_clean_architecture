package info.cepheus.sample.spring_kafka_sample.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import info.cepheus.sample.spring_kafka_sample.application.BatchPromotionMessage;
import info.cepheus.sample.spring_kafka_sample.application.BatchSubsidyMessage;
import info.cepheus.sample.spring_kafka_sample.coreapi.LongRunningProcess;
import info.cepheus.sample.spring_kafka_sample.infrastructure.task.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Printed;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.SeekToCurrentErrorHandler;
import org.springframework.kafka.support.converter.ByteArrayJsonMessageConverter;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.kafka.support.mapping.DefaultJackson2JavaTypeMapper;
import org.springframework.kafka.support.mapping.Jackson2JavaTypeMapper;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;

@Slf4j
@EnableKafka
@Configuration
public class KafkaConfiguration {
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    TaskCompleteMessageSerializer taskCompleteMessageSerializer;

    @Autowired
    TaskCompletedMessageDeserializer taskCompletedMessageDeserializer;
//
//    @Autowired
//    TaskManager taskManager;

    @Bean
    public Serde<TaskCompletedMessage> taskCompletedMessageSerde() {
        return new Serde<>() {
            @Override
            public Serializer<TaskCompletedMessage> serializer() {
                return taskCompleteMessageSerializer;
            }

            @Override
            public Deserializer<TaskCompletedMessage> deserializer() {
                return taskCompletedMessageDeserializer;
            }
        };
    }

    /**
     * transform InitializedChildTask & ProgressedChildTask into a CompletedTaskStream.
     * say if 500 childTasks initialized & 400 childTasks progressed, then CompletedMessage will be false.
     * if 500 of 500 childTasks progressed, then CompletedMessage will be true.
     *
     * @return A BiFunction which defined (2 inputs, 1 output)
     * input-0: childTaskInitializedStream
     * input-1: childTaskProgressedStream
     * output-0: TaskCompletedMessageStream which indicates a batchTransaction is totally completed.
     */
    @Bean
    public BiFunction<KStream<String, String>, KStream<String, String>, KStream<String, TaskCompletedMessage>> transform() {
        return (childTaskInitializedStream, childTaskProgressedStream) -> {
//        KStream<String, String> childTaskInitializedStream = kStreamBuilder.stream(TaskProcess.ChildTaskInitialized);
//        KStream<String, String> childTaskProgressedStream = kStreamBuilder.stream(TaskProcess.ChildTaskProgressed);

            // step1: grouping all taskProgressed.
            KStream<String, String> progressedStream = childTaskProgressedStream.groupByKey()
                    .reduce(((value1, value2) -> {
                        List<String> taskSteps = new ArrayList<>();
                        // read progressed message.
                        ChildTaskProgressedMessage v = new ChildTaskProgressedMessage();
                        try {
                            v = objectMapper.readValue(value1, ChildTaskProgressedMessage.class);
                            taskSteps = new ArrayList<>(v.getChildTaskSteps());
                        } catch (JsonProcessingException e) {
                            log.error(e.getMessage());
                            return null;
                        }

                        try {
                            if (value2 != null) {
                                v = objectMapper.readValue(value2, ChildTaskProgressedMessage.class);
                                taskSteps.addAll(v.getChildTaskSteps());
                            }
                        } catch (JsonProcessingException e) {
                            log.error(e.getMessage());
                            return null;
                        }

                        // write combined message.
                        String result;
                        try {
                            result = objectMapper.writeValueAsString(new ChildTaskProgressedMessage(v.getTransactionId(), taskSteps));
                        } catch (JsonProcessingException e) {
                            return null;
                        }
                        return result;
                    }), Materialized.as(TaskProcess.ChildTaskProgressed))
                    .toStream();

            // step2: check if allChildTaskCompleted.
            KStream<String, TaskCompletedMessage> completedStream = progressedStream.join(childTaskInitializedStream,
                    (leftValue, rightValue) -> {
                        ChildTaskProgressedMessage lValue = null;
                        ChildTaskInitializedMessage rValue = null;
                        try {
                            lValue = objectMapper.readValue(leftValue, ChildTaskProgressedMessage.class);
                            rValue = objectMapper.readValue(rightValue, ChildTaskInitializedMessage.class);
                        } catch (JsonProcessingException e) {
                            log.error(e.getMessage());
                            return null;
                        }
                        boolean allChildTaskCompleted = lValue.getChildTaskSteps().containsAll(rValue.getChildTaskSteps());
                        log.warn("kstream: {} allChildTaskCompleted: {}", lValue.getTransactionId(), allChildTaskCompleted);
                        return new TaskCompletedMessage(lValue.getTransactionId(), allChildTaskCompleted);
                    },
                    JoinWindows.of(Duration.ofDays(1))
            );

            completedStream.print(Printed.toSysOut());

            return completedStream;
        };
    }

    /**
     * Invoke TaskManager to update task status & notify subscriber that a Task Completed.
     *
     * @return A Consumer which defined (1 input 0 output)
     * input-0: TaskCompletedMessage Stream
     */
    @Bean
    public Consumer<KStream<String, TaskCompletedMessage>> process(@Autowired TaskManager taskManager) {
        return input -> {
            input.foreach((k, v) -> {
                TaskStatus taskStatus = v.isCompleted() ? TaskStatus.Completed : TaskStatus.InProgress;
                taskManager.update(k, taskStatus);
                log.warn("kstream TaskCompletedMessage: transactionId: {}, message: {}", k, v);
            });
        };
    }

    @Value("${app.kafka.partition}")
    private Integer partitions;

    @Value("${app.kafka.replica}")
    private Integer replicas;

    @Autowired
    KafkaProperties kafkaProperties;

    @Bean
    @ConditionalOnMissingBean(name = "kafkaListenerContainerFactory")
    ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory(ConcurrentKafkaListenerContainerFactoryConfigurer configurer, ObjectProvider<ConsumerFactory<Object, Object>> kafkaConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.getContainerProperties().setAsyncAcks(true);
        configurer.configure(factory, kafkaConsumerFactory.getIfAvailable(() -> new DefaultKafkaConsumerFactory(kafkaProperties.buildConsumerProperties())));
        return factory;
    }

    /*
     * Boot will autowire this into the container factory.
     */
    @Bean
    public SeekToCurrentErrorHandler errorHandler(KafkaTemplate<Object, Object> template) {
        return new SeekToCurrentErrorHandler(new DeadLetterPublishingRecoverer(template), new FixedBackOff(1000L, 2));
    }

    @Bean
    public JsonDeserializer<?> jsonDeserializer() {
        JsonDeserializer<?> deserializer = new JsonDeserializer<>();
        deserializer.addTrustedPackages("*");
        var typeMapper = new DefaultJackson2JavaTypeMapper();
        typeMapper.setTypePrecedence(Jackson2JavaTypeMapper.TypePrecedence.TYPE_ID);
        typeMapper.addTrustedPackages("*");
        Map<String, Class<?>> mappings = new HashMap<>();
        mappings.put("promotion", BatchPromotionMessage.class);
        mappings.put("subsidy", BatchSubsidyMessage.class);
        typeMapper.setIdClassMapping(mappings);
        deserializer.typeMapper(typeMapper);
        return deserializer;
    }

    @Bean
    public RecordMessageConverter converter() {
        ByteArrayJsonMessageConverter converter = new ByteArrayJsonMessageConverter();
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        typeMapper.setTypePrecedence(Jackson2JavaTypeMapper.TypePrecedence.TYPE_ID);
        typeMapper.addTrustedPackages("*");
        Map<String, Class<?>> mappings = new HashMap<>();
        mappings.put("promotion", BatchPromotionMessage.class);
        mappings.put("subsidy", BatchSubsidyMessage.class);
        typeMapper.setIdClassMapping(mappings);
        converter.setTypeMapper(typeMapper);
        return converter;
    }

    @Bean
    public NewTopic topicSubsidy() {
        return TopicBuilder.name(LongRunningProcess.BatchSubsidy).partitions(partitions).replicas(replicas).build();
    }

    @Bean
    public NewTopic topicSubsidyDlt() {
        return TopicBuilder.name(LongRunningProcess.BatchSubsidy + ".DLT").partitions(partitions).replicas(replicas).build();
    }

    @Bean
    public NewTopic topicPromotion() {
        return TopicBuilder.name(LongRunningProcess.BatchPromotion).partitions(partitions).replicas(replicas).build();
    }

    @Bean
    public NewTopic topicPromotionDlt() {
        return TopicBuilder.name(LongRunningProcess.BatchPromotion + ".DLT").partitions(partitions).replicas(replicas).build();
    }

    @Bean
    public NewTopic topicChildTaskInitialized() {
        return TopicBuilder.name(TaskProcess.ChildTaskInitialized).partitions(partitions).replicas(replicas).build();
    }

    @Bean
    public NewTopic topicChildTaskProgressed() {
        return TopicBuilder.name(TaskProcess.ChildTaskProgressed).partitions(partitions).replicas(replicas).build();
    }

    @Bean
    public NewTopic topicTaskCompleted() {
        return TopicBuilder.name(TaskProcess.TaskCompleted).partitions(partitions).replicas(replicas).build();
    }

    @Bean
    public NewTopic topicTaskCompletedDlt() {
        return TopicBuilder.name(TaskProcess.TaskCompleted + ".DLT").partitions(partitions).replicas(replicas).build();
    }

}
