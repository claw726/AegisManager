package com.aegis.project.dto;

import com.aegis.project.model.UserModel;

public class UserDTO {

  private int userID;
  private String userName;
  private String email;
  private String profilePicture;
  private boolean has2fa;

  public UserDTO(
    int userID,
    String userName,
    String email,
    String profilePicture,
    boolean has2fa
  ) {
    this.userID = userID;
    this.userName = userName;
    this.email = email;
    this.profilePicture = profilePicture;
    this.has2fa = has2fa;
  }

  public UserDTO(UserModel user) {
    this.userID = user.getUserID();
    this.userName = user.getUserName();
    this.email = user.getEmail();
    this.profilePicture = user.getProfilePicture();
    this.has2fa = user.isHas2fa();
  }

  public UserDTO() {}

  public int getUserID() {
    return userID;
  }

  public void setUserID(int userID) {
    this.userID = userID;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getProfilePicture() {
    return this.profilePicture;
  }

  public void setProfilePicture(String profilePicture) {
    this.profilePicture = profilePicture;
  }

  public boolean isHas2fa() {
    return has2fa;
  }

  public void setHas2fa(boolean has2fa) {
    this.has2fa = has2fa;
  }
}
