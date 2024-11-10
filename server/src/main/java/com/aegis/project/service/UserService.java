package com.aegis.project.service;

import com.aegis.project.dto.UserDTO;
import com.aegis.project.model.UserModel;
import com.aegis.project.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private static final Logger logger = LoggerFactory.getLogger(
    UserService.class
  );

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private BCryptPasswordEncoder passwordEncoder;

  @Transactional
  public boolean createUser(
    String email,
    String name,
    String password,
    String profilePicture
  ) {
    logger.info(
      "Attempting to create user with email: {}, name: {}",
      email,
      name
    );
    if (userRepository.existsByEmail(email)) {
      logger.warn("User with email: {} already exists", email);
      return false;
    }
    UserModel user = new UserModel(
      name,
      email,
      passwordEncoder.encode(password),
      profilePicture
    );

    userRepository.save(user);
    logger.info("User with email: {} created successfully", email);
    return true;
  }

  public void updateUser(
    int userID,
    String name,
    String email,
    String profilePicture
  ) {
    logger.info("Updating user with ID: {}", userID);
    UserModel user = userRepository
      .findById(userID)
      .orElseThrow(() ->
        new RuntimeException("User not found with ID: " + userID)
      );

    Authentication authentication = SecurityContextHolder.getContext()
      .getAuthentication();
    String currentUsername =
      ((UserDetails) authentication.getPrincipal()).getUsername();

    if (!currentUsername.equals(user.getEmail())) {
      logger.warn(
        "User with email: {} does not have permission to update user with ID: {}",
        currentUsername,
        userID
      );
      throw new RuntimeException(
        "User with email: " +
        currentUsername +
        " does not have permission to update user with ID: " +
        userID
      );
    }

    if (
      userRepository.existsByEmail(email) &&
      !userRepository
        .findByEmail(email)
        .get()
        .getEmail()
        .equals(user.getEmail())
    ) {
      logger.warn("User with email: {} already exists", email);
      throw new RuntimeException(
        "User with email: " + email + " already exists"
      );
    }
    user.setUserName(name);
    user.setEmail(email);
    user.setProfilePicture(profilePicture);
    userRepository.save(user);
    logger.info("Updated user with ID: {}", userID);
  }

  public void updateUser2FA(int userID, String twoFactorAuthInfo) {
    logger.info("Updating 2FA for user with ID: {}", userID);

    Authentication authentication = SecurityContextHolder.getContext()
      .getAuthentication();
    String currentUsername =
      ((UserDetails) authentication.getPrincipal()).getUsername();

    if (
      !currentUsername.equals(userRepository.findById(userID).get().getEmail())
    ) {
      logger.warn(
        "User does not have permission to update 2FA for user with ID: {}",
        userID
      );
      throw new RuntimeException(
        "User does not have permission to update 2FA for user with ID: " +
        userID
      );
    }

    UserModel user = userRepository
      .findById(userID)
      .orElseThrow(() ->
        new RuntimeException("User not found with ID: " + userID)
      );

    user.setHas2fa(twoFactorAuthInfo != null);

    user.setTwoFactorAuthInfo(twoFactorAuthInfo);
    userRepository.save(user);
    logger.info("Updated 2FA for user with ID: {}", userID);
  }

  public String getUser2FA(int userID) {
    logger.info("Getting 2FA for user with ID: {}", userID);

    Authentication authentication = SecurityContextHolder.getContext()
      .getAuthentication();
    String currentUsername =
      ((UserDetails) authentication.getPrincipal()).getUsername();

    if (
      !currentUsername.equals(userRepository.findById(userID).get().getEmail())
    ) {
      logger.warn(
        "User does not have permission to get 2FA for user with ID: {}",
        userID
      );
      throw new RuntimeException(
        "User does not have permission to get 2FA for user with ID: " + userID
      );
    }

    UserModel user = userRepository
      .findById(userID)
      .orElseThrow(() ->
        new RuntimeException("User not found with ID: " + userID)
      );
    logger.info("Got 2FA for user with ID: {}", userID);
    return user.getTwoFactorAuthInfo();
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
    UserModel user = userRepository
      .findById(userID)
      .orElseThrow(() ->
        new RuntimeException("User not found with ID: " + userID)
      );
    UserDTO userDTO = new UserDTO(user);
    logger.info("Got user DTO for user ID: {}", userID);
    return userDTO;
  }

  public UserDTO getUserDTOByEmail(String email) {
    logger.info("Getting user DTO for user with email: {}", email);
    UserModel user = userRepository
      .findByEmail(email)
      .orElseThrow(() ->
        new RuntimeException("User not found with email: " + email)
      );
    UserDTO userDTO = new UserDTO(user);
    logger.info("Got user DTO for user with: {}", email);
    return userDTO;
  }

  public void updateFailedLoginAttempts(
    int failedLoginAttempts,
    boolean isLocked,
    int userID
  ) {
    logger.info(
      "Updating failed login attempts for user ID: {}. Attempts: {}, Locked: {}",
      userID,
      failedLoginAttempts,
      isLocked
    );
    userRepository.updateFailedLoginAttempts(
      failedLoginAttempts,
      isLocked,
      userID
    );
    logger.info("Updated failed login attempts for user ID: {}", userID);
  }

  public List<UserDTO> getAllUsers() {
    List<UserModel> allUsers = userRepository.findAll();
    return allUsers
      .stream()
      .map(user -> new UserDTO(user))
      .collect(Collectors.toList());
  }

  public String createUserJson(UserModel user) {
    return (
      "{" +
      "\"userID\": " +
      user.getUserID() +
      "," +
      "\"userName\": " +
      user.getUserName() +
      "," +
      "\"email\": " +
      user.getEmail() +
      "\"" +
      "}"
    );
  }
}
