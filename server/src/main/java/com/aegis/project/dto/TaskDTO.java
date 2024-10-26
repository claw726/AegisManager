package com.aegis.project.dto;

import java.util.Set;
import java.util.Date;

import com.aegis.project.model.UserModel;
import com.aegis.project.model.TaskModel;

import com.aegis.project.service.TaskService;
public class TaskDTO {
    private Integer taskID;
    private Integer parentProjectID;
    private Integer parentOrgID;
    private String taskName;
    private String taskDescription;
    private Integer assignerID;
    private Set<UserModel> assignedUsers;
    private String taskPriority;
    private Date dueDate;
    private boolean isComplete;

    private TaskService taskService;

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
        this.assignedUsers = task.getAssignedUsers();
        // Map other fields
    }

    public TaskDTO(Integer taskID, Integer parentProjectID, Integer ParentOrgID, String TaskName,
                    String TaskDescription, Integer AssignerID, String TaskPriority,
                    Date DueDate, boolean IsComplete) {
        this.taskID = taskID;
        this.parentProjectID = parentProjectID;
        this.parentOrgID = ParentOrgID;
        this.taskName = TaskName;
        this.taskDescription = TaskDescription;
        this.assignerID = AssignerID;
        this.taskPriority = TaskPriority;
        this.dueDate = DueDate;
        this.isComplete = IsComplete;

        this.assignedUsers = taskService.getAssignedUsers(taskID);
    }

    public Integer getTaskID() {
        return taskID;
    }

    public void setTaskID(Integer taskID) {
        this.taskID = taskID;
    }

    public Integer getParentProjectID() {
        return parentProjectID;
    }

    public void setParentProjectID(Integer parentProjectID) {
        this.parentProjectID = parentProjectID;
    }

    public Integer getParentOrgID() {
        return parentOrgID;
    }

    public void setParentOrgID(Integer parentOrgID) {
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

    public Integer getAssignerID() {
        return assignerID;
    }

    public void setAssignerID(Integer assignerID) {
        this.assignerID = assignerID;
    }

    public Set<UserModel> getAssignedUsers() {
        return assignedUsers;
    }

    public void setAssignedUsers(Set<UserModel> assignedUsers) {
        this.assignedUsers = assignedUsers;
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

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean isComplete) {
        this.isComplete = isComplete;
    }
}
