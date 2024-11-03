package com.aegis.project.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

  @Autowired
  private final JavaMailSender mailSender;

  public EmailService(JavaMailSender mailSender) {
    this.mailSender = mailSender;
  }

  public void sendPasswordResetEmail(
    String email,
    String token,
    LocalDateTime expiryDate
  ) {
    String subject = "Password Reset Request";
    String content = loadEmailTemplate(
      "src/main/resources/templates/password-reset-email.html"
    );

    if (content != null) {
      content = content.replace("{{token}}", token);
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
      return new String(Files.readAllBytes(Paths.get(filePath)));
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }
}
