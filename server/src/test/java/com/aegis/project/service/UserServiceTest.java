package com.aegis.project.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.aegis.project.dto.UserDTO;
import com.aegis.project.model.UserModel;
import com.aegis.project.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private Authentication authentication;

  @Mock
  private UserDetails userDetails;

  @Mock
  private BCryptPasswordEncoder passwordEncoder;

  @InjectMocks
  private UserService userService;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }

  @Test
  public void testUpdateUser2FA_Success() {
    int userID = 1;
    UserModel user = new UserModel();
    user.setUserID(userID);
    user.setEmail("test@example.com");
    user.setTwoFactorAuthInfo("2FA Info");

    when(authentication.getPrincipal()).thenReturn(userDetails);
    when(userDetails.getUsername()).thenReturn("test@example.com");
    when(userRepository.findById(userID)).thenReturn(Optional.of(user));

    String twoFactorAuthInfo = userService.getUser2FA(userID);

    assertEquals("2FA Info", twoFactorAuthInfo);
  }

  @Test
  public void testUpdateUser2FA_NoPermission() {
    int userID = 1;
    String twoFactorAuthInfo = "2FA Info";
    UserModel user = new UserModel();
    user.setEmail("test@example.com");

    when(authentication.getPrincipal()).thenReturn(userDetails);
    when(userDetails.getUsername()).thenReturn("other@example.com");
    when(userRepository.findById(userID)).thenReturn(Optional.of(user));

    Exception exception = assertThrows(RuntimeException.class, () -> {
      userService.updateUser2FA(userID, twoFactorAuthInfo);
    });

    assertEquals(
      "User does not have permission to update 2FA for user with ID: " + userID,
      exception.getMessage()
    );
  }

  @Test
  public void testGetUser2FA_Success() {
    int userID = 1;
    UserModel user = new UserModel();
    user.setEmail("test@example.com");
    user.setTwoFactorAuthInfo("2FA Info");
    user.setUserID(userID);

    when(authentication.getPrincipal()).thenReturn(userDetails);
    when(userDetails.getUsername()).thenReturn("test@example.com");
    when(userRepository.findById(userID)).thenReturn(Optional.of(user));

    String twoFactorAuthInfo = userService.getUser2FA(userID);

    assertEquals("2FA Info", twoFactorAuthInfo);
  }

  @Test
  public void testGetUser2FA_NoPermission() {
    int userID = 1;
    UserModel user = new UserModel();
    user.setEmail("test@example.com");

    when(authentication.getPrincipal()).thenReturn(userDetails);
    when(userDetails.getUsername()).thenReturn("other@example.com");
    when(userRepository.findById(userID)).thenReturn(Optional.of(user));

    Exception exception = assertThrows(RuntimeException.class, () -> {
      userService.getUser2FA(userID);
    });

    assertEquals(
      "User does not have permission to get 2FA for user with ID: " + userID,
      exception.getMessage()
    );
  }

  @Test
  public void testFindUserByEmail_Success() {
    String email = "test@example.com";
    UserModel user = new UserModel();
    user.setEmail(email);

    when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

    Optional<UserModel> foundUser = userService.findUserByEmail(email);

    assertTrue(foundUser.isPresent());
    assertEquals(email, foundUser.get().getEmail());
  }

  @Test
  public void testFindUserByEmail_NotFound() {
    String email = "test@example.com";

    when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

    Optional<UserModel> foundUser = userService.findUserByEmail(email);

    assertFalse(foundUser.isPresent());
  }

  @Test
  public void testGetUserDTO_Success() {
    int userID = 1;
    UserModel user = new UserModel();
    user.setUserID(userID);

    when(userRepository.findById(userID)).thenReturn(Optional.of(user));

    UserDTO userDTO = userService.getUserDTO(userID);

    assertEquals(userID, userDTO.getUserID());
  }

  @Test
  public void testGetUserDTO_NotFound() {
    int userID = 1;

    when(userRepository.findById(userID)).thenReturn(Optional.empty());

    Exception exception = assertThrows(RuntimeException.class, () -> {
      userService.getUserDTO(userID);
    });

    assertEquals("User not found with ID: " + userID, exception.getMessage());
  }

  @Test
  public void testGetUserDTOByEmail_Success() {
    String email = "test@example.com";
    UserModel user = new UserModel();
    user.setEmail(email);

    when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

    UserDTO userDTO = userService.getUserDTOByEmail(email);

    assertEquals(email, userDTO.getEmail());
  }

  @Test
  public void testGetUserDTOByEmail_NotFound() {
    String email = "test@example.com";

    when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

    Exception exception = assertThrows(RuntimeException.class, () -> {
      userService.getUserDTOByEmail(email);
    });

    assertEquals("User not found with email: " + email, exception.getMessage());
  }

  @Test
  public void testUpdateFailedLoginAttempts() {
    int userID = 1;
    int failedLoginAttempts = 3;
    boolean isLocked = true;

    doNothing()
      .when(userRepository)
      .updateFailedLoginAttempts(failedLoginAttempts, isLocked, userID);

    userService.updateFailedLoginAttempts(
      failedLoginAttempts,
      isLocked,
      userID
    );

    verify(userRepository, times(1)).updateFailedLoginAttempts(
      failedLoginAttempts,
      isLocked,
      userID
    );
  }

  @Test
  public void testGetAllUsers() {
    UserModel user1 = new UserModel();
    user1.setUserID(1);
    UserModel user2 = new UserModel();
    user2.setUserID(2);

    when(userRepository.findAll()).thenReturn(List.of(user1, user2));

    List<UserDTO> users = userService.getAllUsers();

    assertEquals(2, users.size());
    assertEquals(1, users.get(0).getUserID());
    assertEquals(2, users.get(1).getUserID());
  }

  @Test
  public void testCreateUserJson() {
    UserModel user = new UserModel();
    user.setUserID(1);
    user.setUserName("Test User");
    user.setEmail("test@example.com");

    String userJson = userService.createUserJson(user);

    assertEquals(
      "{\"userID\": 1,\"userName\": Test User,\"email\": test@example.com\"}",
      userJson
    );
  }

  @Test
  public void testUpdateUser_Success() {
    int userID = 1;
    String name = "Updated Name";
    String email = "updated@example.com";
    String profilePicture = "updatedProfilePic.jpg";
    UserModel user = new UserModel();
    user.setEmail("test@example.com");

    when(authentication.getPrincipal()).thenReturn(userDetails);
    when(userDetails.getUsername()).thenReturn("test@example.com");
    when(userRepository.findById(userID)).thenReturn(Optional.of(user));
    when(userRepository.existsByEmail(email)).thenReturn(false);

    userService.updateUser(userID, name, email, profilePicture);

    verify(userRepository, times(1)).save(user);
    assertEquals(name, user.getUserName());
    assertEquals(email, user.getEmail());
    assertEquals(profilePicture, user.getProfilePicture());
  }

  @Test
  public void testUpdateUser_NoPermission() {
    int userID = 1;
    String name = "Updated Name";
    String email = "updated@example.com";
    String profilePicture = "updatedProfilePic.jpg";
    UserModel user = new UserModel();
    user.setEmail("test@example.com");

    when(authentication.getPrincipal()).thenReturn(userDetails);
    when(userDetails.getUsername()).thenReturn("other@example.com");
    when(userRepository.findById(userID)).thenReturn(Optional.of(user));

    Exception exception = assertThrows(RuntimeException.class, () -> {
      userService.updateUser(userID, name, email, profilePicture);
    });

    assertEquals(
      "User with email: other@example.com does not have permission to update user with ID: " +
      userID,
      exception.getMessage()
    );
  }

  @Test
  public void testCreateUser_Success() {
    String password = "password";
    UserModel user = new UserModel();
    user.setUserName("New User");
    user.setEmail("newuser@example.com");
    user.setPWHash("password");

    when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
    when(passwordEncoder.encode(password)).thenReturn("encodedPassword");

    boolean isCreated = userService.createUser(
      "newuser@example.com",
      "New User",
      "password",
      null
    );

    assertTrue(isCreated);

    ArgumentCaptor<UserModel> userCaptor = ArgumentCaptor.forClass(
      UserModel.class
    );
    verify(userRepository, times(1)).save(userCaptor.capture());
    UserModel savedUser = userCaptor.getValue();

    assertEquals("New User", savedUser.getUserName());
    assertEquals("newuser@example.com", savedUser.getEmail());
    assertEquals("encodedPassword", savedUser.getPWHash());
  }

  @Test
  public void testCreateUser_UserAlreadyExists() {
    String password = "password";
    UserModel user = new UserModel();
    user.setUserName("Existing User");
    user.setEmail("existinguser@example.com");
    user.setPWHash("password");

    when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);
    when(passwordEncoder.encode(password)).thenReturn("encodedPassword");

    boolean isCreated = userService.createUser(
      "existinguser@example.com",
      "Existing User",
      "password",
      null
    );

    assertFalse(isCreated);
    verify(userRepository, never()).save(user);
  }
}
