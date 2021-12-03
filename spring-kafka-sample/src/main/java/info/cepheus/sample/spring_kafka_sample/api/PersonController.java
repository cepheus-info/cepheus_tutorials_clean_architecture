package info.cepheus.sample.spring_kafka_sample.api;

import info.cepheus.sample.spring_kafka_sample.application.BatchPromotionDto;
import info.cepheus.sample.spring_kafka_sample.application.BatchSubsidyDto;
import info.cepheus.sample.spring_kafka_sample.coreapi.LongRunningProcess;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.QueryableStoreType;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController("/api/person")
@RequestMapping(value = "/api/person", produces = MediaType.APPLICATION_JSON_VALUE)
public class PersonController {

    @Autowired
    private KafkaTemplate<Object, Object> template;
//
//    @Autowired
//    private StreamsBuilderFactoryBean factoryBean;

    @PostMapping(value = "/subsidy/:batchUpdate")
    @ResponseBody
    public ResponseEntity<String> batchSubsidyUpdate(@RequestBody BatchSubsidyDto batchSubsidyDto) {
        String id = UUID.randomUUID().toString();
        this.template.send(LongRunningProcess.BatchSubsidy, id, batchSubsidyDto);
        return ResponseEntity.accepted().body(id);
    }

    @PostMapping(value = "/promotion/:batchPromote")
    @ResponseBody
    public ResponseEntity<String> batchPromote(@RequestBody BatchPromotionDto batchPromotionDto) {
        String id = UUID.randomUUID().toString();
        this.template.send(LongRunningProcess.BatchPromotion, id, batchPromotionDto);
        return ResponseEntity.accepted().body(id);
    }

//    @GetMapping
//    @ResponseBody
//    public ResponseEntity subsidy() {
//        KafkaStreams kafkaStreams = factoryBean.getKafkaStreams();
//        ReadOnlyKeyValueStore<Object, Object> store = kafkaStreams.store(StoreQueryParameters.fromNameAndType("kPersonCountStore", QueryableStoreTypes.keyValueStore()));
//        return ResponseEntity.ok(store.get("string"));
//    }
}
