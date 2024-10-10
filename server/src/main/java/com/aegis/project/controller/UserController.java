package com.aegis.project.controller;

import com.aegis.project.service.UserService;
import com.aegis.project.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/getUser")
    public ResponseEntity<String> getUser(@RequestParam String email) {
        try {
            UserModel user = userService.findUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
            ;
            return ResponseEntity.ok(userService.createUserJson(user));
        }
        catch (RuntimeException e) {
            if (e.getMessage().equals("User not found with email: " + email)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        }
    }

    @GetMapping("/getAllUsers")
    public ResponseEntity<String> getAllUsers() {
        try {
            return ResponseEntity.ok(userService.getAllUsers());
        }
        catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
