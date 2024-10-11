package com.aegis.project.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "orgs")
public class OrgModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int OrgID;

    @Column(name = "name")
    private String OrgName;

    @Column(name = "description")
    private String OrgDescription;

    @Column(name = "owner_ID")
    private int OrgOwnerID;

    @ManyToMany(mappedBy = "Orgs")
    private Set<UserModel> Users = new HashSet<>();

    @OneToMany(mappedBy = "parentOrg", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProjectModel> OrgProjects = new HashSet<>();

    @Column(name = "encoded_image")
    private String encodedImage;

    public int getOrgID() {
        return OrgID;
    }

    public void setOrgID(int orgID) {
        OrgID = orgID;
    }

    public String getOrgName() {
        return OrgName;
    }

    public void setOrgName(String orgName) {
        OrgName = orgName;
    }

    public String getOrgDescription() {
        return OrgDescription;
    }

    public void setOrgDescription(String orgDescription) {
        OrgDescription = orgDescription;
    }

    public int getOrgOwnerID() {
        return OrgOwnerID;
    }

    public void setOrgOwnerID(int orgOwnerID) {
        OrgOwnerID = orgOwnerID;
    }

    public Set<UserModel> getUsers() {
        return Users;
    }

    public void setUsers(Set<UserModel> users) {
        Users = users;
    }

    public Set<ProjectModel> getOrgProjects() {
        return OrgProjects;
    }

    public void setOrgProjects(Set<ProjectModel> orgProjects) {
        this.OrgProjects = orgProjects;
    }

    public String getEncodedImage() {
        return encodedImage;
    }

    public void setEncodedImage(String encodedImage) {
        this.encodedImage = encodedImage;
    }

//    @OneToMany(mappedBy = "Org", cascade = CascadeType.ALL, orphanRemoval = true)
//    private String OrgChatsTableName;
}
