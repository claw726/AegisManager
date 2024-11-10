package com.aegis.project.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "projects")
public class ProjectModel {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int projectID;

  @Column(name = "parent_org_ID")
  private int parentOrgID;

  @Column(name = "project_name")
  private String projectName;

  @Column(name = "project_description")
  private String projectDescription;

  @ManyToMany
  @JoinTable(
    name = "project_assigned_users",
    joinColumns = @JoinColumn(name = "project_id"),
    inverseJoinColumns = @JoinColumn(name = "user_id")
  )
  private Set<UserModel> assignedUsers = new HashSet<>();

  @Column(name = "project_owner_ID")
  private int projectOwnerID;

  @OneToMany(
    mappedBy = "parentProject",
    cascade = CascadeType.ALL,
    orphanRemoval = true
  )
  private Set<TaskModel> projectTasks = new HashSet<>();

  //TODO: implement project chat table
  //private int ProjectChatID;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_org") // Foreign key column in ProjectModel table
  private OrgModel parentOrg;

  @Column(name = "encoded_image")
  private String encodedImage;

  @Column(name = "is_archived")
  private boolean isArchived;

  public int getProjectID() {
    return projectID;
  }

  public void setProjectID(int projectID) {
    this.projectID = projectID;
  }

  public int getParentOrgID() {
    return parentOrgID;
  }

  public void setParentOrgID(int parentOrgID) {
    this.parentOrgID = parentOrgID;
  }

  public String getProjectName() {
    return projectName;
  }

  public void setProjectName(String projectName) {
    this.projectName = projectName;
  }

  public String getProjectDescription() {
    return projectDescription;
  }

  public void setProjectDescription(String projectDescription) {
    this.projectDescription = projectDescription;
  }

  public Set<UserModel> getAssignedUsers() {
    return assignedUsers;
  }

  public void setAssignedUsers(Set<UserModel> assignedUsers) {
    this.assignedUsers = assignedUsers;
  }

  public int getProjectOwnerID() {
    return projectOwnerID;
  }

  public void setProjectOwnerID(int projectOwnerID) {
    this.projectOwnerID = projectOwnerID;
  }

  public Set<TaskModel> getProjectTasks() {
    return projectTasks;
  }

  public void setProjectTasks(Set<TaskModel> projectTasks) {
    this.projectTasks = projectTasks;
  }

  public String getEncodedImage() {
    return encodedImage;
  }

  public void setEncodedImage(String encodedImage) {
    this.encodedImage = encodedImage;
  }

  public OrgModel getParentOrg() {
    return parentOrg;
  }

  public void setParentOrg(OrgModel parentOrg) {
    this.parentOrg = parentOrg;
  }

  public boolean isArchived() {
    return isArchived;
  }

  public void setArchived(boolean isArchived) {
    this.isArchived = isArchived;
  }
}
