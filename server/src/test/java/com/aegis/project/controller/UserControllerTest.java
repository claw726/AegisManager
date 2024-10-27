package com.aegis.project.controller;

import com.aegis.project.AegisApplication;
import com.aegis.project.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = AegisApplication.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

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
    public void testCreateUser_Failure() {
        String email = "test@example.com";
        String name = "Test User";
        String password = "password";
        String profilePicture = "encodedImage";

        when(userService.createUser(email, name, password, profilePicture)).thenReturn(false);

        ResponseEntity<String> response = authController.createUser(email, name, password, profilePicture);

        assertNotEquals(ResponseEntity.ok("User created successfully"), response);
    }
}