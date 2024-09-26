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

    @Column(name = "UserName")
    private String UserName;

    @Column(name = "email")
    private String email;

    @ManyToMany
    @JoinTable(
            name = "user_orgs",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "org_id")
    )
    private Set<OrgModel> Orgs = new HashSet<>();

    @Column(name = "PWHash")
    private String PWHash;

    @Column(name = "TwoFactorAuthInfo")
    private String TwoFactorAuthInfo;

    @Column(name = "PasswordResetToken")
    private String PasswordResetToken;

    @Column(name = "IsLoggedIn")
    private Boolean IsLoggedIn;

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
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
}

