package com.aegis.project.controller;

import com.aegis.project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public ResponseEntity<String> createUser(String email, String name, String password) {
        if (userService.createUser(email, name, password)) {
            return ResponseEntity.ok("User created successfully");
        } else {
            return ResponseEntity.badRequest().body("User already exists or there was an error");
        }
    }
}
