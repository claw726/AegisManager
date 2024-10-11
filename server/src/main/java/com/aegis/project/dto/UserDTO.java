package com.aegis.project.dto;

public class UserDTO {
    private int userID;
    private String userName;
    private String email;
    private String profilePicture;

    public UserDTO(int userID, String userName, String email, String profilePicture) {
        this.userID = userID;
        this.userName = userName;
        this.email = email;
        this.profilePicture = profilePicture;
    }

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
}
