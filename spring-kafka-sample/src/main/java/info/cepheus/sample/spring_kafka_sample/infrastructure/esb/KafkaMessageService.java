package info.cepheus.sample.spring_kafka_sample.infrastructure.esb;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaMessageService {

    @Autowired
    private KafkaMessageRepository repository;

    public boolean isDuplicated(String messageIdentifier) {
        if (repository.existsById(messageIdentifier)) {
            log.warn("Received duplicated message identifier {}, ignored", messageIdentifier);
            return true;
        }
        repository.save(new KafkaMessage(messageIdentifier));
        return false;
    }
}
