package com.aegis.project.service;

import com.aegis.project.model.UserModel;
import com.aegis.project.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthFailureHandler implements AuthenticationFailureHandler {

  @Autowired
  private UserRepository userRepository;

  private static final int MAX_FAILED_ATTEMPTS = 3;

  @Override
  @Transactional
  public void onAuthenticationFailure(
    HttpServletRequest request,
    HttpServletResponse response,
    AuthenticationException exception
  ) throws IOException, ServletException {
    String email = request.getParameter("email");

    UserModel user = userRepository.findByEmail(email).orElse(null);

    if (user != null) {
      int failedAttempts = user.getFailedLoginAttempts() + 1;
      boolean isLocked = failedAttempts >= MAX_FAILED_ATTEMPTS;
      userRepository.updateFailedLoginAttempts(
        failedAttempts,
        isLocked,
        user.getUserID()
      );

      if (isLocked) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("Account locked");
        return;
      }
    }
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.getWriter().write("Login failed");
  }
}
