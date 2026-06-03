package ru.aston.notificationservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/notifications")
public interface NotificationController {

    @PostMapping
    ResponseEntity<Void> send(@RequestBody NotificationRequest request);
}
