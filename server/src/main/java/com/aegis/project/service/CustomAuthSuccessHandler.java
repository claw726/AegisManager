package com.aegis.project.service;

import com.aegis.project.model.UserModel;
import com.aegis.project.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthSuccessHandler implements AuthenticationSuccessHandler {

  @Autowired
  private UserRepository userRepository;

  @Override
  public void onAuthenticationSuccess(
    HttpServletRequest request,
    HttpServletResponse response,
    Authentication authentication
  ) throws IOException {
    String email = request.getParameter("email");

    UserModel user = userRepository.findByEmail(email).orElse(null);

    if (user != null) {
      userRepository.updateFailedLoginAttempts(0, false, user.getUserID());
    }

    response.setStatus(HttpServletResponse.SC_OK);
    response.getWriter().write("Login successful");
  }
}
