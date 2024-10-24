package com.aegis.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class EmailService {
    @Autowired
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendPasswordResetEmail(String email, String token, LocalDateTime expiryDate) {
        String subject = "Password Reset Request";
        String resetUrl = "http://localhost:8081/reset-password?token=" + token;
        String content = loadEmailTemplate("src/main/resources/templates/password-reset-email.html");

        if (content != null) {
            content = content.replace("{{token}}", token);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm");
            String formattedExpiryDate = expiryDate.format(formatter);

            MimeMessage message = mailSender.createMimeMessage();
            try {
                MimeMessageHelper helper = new MimeMessageHelper(message, true);
                helper.setTo(email);
                helper.setSubject(subject);
                helper.setText(content, true);

                mailSender.send(message);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
    }

    private String loadEmailTemplate(String filePath) {
        try {
            return new String (Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
