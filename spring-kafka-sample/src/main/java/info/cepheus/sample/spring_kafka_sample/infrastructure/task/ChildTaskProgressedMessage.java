package info.cepheus.sample.spring_kafka_sample.infrastructure.task;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ChildTaskProgressedMessage {
    private String transactionId;

    private List<String> childTaskSteps;
}
