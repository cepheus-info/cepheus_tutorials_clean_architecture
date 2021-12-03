package info.cepheus.sample.spring_kafka_sample.application;

import info.cepheus.sample.spring_kafka_sample.coreapi.LongRunningProcess;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(id = "promotionGroup", topics = {LongRunningProcess.BatchPromotion})
public class BatchPromotionHandler {

    @KafkaHandler
    public void on(BatchPromotionDto foo) {
        System.out.println("Received promotion: " + foo);
    }

    @KafkaHandler(isDefault = true)
    public void on(Object foo) {
        System.out.println("Received unknown: " + foo);
    }
}
