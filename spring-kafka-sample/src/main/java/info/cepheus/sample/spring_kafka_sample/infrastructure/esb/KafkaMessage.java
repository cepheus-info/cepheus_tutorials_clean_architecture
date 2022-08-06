package info.cepheus.sample.spring_kafka_sample.infrastructure.esb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class KafkaMessage {

    @Id
    private String id;

}
