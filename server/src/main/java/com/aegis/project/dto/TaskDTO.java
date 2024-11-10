package com.aegis.project.dto;

import com.aegis.project.model.TaskModel;
import com.aegis.project.model.UserModel;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

public class TaskDTO {

  private int taskID;
  private int parentProjectID;
  private int parentOrgID;
  private String taskName;
  private String taskDescription;
  private int assignerID;
  private Set<UserDTO> assignedUsers;
  private String taskPriority;
  private Date dueDate;
  private boolean isComplete;

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
    Set<UserModel> assignedUsers
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
  }

  public TaskDTO() {}

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

  public Set<UserDTO> getAssignedUsers() {
    return assignedUsers;
  }

  public void setAssignedUsers(Set<UserDTO> assignedUsers) {
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