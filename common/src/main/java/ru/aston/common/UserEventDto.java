package ru.aston.common;

public class UserEventDto {

    private String operation;
    private String email;

    public UserEventDto() {
    }

    public UserEventDto(String operation, String email) {
        this.operation = operation;
        this.email = email;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "UserEventDto{operation='" + operation + "', email='" + email + "'}";
    }
}
