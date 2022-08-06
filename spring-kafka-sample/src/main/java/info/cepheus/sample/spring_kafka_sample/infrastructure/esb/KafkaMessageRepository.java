package info.cepheus.sample.spring_kafka_sample.infrastructure.esb;

import org.springframework.data.jpa.repository.JpaRepository;

public interface KafkaMessageRepository extends JpaRepository<KafkaMessage, String> {
}
