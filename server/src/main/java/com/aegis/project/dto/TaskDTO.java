package com.aegis.project.dto;

import java.util.Set;
import java.util.HashSet;
import java.util.Date;

import com.aegis.project.model.ProjectModel;
import com.aegis.project.model.UserModel;

public class TaskDTO {
    private int taskID;
    private int parentProjectID;
    private ProjectModel ParentProject;
    private int ParentOrgID;
    private String TaskName;
    private String TaskDescription;
    private int AssignerID;
    private Set<UserModel> AssignedUsers;
    private String TaskPriority;
    private Date DueDate;
    private boolean IsComplete;

    public TaskDTO(int taskID, int parentProjectID, ProjectModel ParentProject, int ParentOrgID, String TaskName,
                    String TaskDescription, int AssignerID, Set<UserModel> AssignedUsers, String TaskPriority,
                    Date DueDate, boolean IsComplete) {
        this.taskID = taskID;
        this.parentProjectID = parentProjectID;
        this.ParentProject = ParentProject;
        this.ParentOrgID = ParentOrgID;
        this.TaskName = TaskName;
        this.TaskDescription = TaskDescription;
        this.AssignerID = AssignerID;
        this.AssignedUsers = AssignedUsers;
        this.TaskPriority = TaskPriority;
        this.DueDate = DueDate;
        this.IsComplete = IsComplete;
    }

    public int getTaskID() {
        return taskID;
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }

    public int getParentProjectID() {
        return parentProjectID;
    }

    public void setParentProjectID(int parentProjectID) {
        this.parentProjectID = parentProjectID;
    }

    public ProjectModel getParentProject() {
        return ParentProject;
    }

    public void setParentProject(ProjectModel parentProject) {
        ParentProject = parentProject;
    }

    public int getParentOrgID() {
        return ParentOrgID;
    }

    public void setParentOrgID(int parentOrgID) {
        ParentOrgID = parentOrgID;
    }

    public String getTaskName() {
        return TaskName;
    }

    public void setTaskName(String taskName) {
        TaskName = taskName;
    }

    public String getTaskDescription() {
        return TaskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        TaskDescription = taskDescription;
    }

    public int getAssignerID() {
        return AssignerID;
    }

    public void setAssignerID(int assignerID) {
        AssignerID = assignerID;
    }

    public Set<UserModel> getAssignedUsers() {
        return AssignedUsers;
    }

    public void setAssignedUsers(Set<UserModel> assignedUsers) {
        AssignedUsers = assignedUsers;
    }

    public String getTaskPriority() {
        return TaskPriority;
    }

    public void setTaskPriority(String taskPriority) {
        TaskPriority = taskPriority;
    }

    public Date getDueDate() {
        return DueDate;
    }

    public void setDueDate(Date dueDate) {
        DueDate = dueDate;
    }

    public boolean isIsComplete() {
        return IsComplete;
    }

    public void setIsComplete(boolean isComplete) {
        IsComplete = isComplete;
    }

}
