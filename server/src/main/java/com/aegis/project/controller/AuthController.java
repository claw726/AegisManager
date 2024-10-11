package com.aegis.project.controller;

import com.aegis.project.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import com.aegis.project.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserService userService;

    // Registration endpoint
    @PostMapping("/register")
    public ResponseEntity<String> createUser(@RequestParam String email, @RequestParam String name, @RequestParam String password, @RequestParam String profilePicture) {
        // Log the input parameters
        logger.info("Received registration request with email: {}, name: {}, password: {}, profilePicture: {}", email, name, password, profilePicture);
        try {
            if (userService.createUser(email, name, password, profilePicture)) {
                logger.info("User created successfully for email: {}", email);
                return ResponseEntity.ok("User created successfully");
            } else {
                logger.warn("User already exists with email: {}", email);
                return ResponseEntity.badRequest().body("User already exists");
            }
        } catch (Exception e) {
            logger.error("Error creating user: " + e.getMessage());
            return ResponseEntity.internalServerError().body("There was an error creating the user");
        }
    }

    // Login endpoint
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(@RequestParam String email, @RequestParam String password) {
        Map<String, String> response = new HashMap<>();

        logger.info("Recieved Login Request from: " + email);

        try {
            Authentication auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

            String token = tokenService.generateToken(auth);

            response.put("message", "Login successful");
            response.put("token", token);

            logger.info("Sending response to user: " + response);

            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            response.put("message", "Login failed");
            logger.error("Error authenticating user: " + email + " Error message: " + e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }
}
