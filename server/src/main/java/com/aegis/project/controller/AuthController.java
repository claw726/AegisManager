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

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserService userService;

    // Registration endpoint
    @PostMapping("/register")
    public ResponseEntity<String> createUser(String email, String name, String password) {
        if (userService.createUser(email, name, password)) {
            return ResponseEntity.ok("User created successfully");
        } else {
            return ResponseEntity.badRequest().body("User already exists or there was an error");
        }
    }

    // Login endpoint
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(@RequestParam String email, @RequestParam String password) {
        Map<String, String> response = new HashMap<>();

        try {
            Authentication auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

            String token = tokenService.generateToken(auth);

            response.put("message:", "Login successful");
            response.put("token", token);

            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            response.put("message", "Login failed");

            return ResponseEntity.badRequest().body(response);
        }
    }
}
