package com.aegis.project.controller;

import com.aegis.project.model.PasswordResetToken;
import com.aegis.project.model.UserModel;
import com.aegis.project.repository.PasswordResetTokenRepository;
import com.aegis.project.repository.UserRepository;
import com.aegis.project.service.EmailService;
import com.aegis.project.service.TokenService;
import com.aegis.project.service.TwoFactorAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.aegis.project.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.transaction.annotation.Transactional;


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

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private TwoFactorAuthService twoFactorAuthService;

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

    @PostMapping("/requestPasswordReset")
    @Transactional
    public ResponseEntity<String> requestPasswordReset(@RequestParam String email) {
        logger.info("Received password reset request for email: {}", email);
        try {
            UserModel user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

            String existingToken = user.getPasswordResetToken();
            if (existingToken != null) {
                logger.info("Deleting existing password reset token for user: {}", email);
                passwordResetTokenRepository.deleteByToken(existingToken);
                user.setPasswordResetToken(null);
                userRepository.save(user);
            }

            String token = UUID.randomUUID().toString();
            PasswordResetToken resetToken = new PasswordResetToken(token, user.getUserID(), LocalDateTime.now().plusMinutes(15));
            passwordResetTokenRepository.save(resetToken);

            user.setPasswordResetToken(resetToken.getToken());
            userRepository.save(user);

            emailService.sendPasswordResetEmail(email, token, LocalDateTime.now().plusMinutes(15));
            return ResponseEntity.ok("Password reset requested successfully with token: " + token);
        } catch (RuntimeException e) {
            logger.error("Error requesting password reset: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Error requesting password reset");
        }
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<String> resetPassword(@RequestParam String token, @RequestParam String password) {
        logger.info("Received password reset request with token: {}", token);
        try {
            PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                    .orElseThrow(() -> new RuntimeException("Token not found: " + token));

            if (resetToken.isExpired()) {
                logger.warn("Token: {} has expired", token);
                return ResponseEntity.badRequest().body("Token has expired");
            }

            int userId = resetToken.getUserId();
            UserModel user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
            user.setPWHash(passwordEncoder.encode(password));
            user.setPasswordResetToken(null);
            userRepository.save(user);
            passwordResetTokenRepository.deleteById(resetToken.getId());

            return ResponseEntity.ok("Password reset successfully");
        } catch (RuntimeException e) {
            logger.error("Error resetting password: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Error resetting password");
        }
    }

    @PostMapping("/enable2FA")
    public ResponseEntity<String> enable2FA(@RequestParam int userID) {
        try {
            String secretKey = twoFactorAuthService.generateSecretKey();
            String qrCodeURL = twoFactorAuthService.getQRBarcodeURL("Aegis", secretKey);

            userService.updateUser2FA(userID, secretKey);
            return ResponseEntity.ok(qrCodeURL);
        } catch (RuntimeException e) {
            if (e.getMessage().equals("User not found with ID: " + userID)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with ID: " + userID);
            } else if (e.getMessage().contains("does not have permission")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User does not have permission to enable 2FA for user with ID: " + userID);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error enabling 2FA");
            }
        }
    }

    @PostMapping("/verify2FA")
    public ResponseEntity<String> verify2FA(@RequestParam int userID, @RequestParam int code) {
        try {
            UserModel user = userRepository.findById(userID)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + userID));
            String secretKey = user.getTwoFactorAuthInfo();
            if (secretKey == null) {
                return ResponseEntity.badRequest().body("2FA not enabled for user with ID: " + userID);
            }

            if (twoFactorAuthService.verifyCode(secretKey, code)) {
                return ResponseEntity.ok("2FA code verified successfully");
            } else {
                return ResponseEntity.badRequest().body("Invalid 2FA code");
            }
        } catch (RuntimeException e) {
            if (e.getMessage().equals("User not found with ID: " + userID)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with ID: " + userID);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error verifying 2FA code");
            }
        }
    }

    @PostMapping("/disable2FA")
    public ResponseEntity<String> disable2FA(@RequestParam int userID) {
        try {
            userService.updateUser2FA(userID, null);
            return ResponseEntity.ok("2FA disabled successfully");
        } catch (RuntimeException e) {
            if (e.getMessage().equals("User not found with ID: " + userID)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with ID: " + userID);
            } else if (e.getMessage().contains("does not have permission")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User does not have permission to disable 2FA for user with ID: " + userID);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error disabling 2FA");
            }
        }
    }

    @GetMapping("/qrCode")
    public ResponseEntity<byte[]> getQRCode(@RequestParam int userID) {
        try {
            UserModel user = userRepository.findById(userID)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + userID));
            String secretKey = user.getTwoFactorAuthInfo();
            if (secretKey == null) {
                return ResponseEntity.badRequest().body(null);
            }

            String qrCodeURL = twoFactorAuthService.getQRBarcodeURL("Aegis", secretKey);
            byte[] qrCodeImage = twoFactorAuthService.generateQRCodeImage(qrCodeURL);
            return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(qrCodeImage);
        } catch (Exception e) {
            if (e.getMessage().equals("User not found with ID: " + userID)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        }
    }
}
