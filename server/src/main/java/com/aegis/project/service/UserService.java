package com.aegis.project.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.aegis.project.dto.UserDTO;
import com.aegis.project.model.UserModel;
import com.aegis.project.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public boolean createUser(String email, String name, String password, String profilePicture) {
        logger.info("Attempting to create user with email: {}, name: {}", email, name);
        if (userRepository.existsByEmail(email)) {
            logger.warn("User with email: {} already exists", email);
            return false;
        }
        UserModel user = new UserModel(name, email, passwordEncoder.encode(password), profilePicture);

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

    public UserDTO getUserDTO(int userID) {
        logger.info("Getting user DTO for user ID: {}", userID);
        UserModel user = userRepository.findById(userID)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userID));
        UserDTO userDTO = new UserDTO(user.getUserID(), user.getUserName(), user.getEmail(), user.getProfilePicture());
        logger.info("Got user DTO for user ID: {}", userID);
        return userDTO;
    }

    public UserDTO getUserDTOByEmail(String email) {
        logger.info("Getting user DTO for user with email: {}", email);
        UserModel user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        UserDTO userDTO = new UserDTO(user.getUserID(), user.getUserName(), user.getEmail(), user.getProfilePicture());
        logger.info("Got user DTO for user with: {}", email);
        return userDTO;
    }

    public void updateFailedLoginAttempts(int failedLoginAttempts, boolean isLocked, int userID) {
        logger.info("Updating failed login attempts for user ID: {}. Attempts: {}, Locked: {}", userID, failedLoginAttempts, isLocked);
        userRepository.updateFailedLoginAttempts(failedLoginAttempts, isLocked, userID);
        logger.info("Updated failed login attempts for user ID: {}", userID);
    }

    public String getAllUsers() {
        List<UserModel> allUsers = userRepository.findAll();
        String ret = "{";
        for (UserModel user : allUsers) {
            if (ret.length() > 1) {
                ret += ",";
            }
            ret += createUserJson(user);
        }
        ret += "}";
        return ret;
    }

    public String createUserJson(UserModel user) {
        return "{"
                + "\"userID\": " + user.getUserID() + ","
                + "\"userName\": " + user.getUserName() + ","
                + "\"email\": " + user.getEmail() + "\""
                + "}";
    }
}
