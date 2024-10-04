package com.aegis.project.dto;

import java.util.Date;

public class TaskDTO {
    private int taskID;
    private int parentProjectID;
    private int parentOrgID;
    private String taskName;
    private String taskDescription;
    private int assignerID;
    private String taskPriority;
    private Date dueDate;

    public TaskDTO(int taskID, int parentProjectID, int parentOrgID, String taskName, String taskDescription, int assignerID, String taskPriority, Date dueDate) {
        this.taskID = taskID;
        this.parentProjectID = parentProjectID;
        this.parentOrgID = parentOrgID;
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.assignerID = assignerID;
        this.taskPriority = taskPriority;
        this.dueDate = dueDate;
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

    public int getParentOrgID() {
        return parentOrgID;
    }

    public void setParentOrgID(int parentOrgID) {
        this.parentOrgID = parentOrgID;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public int getAssignerID() {
        return assignerID;
    }

    public void setAssignerID(int assignerID) {
        this.assignerID = assignerID;
    }

    public String getTaskPriority() {
        return taskPriority;
    }

    public void setTaskPriority(String taskPriority) {
        this.taskPriority = taskPriority;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }
}
