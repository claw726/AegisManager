package com.aegis.project.controller;

import com.aegis.project.dto.UserDTO;
import com.aegis.project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{userID}")
    public ResponseEntity<UserDTO> getUser(@PathVariable int userID) {
        try {
            return ResponseEntity.ok(userService.getUserDTO(userID));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("User not found with ID: " + userID)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        }
    }
}
