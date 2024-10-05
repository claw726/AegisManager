package com.aegis.project.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class UserModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int UserID;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "email")
    private String email;

    @ManyToMany
    @JoinTable(
            name = "user_orgs",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "org_id")
    )
    private Set<OrgModel> Orgs = new HashSet<>();

    @Column(name = "password_hash")
    private String PWHash;

    @Column(name = "auth_info")
    private String TwoFactorAuthInfo;

    @Column(name = "password_reset_token")
    private String PasswordResetToken;

    @Column(name = "is_logged_in")
    private Boolean IsLoggedIn;

    @Column(name = "failed_login_attempts")
    private int failedLoginAttempts;

    @Column(name = "is_locked")
    private boolean isLocked;

    public UserModel(String userName, String email, String PWHash) {
        this.userName = userName;
        this.email = email;
        this.PWHash = PWHash;
        TwoFactorAuthInfo = "none";
        PasswordResetToken = "none";
        IsLoggedIn = false;
        this.failedLoginAttempts = 0;
        this.isLocked = false;
    }

    public UserModel() {
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
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

    public Set<OrgModel> getOrgs() {
        return Orgs;
    }

    public void setOrgs(Set<OrgModel> orgs) {
        Orgs = orgs;
    }

    public String getPWHash() {
        return PWHash;
    }

    public void setPWHash(String PWHash) {
        this.PWHash = PWHash;
    }

    public String getTwoFactorAuthInfo() {
        return TwoFactorAuthInfo;
    }

    public void setTwoFactorAuthInfo(String twoFactorAuthInfo) {
        TwoFactorAuthInfo = twoFactorAuthInfo;
    }

    public String getPasswordResetToken() {
        return PasswordResetToken;
    }

    public void setPasswordResetToken(String passwordResetToken) {
        PasswordResetToken = passwordResetToken;
    }

    public Boolean getLoggedIn() {
        return IsLoggedIn;
    }

    public void setLoggedIn(Boolean loggedIn) {
        IsLoggedIn = loggedIn;
    }

    public int getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public void setFailedLoginAttempts(int failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }
}

