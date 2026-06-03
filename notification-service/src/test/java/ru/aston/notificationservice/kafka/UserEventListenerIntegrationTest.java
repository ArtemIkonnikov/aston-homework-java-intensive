package ru.aston.notificationservice.kafka;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import ru.aston.common.UserEventDto;
import ru.aston.notificationservice.email.EmailMessage;
import ru.aston.notificationservice.email.EmailService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = "user-events")
class UserEventListenerIntegrationTest {

    @Autowired
    private KafkaTemplate<String, UserEventDto> kafkaTemplate;

    @MockitoSpyBean
    private EmailService emailService;

    @Test
    void createEvent_buildsCreateEmail() {
        kafkaTemplate.send("user-events", new UserEventDto("CREATE", "rick@example.com"));

        ArgumentCaptor<EmailMessage> captor = ArgumentCaptor.forClass(EmailMessage.class);
        verify(emailService, timeout(10000)).send(captor.capture());

        EmailMessage message = captor.getValue();
        assertEquals("rick@example.com", message.getTo());
        assertTrue(message.getText().contains("успешно создан"));
    }

    @Test
    void deleteEvent_buildsDeleteEmail() {
        kafkaTemplate.send("user-events", new UserEventDto("DELETE", "morty@example.com"));

        ArgumentCaptor<EmailMessage> captor = ArgumentCaptor.forClass(EmailMessage.class);
        verify(emailService, timeout(10000)).send(captor.capture());

        EmailMessage message = captor.getValue();
        assertEquals("morty@example.com", message.getTo());
        assertTrue(message.getText().contains("удалён"));
    }
}
