package com.aegis.project.service;

import java.util.Date;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

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
    @Autowired
    private UserService userService;

    @Autowired
    private SimpMessagingTemplate simpMessageTemplate;

    public String switchTaskAssigner(int taskID, String newAssignerEmail) {
        TaskModel task = taskRepository.findById(taskID)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskID));

        userRepository.findByEmail(newAssignerEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + newAssignerEmail));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = ((UserDetails) authentication.getPrincipal()).getUsername();

        UserModel currentUser = userRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + currentUsername));

        if (task.getAssignerID() != currentUser.getUserID()) {
            throw new RuntimeException("User does not have permission to switch task assigner");
        }

        task.setAssignerID(userRepository.findByEmail(newAssignerEmail).get().getUserID());
        taskRepository.save(task);

        return "Assigner switched successfully";
    }

    public String getTask(int taskID) {
        TaskModel task = taskRepository.findById(taskID)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskID));

        return createTaskJson(task);
    }

    public void sendTaskInfoToUsers(int taskID) {
        TaskModel task = taskRepository.findById(taskID)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskID));

        String taskJson = createTaskJson(task);

        Set<UserModel> assignedUsers = task.getAssignedUsers();

        for (UserModel user : assignedUsers) {
            simpMessageTemplate.convertAndSendToUser(user.getEmail(), "/queue/task-updates", getTask(taskID));
        }
    }

    public void notifyTaskDeletion(int taskID) {
        TaskModel task = taskRepository.findById(taskID)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskID));

        Set<UserModel> assignedUsers = task.getAssignedUsers();

        for (UserModel user : assignedUsers) {
            simpMessageTemplate.convertAndSendToUser(user.getEmail(), "/queue/task-updates", "Task deleted with ID: " + taskID);
        }
    }

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
        projectRepository.save(parentProject);

        return true;
    }

    public Set<UserModel> getAssignedUsers(int taskID) {
        TaskModel task = taskRepository.findById(taskID)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskID));
        return task.getAssignedUsers();
    }

    public String getAllUserTasks(int userID, int orgID, int projectID) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = ((UserDetails) authentication.getPrincipal()).getUsername();
        UserModel currentUser = userRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + currentUsername));
        if (currentUser.getUserID() == userID) {
            Set<TaskModel> tasks = taskRepository.getAllUserTasks(userID, orgID, projectID);
            String ret = "{";
            for (TaskModel task : tasks) {
                if (ret.length() > 1) {
                    ret += ",";
                }
                ret += createTaskJson(task);
            }
            ret += "}";
            return ret;

            /*return tasks.stream()
                .map(task -> new TaskDTO(task.getTaskID(), task.getParentProjectID(), task.getParentOrgID(), task.getTaskName(), task.getTaskDescription(), task.getAssignerID(), task.getTaskPriority(), task.getDueDate(), task.isComplete()))
                .collect(Collectors.toSet());*/
        } else {
            throw new RuntimeException("User does not have permission to access task list");
        }
    }

    public void updateTask(int taskID, String taskName, String taskDescription, int assignerID, String taskPriority, Date dueDate, boolean isComplete) {
        TaskModel task = taskRepository.findById(taskID)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskID));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = ((UserDetails) authentication.getPrincipal()).getUsername();

        UserModel currentUser = userRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + currentUsername));

        if (task.getAssignerID() != currentUser.getUserID()) {
            throw new RuntimeException("User does not have permission to update task");
        }

        task.setTaskName(taskName);
        task.setTaskDescription(taskDescription);
        task.setAssignerID(assignerID);
        task.setTaskPriority(taskPriority);
        task.setDueDate(dueDate);
        task.setComplete(isComplete);

        taskRepository.save(task);
    }

    public void deleteTask(int taskID) {
        TaskModel task = taskRepository.findById(taskID)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskID));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = ((UserDetails) authentication.getPrincipal()).getUsername();

        UserModel currentUser = userRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + currentUsername));

        if (task.getAssignerID() != currentUser.getUserID()) {
            throw new RuntimeException("User does not have permission to delete task");
        }

        taskRepository.deleteById(taskID);

        ProjectModel parentProject = projectRepository.findById(task.getParentOrgID())
                .orElseThrow(() -> new RuntimeException("Parent project not found with id: " + task.getParentOrgID()));
        Set<TaskModel> projectTasks = parentProject.getProjectTasks();
        projectTasks.remove(task);
        projectRepository.save(parentProject);
    }

    public boolean validateAssigner(int taskID, int userID) {
        TaskModel task = taskRepository.findById(taskID)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskID));
        return (task.getAssignerID() == userID);
    }

    public void updateTaskCompletionStatus(int taskID, boolean isCompleted) {
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
        } else {
            for (UserModel user : users) {
                if (user.getUserID() == currentUser.getUserID()) {
                    hasPermission = true;
                }
            }
        }
        if (!hasPermission) {
            throw new RuntimeException("User does not have permission to update completion status of task");
        }
        taskRepository.updateTaskCompletedStatus(taskID, isCompleted);
    }

    public void addUser(int taskID, String email) {
        UserModel userToAdd = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        TaskModel task = taskRepository.findById(taskID)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskID));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = ((UserDetails) authentication.getPrincipal()).getUsername();

        UserModel currentUser = userRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + currentUsername));

        if (task.getAssignerID() != currentUser.getUserID()) {
            throw new RuntimeException("User does not have permission to add user to task");
        }
        task.getAssignedUsers().add(userToAdd);
        taskRepository.save(task);
    }

    public void removeUser(int taskID, String email) {
        UserModel userToRemove = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        TaskModel task = taskRepository.findById(taskID)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskID));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = ((UserDetails) authentication.getPrincipal()).getUsername();

        UserModel currentUser = userRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + currentUsername));

        if (task.getAssignerID() != currentUser.getUserID()) {
            throw new RuntimeException("User does not have permission to remove user from  task");
        }
        task.getAssignedUsers().remove(userToRemove);
        taskRepository.save(task);
    }

    public String createTaskJson(TaskModel task) {
        String ret = "{"
                + "\"taskID\": " + task.getTaskID() + ","
                + "\"parentProjectID\": " + task.getParentProjectID() + ","
                + "\"parentOrgID\": " + task.getParentOrgID() + ","
                + "\"taskName\": \"" + task.getTaskName() + "\","
                + "\"taskDescription\": \"" + task.getTaskDescription() + "\","
                + "\"assignerID\": " + task.getAssignerID() + ","
                + "\"taskPriority\": \"" + task.getTaskPriority() + "\","
                + "\"dueDate\": \"" + task.getDueDate() + "\","
                + "\"isComplete\": " + task.isComplete() + ","
                + "\"assignedUsers\": [";
        boolean first = true;
        for (UserModel user : task.getAssignedUsers()) {
            if (first) {
                first = false;
            } else {
                ret += ",";
            }
            ret += userService.createUserJson(user);
        }
        ret += "]}";
        return ret;
    }
}
