package info.cepheus.sample.spring_kafka_sample.application;

import info.cepheus.sample.spring_kafka_sample.coreapi.LongRunningProcess;
import info.cepheus.sample.spring_kafka_sample.infrastructure.esb.KafkaMessageService;
import info.cepheus.sample.spring_kafka_sample.infrastructure.task.TaskManager;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@KafkaListener(id = "promotionGroup", topics = {LongRunningProcess.BatchPromotion, LongRunningProcess.BatchPromotion + ".DLT"})
public class BatchPromotionHandler {

    @Autowired
    KafkaMessageService kafkaMessageService;

    @Autowired
    TaskManager taskManager;

    @SneakyThrows
    @KafkaHandler
    @Transactional
    public void on(BatchPromotionMessage message, Acknowledgment acknowledgment) {
        System.out.println("Received promotion: " + message);
        log.info("onBatchBasicInfoMessage received: {}", message.getTransactionId());
        if (kafkaMessageService.isDuplicated(message.getTransactionId())) {
            log.info("onBatchBasicInfoMessage received duplicated message: {}", message.getTransactionId());
            acknowledgment.acknowledge();
            return;
        }

        // TaskManager step1: InProgress Task
        taskManager.inProgress(message.getTransactionId());

        // TaskManager step2: Initialize ChildTasks.
        taskManager.initializeChildSteps(message.getTransactionId(), message.getIds());

        // TaskManager step3: Acknowledge when task completed.
        taskManager.acknowledgeWhenCompletion(message.getTransactionId(), acknowledgment);
    }

    @KafkaHandler(isDefault = true)
    public void on(Object foo) {
        System.out.println("Received unknown: " + foo);
    }
}
