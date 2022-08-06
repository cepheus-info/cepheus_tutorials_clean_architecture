package info.cepheus.sample.spring_kafka_sample.infrastructure.task;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TaskCompletedMessage {
    private String transactionId;

    private boolean completed;
}
