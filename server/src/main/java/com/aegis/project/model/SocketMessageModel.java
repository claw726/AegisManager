package com.aegis.project.model;

import java.util.Objects;

public class SocketMessageModel {
    private String senderEmail;
    private String targetEmail;
    private String type;
    private String message;

    public SocketMessageModel() {
    }

    public SocketMessageModel(String senderEmail, String targetEmail, String type, String message) {
        this.senderEmail = senderEmail;
        this.targetEmail = targetEmail;
        this.type = type;
        this.message = message;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getTargetEmail() {
        return targetEmail;
    }

    public void setTargetEmail(String targetEmail) {
        this.targetEmail = targetEmail;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SocketMessageModel that = (SocketMessageModel) o;
        return Objects.equals(senderEmail, that.senderEmail) && Objects.equals(targetEmail, that.targetEmail) && Objects.equals(type, that.type) && Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(senderEmail, targetEmail, type, message);
    }

    @Override
    public String toString() {
        return "SocketMessageModel{" +
                "senderEmail='" + senderEmail + '\'' +
                ", targetEmail='" + targetEmail + '\'' +
                ", type='" + type + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}