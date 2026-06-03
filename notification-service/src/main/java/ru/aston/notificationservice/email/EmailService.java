package ru.aston.notificationservice.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private static final String CREATE_TEXT =
            "Здравствуйте! Ваш аккаунт на сайте был успешно создан.";
    private static final String DELETE_TEXT =
            "Здравствуйте! Ваш аккаунт был удалён.";

    public EmailMessage sendByOperation(String email, String operation) {
        String text;
        if ("DELETE".equals(operation)) {
            text = DELETE_TEXT;
        } else {
            text = CREATE_TEXT;
        }
        EmailMessage message = new EmailMessage(email, text);
        send(message);
        return message;
    }

    public void send(EmailMessage message) {
        log.info("Отправка письма на {}: {}", message.getTo(), message.getText());
    }
}
