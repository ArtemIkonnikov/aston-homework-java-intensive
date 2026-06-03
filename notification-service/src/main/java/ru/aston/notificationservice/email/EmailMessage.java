package ru.aston.notificationservice.email;

public class EmailMessage {

    private final String to;
    private final String text;

    public EmailMessage(String to, String text) {
        this.to = to;
        this.text = text;
    }

    public String getTo() {
        return to;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "EmailMessage{to='" + to + "', text='" + text + "'}";
    }
}
