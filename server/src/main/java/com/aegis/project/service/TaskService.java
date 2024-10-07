package com.aegis.project.service;

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.config.TaskManagementConfigUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.aegis.project.dto.TaskDTO;
import com.aegis.project.model.ProjectModel;
import com.aegis.project.model.TaskModel;
import com.aegis.project.model.UserModel;
import com.aegis.project.repository.ProjectRepository;
import com.aegis.project.repository.TaskRepository;
import com.aegis.project.repository.UserRepository;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProjectRepository projectRepository;

    public boolean createTask(int parentProjectID, int parentOrgID, String taskName, String taskDescription, int assignerID, String taskPriority, Date dueDate) {
        TaskModel task = new TaskModel();
        task.setParentProjectID(parentProjectID);
        task.setParentOrgID(parentOrgID);
        task.setTaskName(taskName);
        task.setTaskDescription(taskDescription);
        task.setAssignerID(assignerID);
        task.setTaskPriority(taskPriority);
        task.setDueDate(dueDate);

        taskRepository.save(task);

        ProjectModel parentProject = projectRepository.findById(parentProjectID)
            .orElseThrow(() -> new RuntimeException("Project not found with id: " + parentProjectID));
        Set<TaskModel> projectTasks = parentProject.getProjectTasks();
        projectTasks.add(task);
        parentProject.setProjectTasks(projectTasks);

        return true;
    }

    public Set<TaskDTO> getAllUserTasks(int userID, int orgID, int projectID) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = ((UserDetails) authentication.getPrincipal()).getUsername();
        UserModel currentUser = userRepository.findByEmail(currentUsername)
            .orElseThrow(() -> new RuntimeException("User not found with email: " + currentUsername));
        if (currentUser.getUserID() == userID) {
            Set<TaskModel> tasks = taskRepository.getAllUserTasks(userID, orgID, projectID);
            return tasks.stream()
                .map(task -> new TaskDTO(task.getTaskID(), task.getParentProjectID(), task.getParentProject(), task.getParentOrgID(), task.getTaskName(), task.getTaskDescription(), task.getAssignerID(), task.getAssignedUsers(), task.getTaskPriority(), task.getDueDate(), task.isIsComplete()))
                .collect(Collectors.toSet());
        }
        else {
            throw new RuntimeException("User does not have permission to access task list");
        }
    }

    public boolean validateAssigner(int taskID, int userID) {
        TaskModel task = taskRepository.findById(taskID)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskID));
        return(task.getAssignerID() == userID);
    }

    public void updateTaskCompletionStatus(int taskID, boolean completed) {
        TaskModel task = taskRepository.findById(taskID)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskID));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = ((UserDetails) authentication.getPrincipal()).getUsername();

        UserModel currentUser = userRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + currentUsername));
        Set<UserModel> users = task.getAssignedUsers();
        boolean hasPermission = false;
        if (task.getAssignerID() == currentUser.getUserID()) {
            hasPermission = true;
        }
        else {
            for (UserModel user : users) {
                if (user.getUserID() == currentUser.getUserID()) {
                    hasPermission = true;
                }
            }
        }
        if (!hasPermission) {
            throw new RuntimeException("User does not have permission to update completion status of task");
        }
        taskRepository.updateTaskCompletedStatus(taskID, completed);
    }
}
