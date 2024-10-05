package com.aegis.project.service;

import com.aegis.project.model.UserModel;
import com.aegis.project.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public boolean createUser(String email, String name, String password) {
        logger.info("Attempting to create user with email: {}, name: {}", email, name);
        if (userRepository.existsByEmail(email)) {
            logger.warn("User with email: {} already exists", email);
            return false;
        }
        UserModel user = new UserModel(name, email, passwordEncoder.encode(password));

        userRepository.save(user);
        logger.info("User with email: {} created successfully", email);
        return true;
    }

    public Optional<UserModel> findUserByEmail(String email) {
        logger.info("Searching for user with email: {}", email);
        Optional<UserModel> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            logger.info("User found with email: {}", email);
        } else {
            logger.warn("No user found with email: {}", email);
        }
        return user;
    }

    public void updateFailedLoginAttempts(int failedLoginAttempts, boolean isLocked, int userID) {
        logger.info("Updating failed login attempts for user ID: {}. Attempts: {}, Locked: {}", userID, failedLoginAttempts, isLocked);
        userRepository.updateFailedLoginAttempts(failedLoginAttempts, isLocked, userID);
        logger.info("Updated failed login attempts for user ID: {}", userID);
    }
}
