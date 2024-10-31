package com.aegis.project.controller;

import com.aegis.project.AegisApplication;
import com.aegis.project.model.PasswordResetToken;
import com.aegis.project.model.UserModel;
import com.aegis.project.service.UserService;
import com.aegis.project.service.TokenService;
import com.aegis.project.service.EmailService;
import com.aegis.project.service.TwoFactorAuthService;
import com.aegis.project.repository.UserRepository;
import com.aegis.project.repository.PasswordResetTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class AuthControllerTest {

    @Mock
    private AuthenticationManager authManager;

    @Mock
    private TokenService tokenService;

    @Mock
    private UserService userService;

    @Mock
    private EmailService emailService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private TwoFactorAuthService twoFactorAuthService;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateUser_Success() {
        String email = "test@example.com";
        String name = "Test User";
        String password = "password";
        String profilePicture = "encodedImage";

        when(userService.createUser(email, name, password, profilePicture)).thenReturn(true);

        ResponseEntity<String> response = authController.createUser(email, name, password, profilePicture);

        assertEquals(ResponseEntity.ok("User created successfully"), response);
    }

    @Test
    public void testCreateUser_Conflict() {
        String email = "test@example.com";
        String name = "Test User";
        String password = "password";
        String profilePicture = "encodedImage";

        when(userService.createUser(email, name, password, profilePicture)).thenReturn(false);

        ResponseEntity<String> response = authController.createUser(email, name, password, profilePicture);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("User already exists", response.getBody());
    }

    @Test
    public void testLoginUser_Success() {
        String email = "test@example.com";
        String password = "password";
        Authentication auth = mock(Authentication.class);
        String token = "jwtToken";

        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(tokenService.generateToken(auth)).thenReturn(token);

        ResponseEntity<Map<String, String>> response = authController.loginUser(email, password);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Login successful", response.getBody().get("message"));
        assertEquals(token, response.getBody().get("token"));
    }

    @Test
    public void testLoginUser_InvalidPassword() {
        String email = "test@example.com";
        String password = "password";

        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid password") {});

        ResponseEntity<Map<String, String>> response = authController.loginUser(email, password);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid password", response.getBody().get("message"));
    }

    @Test
    public void testRequestPasswordReset_Success() {
        String email = "test@example.com";
        UserModel user = new UserModel();
        user.setEmail(email);
        user.setUserID(1);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordResetTokenRepository.save(any(PasswordResetToken.class))).thenReturn(new PasswordResetToken());

        ResponseEntity<String> response = authController.requestPasswordReset(email);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testRequestPasswordReset_UserNotFound() {
        String email = "test@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        ResponseEntity<String> response = authController.requestPasswordReset(email);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assert(response.getBody().contains("User not found with email: " + email));
    }

    @Test
    public void testResetPassword_Success() {
        String token = UUID.randomUUID().toString();
        String password = "newPassword";
        PasswordResetToken resetToken = new PasswordResetToken(token, 1, LocalDateTime.now().plusMinutes(15));
        UserModel user = new UserModel();
        user.setUserID(1);

        when(passwordResetTokenRepository.findByToken(token)).thenReturn(Optional.of(resetToken));
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");
        doNothing().when(passwordResetTokenRepository).deleteById(resetToken.getId());

        ResponseEntity<String> response = authController.resetPassword(token, password);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Password reset successfully", response.getBody());
    }

    @Test
    public void testResetPassword_TokenNotFound() {
        String token = UUID.randomUUID().toString();
        String password = "newPassword";

        when(passwordResetTokenRepository.findByToken(token)).thenReturn(Optional.empty());

        ResponseEntity<String> response = authController.resetPassword(token, password);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Password reset token not found", response.getBody());
    }

    @Test
    public void testEnable2FA_Success() {
        int userID = 1;
        String secretKey = "secretKey";
        String qrCodeURL = "qrCodeURL";

        when(twoFactorAuthService.generateSecretKey()).thenReturn(secretKey);
        when(twoFactorAuthService.getQRBarcodeURL(userID, secretKey)).thenReturn(qrCodeURL);
        doNothing().when(userService).updateUser2FA(userID, secretKey);

        ResponseEntity<String> response = authController.enable2FA(userID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(qrCodeURL, response.getBody());
    }

    @Test
    public void testEnable2FA_UserNotFound() {
        int userID = 1;

        doThrow(new RuntimeException("User not found with ID: " + userID)).when(userService).updateUser2FA(userID, null);

        ResponseEntity<String> response = authController.enable2FA(userID);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found with ID: " + userID, response.getBody());
    }

    @Test
    public void testVerify2FA_Success() {
        int userID = 1;
        int code = 123456;
        UserModel user = new UserModel();
        user.setUserID(userID);
        user.setTwoFactorAuthInfo("secretKey");

        when(userRepository.findById(userID)).thenReturn(Optional.of(user));
        when(twoFactorAuthService.verifyCode("secretKey", code)).thenReturn(true);

        ResponseEntity<String> response = authController.verify2FA(userID, code);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("2FA code verified successfully", response.getBody());
    }

    @Test
    public void testVerify2FA_InvalidCode() {
        int userID = 1;
        int code = 123456;
        UserModel user = new UserModel();
        user.setUserID(userID);
        user.setTwoFactorAuthInfo("secretKey");

        when(userRepository.findById(userID)).thenReturn(Optional.of(user));
        when(twoFactorAuthService.verifyCode("secretKey", code)).thenReturn(false);

        ResponseEntity<String> response = authController.verify2FA(userID, code);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid 2FA code", response.getBody());
    }

    @Test
    public void testDisable2FA_Success() {
        int userID = 1;

        doNothing().when(userService).updateUser2FA(userID, null);

        ResponseEntity<String> response = authController.disable2FA(userID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("2FA disabled successfully", response.getBody());
    }

    @Test
    public void testDisable2FA_UserNotFound() {
        int userID = 1;

        doThrow(new RuntimeException("User not found with ID: " + userID)).when(userService).updateUser2FA(userID, null);

        ResponseEntity<String> response = authController.disable2FA(userID);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found with ID: " + userID, response.getBody());
    }
}