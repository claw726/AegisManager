package com.aegis.project.model;

import java.util.Date;
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
@Table(name = "tasks")
public class TaskModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int TaskID;

    @Column(name = "parent_project_ID")
    private int ParentProjectID;

    @Column(name = "parent_org_ID")
    private int ParentOrgID;

    @Column(name = "task_name")
    private String TaskName;

    @Column(name = "task_description")
    private String TaskDescription;

    @Column(name = "assigner_ID")
    private int AssignerID;

    @ManyToMany
    @JoinTable(
            name = "task_assignees",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<UserModel> AssignedUsers = new HashSet<>();

    @Column(name = "task_priority")
    private String TaskPriority;

    @Column(name = "due_date")
    private Date DueDate;

    @Column(name = "is_complete")
    private boolean IsComplete;

    //TODO: implement task chat table and task files table

    //private int TaskChatID;
    //private String TaskFilesTableName;


    public int getTaskID() {
        return TaskID;
    }

    public void setTaskID(int taskID) {
        TaskID = taskID;
    }

    public int getParentProjectID() {
        return ParentProjectID;
    }

    public void setParentProjectID(int parentProjectID) {
        ParentProjectID = parentProjectID;
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
