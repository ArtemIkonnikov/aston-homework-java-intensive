package ru.aston.notificationservice.email;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmailServiceTest {

    private final EmailService emailService = new EmailService();

    @Test
    void sendByOperation_create_buildsCreateMessage() {
        EmailMessage message = emailService.sendByOperation("rick@example.com", "CREATE");

        assertEquals("rick@example.com", message.getTo());
        assertTrue(message.getText().contains("успешно создан"));
    }

    @Test
    void sendByOperation_delete_buildsDeleteMessage() {
        EmailMessage message = emailService.sendByOperation("morty@example.com", "DELETE");

        assertEquals("morty@example.com", message.getTo());
        assertTrue(message.getText().contains("удалён"));
    }
}
