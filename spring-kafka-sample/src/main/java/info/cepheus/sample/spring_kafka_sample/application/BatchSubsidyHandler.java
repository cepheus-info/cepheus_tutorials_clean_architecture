package info.cepheus.sample.spring_kafka_sample.application;

import info.cepheus.sample.spring_kafka_sample.coreapi.LongRunningProcess;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(id = "subsidyGroup", topics = {LongRunningProcess.BatchSubsidy})
public class BatchSubsidyHandler {

    @KafkaHandler()
    public void on(BatchSubsidyDto foo, Acknowledgment acknowledgment) throws InterruptedException {
        System.out.println("Received subsidy: " + foo);
        Thread.sleep(10000);
        acknowledgment.acknowledge();
    }

    @KafkaHandler(isDefault = true)
    public void on(Object foo) {
        System.out.println("Received unknown: " + foo);
    }

}
