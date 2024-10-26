package com.aegis.project.controller;

import com.aegis.project.dto.UserDTO;
import com.aegis.project.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


@RestController
@RequestMapping("api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);


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

    @GetMapping("/getUserByEmail")
    public ResponseEntity<UserDTO> getUserByEmail(@RequestParam String email) {
        logger.info("Recieved Request for user: " + email);
        try {
            return ResponseEntity.ok(userService.getUserDTOByEmail(email));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("User not found with email: " + email)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        }
    }

    @GetMapping("/getAllUsers")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        try {
            return ResponseEntity.ok(userService.getAllUsers());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{userID}/update")
    public ResponseEntity<String> updateUser(@PathVariable int userID, @RequestParam String name, @RequestParam String email, @RequestParam String profilePicture) {
        try {
            userService.updateUser(userID, name, email, profilePicture);
            return ResponseEntity.ok("User updated successfully");
        } catch (RuntimeException e) {
            if (e.getMessage().equals("User not found with ID: " + userID)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else if (e.getMessage().equals("User with email: " + email + " already exists")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            } else if (e.getMessage().equals("User with email: " + email + " does not have permission to update user with ID: " + userID)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }
        }
    }
}
