package com.aegis.project.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

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

    @Column(name = "ownderID")
    private int OrgOwnerID;

    @ManyToMany(mappedBy = "Orgs")
    private Set<UserModel> Users = new HashSet<>();

//    @OneToMany(mappedBy = "Org", cascade = CascadeType.ALL, orphanRemoval = true)
//    private Set<ProjectModel> Projects = new HashSet<>();

//    @OneToMany(mappedBy = "Org", cascade = CascadeType.ALL, orphanRemoval = true)
//    private String OrgChatsTableName;
}
