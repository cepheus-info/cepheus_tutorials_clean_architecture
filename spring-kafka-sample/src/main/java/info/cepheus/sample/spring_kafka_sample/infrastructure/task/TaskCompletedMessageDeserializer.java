package info.cepheus.sample.spring_kafka_sample.infrastructure.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
public class TaskCompletedMessageDeserializer implements Deserializer<TaskCompletedMessage> {

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Configure this class.
     *
     * @param configs configs in key/value pairs
     * @param isKey   whether is for key or value
     */
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        Deserializer.super.configure(configs, isKey);
    }

    /**
     * Deserialize a record value from a byte array into a value or object.
     *
     * @param topic topic associated with the data
     * @param data  serialized bytes; may be null; implementations are recommended to handle null by returning a value or null rather than throwing an exception.
     * @return deserialized typed data; may be null
     */
    @Override
    public TaskCompletedMessage deserialize(String topic, byte[] data) {
        try {
            TaskCompletedMessage message = objectMapper.readValue(data, TaskCompletedMessage.class);
            return message;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Deserialize a record value from a byte array into a value or object.
     *
     * @param topic   topic associated with the data
     * @param headers headers associated with the record; may be empty.
     * @param data    serialized bytes; may be null; implementations are recommended to handle null by returning a value or null rather than throwing an exception.
     * @return deserialized typed data; may be null
     */
    @Override
    public TaskCompletedMessage deserialize(String topic, Headers headers, byte[] data) {
        try {
            TaskCompletedMessage message = objectMapper.readValue(data, TaskCompletedMessage.class);
            return message;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Close this deserializer.
     * <p>
     * This method must be idempotent as it may be called multiple times.
     */
    @Override
    public void close() {
        Deserializer.super.close();
    }
}
