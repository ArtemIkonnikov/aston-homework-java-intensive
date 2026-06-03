package ru.aston.notificationservice.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.aston.common.UserEventDto;
import ru.aston.notificationservice.email.EmailService;

@Component
public class UserEventListener {

    private static final Logger log = LoggerFactory.getLogger(UserEventListener.class);

    private final EmailService emailService;

    public UserEventListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(topics = "user-events", groupId = "notification-service")
    public void onUserEvent(UserEventDto event) {
        log.info("Received event from Kafka: {}", event);
        emailService.sendByOperation(event.getEmail(), event.getOperation());
    }
}
