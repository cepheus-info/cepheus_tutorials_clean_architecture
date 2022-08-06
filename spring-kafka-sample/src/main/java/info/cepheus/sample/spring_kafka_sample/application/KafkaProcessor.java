package info.cepheus.sample.spring_kafka_sample.application;

import info.cepheus.sample.spring_kafka_sample.coreapi.LongRunningProcess;
import org.apache.kafka.common.serialization.*;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.kafka.support.serializer.StringOrBytesSerializer;

import java.util.Collections;

public class KafkaProcessor {

    private Serializer<Object> stringOrBytesSerializer = new StringOrBytesSerializer();

    @Autowired
    private Deserializer<Object> byteArrayDeserializer;

    @Autowired
    private RecordMessageConverter converter;

    @Autowired
    public void process(StreamsBuilder builder) {

        final Serde<Integer> integerSerde = Serdes.Integer();
        final Serde<String> stringSerde = Serdes.String();
        final Serde<Long> longSerde = Serdes.Long();

        KStream<String, Object> batchSubsidyDtoKStream = builder.stream(LongRunningProcess.BatchSubsidy,
                Consumed.with(Serdes.String(), Serdes.serdeFrom(stringOrBytesSerializer, byteArrayDeserializer))
        );

        KTable<String, Integer> personCountStore = batchSubsidyDtoKStream
                .flatMapValues(value -> {
                    BatchSubsidyMessage obj = (BatchSubsidyMessage) value;
                    return Collections.singletonList(obj.getIds().size());
                }).toTable(Materialized.as("kPersonCountStore"));

        personCountStore.toStream().to(LongRunningProcess.Result, Produced.with(stringSerde, integerSerde));
    }
}
