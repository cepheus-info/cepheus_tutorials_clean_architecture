package info.cepheus.sample.spring_kafka_sample.api;

import info.cepheus.sample.spring_kafka_sample.application.BatchPromotionMessage;
import info.cepheus.sample.spring_kafka_sample.application.BatchSubsidyMessage;
import info.cepheus.sample.spring_kafka_sample.coreapi.LongRunningProcess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController("/api/person")
@RequestMapping(value = "/api/person", produces = MediaType.APPLICATION_JSON_VALUE)
public class PersonController {

    @Autowired
    private KafkaTemplate<Object, Object> kafkaTemplate;
//    @Autowired
//    private StreamsBuilderFactoryBean factoryBean;

    @PostMapping(value = "/subsidy/:batchUpdate")
    @ResponseBody
    public ResponseEntity<String> batchSubsidyUpdate(@RequestBody BatchSubsidyMessage batchSubsidyDto) {
        String transactionId = UUID.randomUUID().toString();
        batchSubsidyDto.setTransactionId(transactionId);
        this.kafkaTemplate.send(LongRunningProcess.BatchSubsidy, transactionId, batchSubsidyDto);
        return ResponseEntity.accepted().body(transactionId);
    }

    @PostMapping(value = "/promotion/:batchPromote")
    @ResponseBody
    public ResponseEntity<String> batchPromote(@RequestBody BatchPromotionMessage batchPromotionDto) {
        String transactionId = UUID.randomUUID().toString();
        this.kafkaTemplate.send(LongRunningProcess.BatchPromotion, transactionId, batchPromotionDto);
        return ResponseEntity.accepted().body(transactionId);
    }

//    @GetMapping
//    @ResponseBody
//    public ResponseEntity subsidy() {
//        KafkaStreams kafkaStreams = factoryBean.getKafkaStreams();
//        ReadOnlyKeyValueStore<Object, Object> store = kafkaStreams.store(StoreQueryParameters.fromNameAndType("kPersonCountStore", QueryableStoreTypes.keyValueStore()));
//        return ResponseEntity.ok(store.get("string"));
//    }
}
