package com.aegis.project.dto;

import com.aegis.project.model.ProjectModel;

import java.util.HashSet;
import java.util.Set;

public class ProjectDTO {
    private int projectID;
    private int parentOrgID;
    private String projectName;
    private String projectDescription;

    private Set<UserDTO> assignedUsers = new HashSet<>();
    private int projectOwnerID;
    private Set<TaskDTO> projectTasks = new HashSet<>();
    private String encodedImage;
    private boolean isArchived;


    public ProjectDTO(int projectID, int parentOrgID, String projectName, String projectDescription,
                        int projectOwnerID, String encodedImage, Set<UserDTO> assignedUsers, Set<TaskDTO> projectTasks, boolean isArchived) {
        this.projectID = projectID;
        this.parentOrgID = parentOrgID;
        this.projectName = projectName;
        this.projectDescription = projectDescription;
        this.assignedUsers = assignedUsers;
        this.projectTasks = projectTasks;
        this.projectOwnerID = projectOwnerID;
        this.encodedImage = encodedImage;
        this.isArchived = isArchived;
    }

    public ProjectDTO(ProjectModel project) {
        this.projectID = project.getProjectID();
        this.parentOrgID = project.getParentOrgID();
        this.projectName = project.getProjectName();
        this.projectDescription = project.getProjectDescription();
        this.assignedUsers = project.getAssignedUsers().stream().map(user -> new UserDTO(user)).collect(java.util.stream.Collectors.toSet());
        this.projectTasks = project.getProjectTasks().stream().map(task -> new TaskDTO(task)).collect(java.util.stream.Collectors.toSet());
        this.projectOwnerID = project.getProjectOwnerID();
        this.encodedImage = project.getEncodedImage();
        this.isArchived = project.isArchived();
    }

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

    public Set<UserDTO> getAssignedUsers() {
        return assignedUsers;
    }

    public void setAssignedUsers(Set<UserDTO> assignedUsers) {
        this.assignedUsers = assignedUsers;
    }

    public int getProjectOwnerID() {
        return projectOwnerID;
    }

    public void setProjectOwnerID(int projectOwnerID) {
        this.projectOwnerID = projectOwnerID;
    }

    public Set<TaskDTO> getProjectTasks() {
        return projectTasks;
    }

    public void setProjectTasks(Set<TaskDTO> projectTasks) {
        this.projectTasks = projectTasks;
    }

    public String getEncodedImage() {
        return encodedImage;
    }

    public void setEncodedImage(String encodedImage) {
        this.encodedImage = encodedImage;
    }

    public boolean getIsArchived() {
        return isArchived;
    }

    public void setIsArchived(boolean isArchived) {
        this.isArchived = isArchived;
    }
}
