package com.aegis.project.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "projects")
public class ProjectModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int ProjectID;

    @Column(name = "parent_org_ID")
    private int ParentOrgID;

    @Column(name = "project_name")
    private String ProjectName;

    @Column(name = "project_description")
    private String ProjectDescription;

    @ManyToMany
    @JoinTable(
            name = "project_assigned_users",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<UserModel> AssignedUsers = new HashSet<>();

    @Column(name = "project_owner_ID")
    private int ProjectOwnerID;

    @ManyToMany
    @JoinTable(
            name = "project_assigned_users",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "parent_project_id")
    )
    private Set<TaskModel> ProjectTasks = new HashSet<>();

    //TODO: implement project chat table
    //private int ProjectChatID;

    public int getProjectID() {
        return ProjectID;
    }

    public void setProjectID(int projectID) {
        ProjectID = projectID;
    }

    public int getParentOrgID() {
        return ParentOrgID;
    }

    public void setParentOrgID(int parentOrgID) {
        ParentOrgID = parentOrgID;
    }

    public String getProjectName() {
        return ProjectName;
    }

    public void setProjectName(String projectName) {
        ProjectName = projectName;
    }

    public String getProjectDescription() {
        return ProjectDescription;
    }

    public void setProjectDescription(String projectDescription) {
        ProjectDescription = projectDescription;
    }

    public Set<UserModel> getAssignedUsers() {
        return AssignedUsers;
    }

    public void setAssignedUsers(Set<UserModel> assignedUsers) {
        AssignedUsers = assignedUsers;
    }

    public int getProjectOwnerID() {
        return ProjectOwnerID;
    }

    public void setProjectOwnerID(int projectOwnerID) {
        ProjectOwnerID = projectOwnerID;
    }

    public Set<TaskModel> getProjectTasks() {
        return ProjectTasks;
    }

    public void setProjectTasks(Set<TaskModel> projectTasks) {
        ProjectTasks = projectTasks;
    }

}
