package ru.aston.notificationservice.controller;

public class NotificationRequest {

    private String email;
    private String operation;

    public NotificationRequest() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }
}
