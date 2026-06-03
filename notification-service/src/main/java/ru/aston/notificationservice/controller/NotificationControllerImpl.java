package ru.aston.notificationservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.aston.notificationservice.email.EmailService;

@RestController
public class NotificationControllerImpl implements NotificationController {

    private final EmailService emailService;

    public NotificationControllerImpl(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public ResponseEntity<Void> send(NotificationRequest request) {
        emailService.sendByOperation(request.getEmail(), request.getOperation());
        return ResponseEntity.ok().build();
    }
}
