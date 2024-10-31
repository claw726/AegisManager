package com.aegis.project.controller;

import com.aegis.project.AegisApplication;
import com.aegis.project.dto.UserDTO;
import com.aegis.project.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.doNothing;

public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetUser_Success() {
        int userID = 1;
        UserDTO userDTO = new UserDTO();
        userDTO.setUserID(userID);
        userDTO.setUserName("Test User");

        when(userService.getUserDTO(userID)).thenReturn(userDTO);

        ResponseEntity<UserDTO> response = userController.getUser(userID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userDTO, response.getBody());
    }

    @Test
    public void testGetUser_NotFound() {
        int userID = 1;

        doThrow(new RuntimeException("User not found with ID: " + userID)).when(userService).getUserDTO(userID);

        ResponseEntity<UserDTO> response = userController.getUser(userID);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(null, response.getBody());
    }

    @Test
    public void testGetUser_InternalServerError() {
        int userID = 1;

        doThrow(new RuntimeException("Some other error")).when(userService).getUserDTO(userID);

        ResponseEntity<UserDTO> response = userController.getUser(userID);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(null, response.getBody());
    }

    @Test
    public void testGetUserByEmail_Success() {
        String email = "test@example.com";
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(email);
        userDTO.setUserName("Test User");

        when(userService.getUserDTOByEmail(email)).thenReturn(userDTO);

        ResponseEntity<UserDTO> response = userController.getUserByEmail(email);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userDTO, response.getBody());
    }

    @Test
    public void testGetUserByEmail_NotFound() {
        String email = "test@example.com";

        doThrow(new RuntimeException("User not found with email: " + email)).when(userService).getUserDTOByEmail(email);

        ResponseEntity<UserDTO> response = userController.getUserByEmail(email);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(null, response.getBody());
    }

    @Test
    public void testGetUserByEmail_InternalServerError() {
        String email = "test@example.com";

        doThrow(new RuntimeException("Some other error")).when(userService).getUserDTOByEmail(email);

        ResponseEntity<UserDTO> response = userController.getUserByEmail(email);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(null, response.getBody());
    }

    @Test
    public void testGetAllUsers_Success() {
        List<UserDTO> users = new ArrayList<>();
        users.add(new UserDTO());

        when(userService.getAllUsers()).thenReturn(users);

        ResponseEntity<List<UserDTO>> response = userController.getAllUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(users, response.getBody());
    }

    @Test
    public void testGetAllUsers_InternalServerError() {
        doThrow(new RuntimeException("Some other error")).when(userService).getAllUsers();

        ResponseEntity<List<UserDTO>> response = userController.getAllUsers();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(null, response.getBody());
    }

    @Test
    public void testUpdateUser_Success() {
        int userID = 1;
        String name = "Updated Name";
        String email = "updated@example.com";
        String profilePicture = "updatedImage";

        doNothing().when(userService).updateUser(userID, name, email, profilePicture);

        ResponseEntity<String> response = userController.updateUser(userID, name, email, profilePicture);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User updated successfully", response.getBody());
    }

    @Test
    public void testUpdateUser_NotFound() {
        int userID = 1;
        String name = "Updated Name";
        String email = "updated@example.com";
        String profilePicture = "updatedImage";

        doThrow(new RuntimeException("User not found with ID: " + userID)).when(userService).updateUser(userID, name, email, profilePicture);

        ResponseEntity<String> response = userController.updateUser(userID, name, email, profilePicture);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found with ID: " + userID, response.getBody());
    }

    @Test
    public void testUpdateUser_EmailExists() {
        int userID = 1;
        String name = "Updated Name";
        String email = "updated@example.com";
        String profilePicture = "updatedImage";

        doThrow(new RuntimeException("User with email: " + email + " already exists")).when(userService).updateUser(userID, name, email, profilePicture);

        ResponseEntity<String> response = userController.updateUser(userID, name, email, profilePicture);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("User with email: " + email + " already exists", response.getBody());
    }

    @Test
    public void testUpdateUser_Forbidden() {
        int userID = 1;
        String name = "Updated Name";
        String email = "updated@example.com";
        String profilePicture = "updatedImage";

        doThrow(new RuntimeException("User with email: " + email + " does not have permission to update user with ID: " + userID)).when(userService).updateUser(userID, name, email, profilePicture);

        ResponseEntity<String> response = userController.updateUser(userID, name, email, profilePicture);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("User with email: " + email + " does not have permission to update user with ID: " + userID, response.getBody());
    }

    @Test
    public void testUpdateUser_InternalServerError() {
        int userID = 1;
        String name = "Updated Name";
        String email = "updated@example.com";
        String profilePicture = "updatedImage";

        doThrow(new RuntimeException("Some other error")).when(userService).updateUser(userID, name, email, profilePicture);

        ResponseEntity<String> response = userController.updateUser(userID, name, email, profilePicture);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Some other error", response.getBody());
    }
}