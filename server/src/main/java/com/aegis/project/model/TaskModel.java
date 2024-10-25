package com.aegis.project.model;


import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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
import jakarta.persistence.Table;

@Entity
@Table(name = "tasks")
public class TaskModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int taskID;

    @Column(name = "parent_project_ID", insertable = false, updatable = false)
    private int parentProjectID;

    /*
    @ManyToOne
    @JoinColumn(name = "parent_project")
    private ProjectModel parentProject;
    */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_project_ID")  // Foreign key column in ProjectModel table
    private ProjectModel parentProject;

    @Column(name = "parent_org_ID", insertable = false, updatable = false)
    private int parentOrgID;

    @Column(name = "task_name")
    private String taskName;

    @Column(name = "task_description")
    private String taskDescription;

    @Column(name = "assigner_ID")
    private int assignerID;

    @ManyToMany
    @JoinTable(
            name = "task_assignees",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<UserModel> assignedUsers = new HashSet<>();

    @Column(name = "task_priority")
    private String taskPriority;

    @Column(name = "due_date")
    private Date dueDate;

    @Column(name = "is_complete")
    private boolean isComplete;

    //TODO: implement task chat table and task files table

    //private int TaskChatID;
    //private String TaskFilesTableName;


    public int getTaskID() {
        return taskID;
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }

    public ProjectModel getParentProject() {
        return parentProject;
    }

    public void setParentProject(ProjectModel parentProject) {
        this.parentProject = parentProject;
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
