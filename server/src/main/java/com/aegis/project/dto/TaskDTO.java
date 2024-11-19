package com.aegis.project.dto;

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

import com.aegis.project.model.FileModel;
import com.aegis.project.model.TaskModel;
import com.aegis.project.model.UserModel;

public class TaskDTO {

    private int taskID;
    private int parentProjectID;
    private int parentOrgID;
    private String taskName;
    private String taskDescription;
    private int assignerID;
    private Set<UserDTO> assignedUsers;
    private Set<FileDTO> files;
    private String taskPriority;
    private Date dueDate;
    private boolean isComplete;
    private int chatID;

    public TaskDTO(TaskModel task) {
        this.taskID = task.getTaskID();
        this.parentProjectID = task.getParentProjectID();
        this.parentOrgID = task.getParentOrgID();
        this.taskName = task.getTaskName();
        this.taskDescription = task.getTaskDescription();
        this.assignerID = task.getAssignerID();
        this.taskPriority = task.getTaskPriority();
        this.dueDate = task.getDueDate();
        this.isComplete = task.isComplete();
        this.assignedUsers = task
                .getAssignedUsers()
                .stream()
                .map(user -> new UserDTO(user))
                .collect(Collectors.toSet());
        this.files = task
                .getFiles()
                .stream()
                .map(file -> new FileDTO(file))
                .collect(Collectors.toSet());
        this.chatID = task.getChatID();
    }

    public TaskDTO(
            int taskID,
            int parentProjectID,
            int ParentOrgID,
            String TaskName,
            String TaskDescription,
            int AssignerID,
            String TaskPriority,
            Date DueDate,
            boolean IsComplete,
            Set<UserModel> assignedUsers,
            Set<FileModel> files,
            int chatID
    ) {
        this.taskID = taskID;
        this.parentProjectID = parentProjectID;
        this.parentOrgID = ParentOrgID;
        this.taskName = TaskName;
        this.taskDescription = TaskDescription;
        this.assignerID = AssignerID;
        this.taskPriority = TaskPriority;
        this.dueDate = DueDate;
        this.isComplete = IsComplete;

        this.assignedUsers = assignedUsers
                .stream()
                .map(user -> new UserDTO(user))
                .collect(Collectors.toSet());
        this.files = files
                .stream()
                .map(file -> new FileDTO(file))
                .collect(Collectors.toSet());
        this.chatID = chatID;
    }

    public TaskDTO() {
    }

    public int getTaskID() {
        return this.taskID;
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }

    public int getParentProjectID() {
        return this.parentProjectID;
    }

    public void setParentProjectID(int parentProjectID) {
        this.parentProjectID = parentProjectID;
    }

    public int getParentOrgID() {
        return this.parentOrgID;
    }

    public void setParentOrgID(int parentOrgID) {
        this.parentOrgID = parentOrgID;
    }

    public String getTaskName() {
        return this.taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskDescription() {
        return this.taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public int getAssignerID() {
        return this.assignerID;
    }

    public void setAssignerID(int assignerID) {
        this.assignerID = assignerID;
    }

    public Set<UserDTO> getAssignedUsers() {
        return this.assignedUsers;
    }

    public void setAssignedUsers(Set<UserDTO> assignedUsers) {
        this.assignedUsers = assignedUsers;
    }

    public Set<FileDTO> getFiles() {
        return this.files;
    }

    public void setFiles(Set<FileDTO> files) {
        this.files = files;
    }

    public String getTaskPriority() {
        return this.taskPriority;
    }

    public void setTaskPriority(String taskPriority) {
        this.taskPriority = taskPriority;
    }

    public Date getDueDate() {
        return this.dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isIsComplete() {
        return this.isComplete;
    }

    public boolean getIsComplete() {
        return this.isComplete;
    }

    public void setIsComplete(boolean isComplete) {
        this.isComplete = isComplete;
    }

    public int getChatID() {
        return this.chatID;
    }

    public void setChatID(int chatID) {
        this.chatID = chatID;
    }
}
