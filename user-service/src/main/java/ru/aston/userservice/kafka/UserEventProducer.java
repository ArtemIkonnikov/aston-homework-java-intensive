package ru.aston.userservice.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.aston.common.UserEventDto;

@Component
public class UserEventProducer {

    private static final Logger log = LoggerFactory.getLogger(UserEventProducer.class);
    private static final String TOPIC = "user-events";

    private final KafkaTemplate<String, UserEventDto> kafkaTemplate;

    public UserEventProducer(KafkaTemplate<String, UserEventDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(String operation, String email) {
        UserEventDto event = new UserEventDto(operation, email);
        kafkaTemplate.send(TOPIC, event);
        log.info("Sent event to Kafka: operation={}, email={}", operation, email);
    }
}
