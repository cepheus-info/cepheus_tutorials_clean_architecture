package info.cepheus.sample.spring_kafka_sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@EnableKafkaStreams
public class SpringKafkaSampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringKafkaSampleApplication.class, args);
    }

}
