package info.cepheus.sample.spring_kafka_sample.config;

import info.cepheus.sample.spring_kafka_sample.SpringKafkaSampleApplication;
import info.cepheus.sample.spring_kafka_sample.application.BatchPromotionDto;
import info.cepheus.sample.spring_kafka_sample.application.BatchSubsidyDto;
import info.cepheus.sample.spring_kafka_sample.coreapi.LongRunningProcess;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.SeekToCurrentErrorHandler;
import org.springframework.kafka.support.converter.ByteArrayJsonMessageConverter;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.kafka.support.mapping.DefaultJackson2JavaTypeMapper;
import org.springframework.kafka.support.mapping.Jackson2JavaTypeMapper;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class KafkaConfiguration {
    @Value("${app.kafka.partition}")
    private Integer partitions;

    @Value("${app.kafka.replica}")
    private Integer replicas;

    private ExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    /*
     * Boot will autowire this into the container factory.
     */
    @Bean
    public SeekToCurrentErrorHandler errorHandler(KafkaOperations<Object, Object> template) {
        return new SeekToCurrentErrorHandler(
                new DeadLetterPublishingRecoverer(template), new FixedBackOff(1000L, 2));
    }

    @Bean
    public JsonDeserializer<?> jsonDeserializer() {
        JsonDeserializer<?> deserializer = new JsonDeserializer<>();
        deserializer.addTrustedPackages("*");
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        typeMapper.setTypePrecedence(Jackson2JavaTypeMapper.TypePrecedence.TYPE_ID);
        typeMapper.addTrustedPackages("*");
        Map<String, Class<?>> mappings = new HashMap<>();
        mappings.put("promotion", BatchPromotionDto.class);
        mappings.put("subsidy", BatchSubsidyDto.class);
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
        mappings.put("promotion", BatchPromotionDto.class);
        mappings.put("subsidy", BatchSubsidyDto.class);
        typeMapper.setIdClassMapping(mappings);
        converter.setTypeMapper(typeMapper);
        return converter;
    }

    @Bean
    public NewTopic topicSubsidy() {
        return TopicBuilder.name(LongRunningProcess.BatchSubsidy)
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }

    @Bean
    public NewTopic topicSubsidyDlt() {
        return TopicBuilder.name(LongRunningProcess.BatchSubsidy + ".DLT")
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }

    @Bean
    public NewTopic topicPromotion() {
        return TopicBuilder.name(LongRunningProcess.BatchPromotion)
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }

    @Bean
    public NewTopic topicPromotionDlt() {
        return TopicBuilder.name(LongRunningProcess.BatchPromotion + ".DLT")
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }

    @Bean
    public NewTopic topicResult() {
        return TopicBuilder.name(LongRunningProcess.Result)
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }

    @Bean
    public NewTopic topicResultDlt() {
        return TopicBuilder.name(LongRunningProcess.Result + ".DLT")
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }

}
