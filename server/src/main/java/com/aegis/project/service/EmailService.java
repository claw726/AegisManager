package com.aegis.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendPasswordResetEmail(String email, String token, LocalDateTime expiryDate) {
        String subject = "Password Reset Request";
        String resetUrl = "http://localhost:8081/reset-password?token=" + token;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm");
        String formattedExpiryDate = expiryDate.format(formatter);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(subject);
        message.setText("To reset your password, click the link below:\n" + resetUrl
                + "\n\nThis link will expire on: " + formattedExpiryDate);

        mailSender.send(message);
    }
}
