package com.aegis.project.service;

import com.aegis.project.controller.SocketIOController;
import com.aegis.project.dto.TaskDTO;
import com.aegis.project.exception.TaskNotFoundException;
import com.aegis.project.model.*;
import com.aegis.project.repository.*;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

  @Autowired
  private TaskRepository taskRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ProjectRepository projectRepository;

  @Autowired
  private OrgRepository orgRepository;

  @Autowired
  private UserService userService;

  @Autowired
  private SocketIOController socketIOController;

  private static final Logger logger = LoggerFactory.getLogger(
    TaskService.class
  );
    @Autowired
    private ChatRepository chatRepository;

  public String switchTaskAssigner(int taskID, String newAssignerEmail) {
    TaskModel task = taskRepository
      .findById(taskID)
      .orElseThrow(() ->
        new RuntimeException("Task not found with id: " + taskID)
      );

    userRepository
      .findByEmail(newAssignerEmail)
      .orElseThrow(() ->
        new RuntimeException("User not found with email: " + newAssignerEmail)
      );

    Authentication authentication = SecurityContextHolder.getContext()
      .getAuthentication();
    String currentUsername =
      ((UserDetails) authentication.getPrincipal()).getUsername();

    UserModel currentUser = userRepository
      .findByEmail(currentUsername)
      .orElseThrow(() ->
        new RuntimeException("User not found with email: " + currentUsername)
      );

    if (task.getAssignerID() != currentUser.getUserID()) {
      throw new RuntimeException(
        "User does not have permission to switch task assigner"
      );
    }

    socketIOController.sendMessage(new SocketMessageModel("server", newAssignerEmail, "task-" + taskID,
            "User has has been invited to become the task assigner"));

    return "Assigner invite sent successfully";
  }

  public String directlySwitchTaskAssigner(
    int taskID,
    String newAssignerEmail
  ) {
    TaskModel task = taskRepository
      .findById(taskID)
      .orElseThrow(() ->
        new RuntimeException("Task not found with id: " + taskID)
      );

    task.setAssignerID(
      userRepository.findByEmail(newAssignerEmail).get().getUserID()
    );
    taskRepository.save(task);

    return "Assigner switched successfully";
  }

  public String assignerDecision(int taskID, boolean accepted) {
    TaskModel task = taskRepository
      .findById(taskID)
      .orElseThrow(() ->
        new RuntimeException("Task not found with id: " + taskID)
      );

    Authentication authentication = SecurityContextHolder.getContext()
      .getAuthentication();
    String currentUsername =
      ((UserDetails) authentication.getPrincipal()).getUsername();

    UserModel currentUser = userRepository
      .findByEmail(currentUsername)
      .orElseThrow(() ->
        new RuntimeException("User not found with email: " + currentUsername)
      );

    String result;
    int oldAssignerID = task.getAssignerID();
    UserModel oldAssigner = userRepository
      .findById(oldAssignerID)
      .orElseThrow(() ->
        new RuntimeException("User not found with ID: " + oldAssignerID)
      );

    if (accepted) {
      task.setAssignerID(currentUser.getUserID());
      taskRepository.save(task);
      result = "Assigner successfully switched for task with ID: " + taskID;
    } else {
      result = "Assigner switch declined for task with ID: " + taskID;
    }

    socketIOController.sendMessage(new SocketMessageModel("server", oldAssigner.getEmail(), "task-" + taskID, result));

    return result;
  }

  public TaskDTO getTask(int taskId) {
    try {
      TaskModel task = taskRepository
        .findById(taskId)
        .orElseThrow(() ->
          new TaskNotFoundException("Task not found with id: " + taskId)
        );

      // Add null check before creating DTO
      if (task == null) {
        throw new TaskNotFoundException("Task is null for id: " + taskId);
      }

      TaskDTO taskDTO = new TaskDTO(task);
      logger.debug("Created TaskDTO for task ID {}: {}", taskId, taskDTO);

      return taskDTO;
    } catch (TaskNotFoundException e) {
      logger.error("Task not found: {}", e.getMessage());
      throw e;
    } catch (Exception e) {
      logger.error("Error fetching task: {}", e.getMessage(), e);
      throw new RuntimeException("Error fetching task: " + e.getMessage());
    }
  }

  public void sendTaskInfoToUsers(int taskID) {
    TaskModel task = taskRepository
      .findById(taskID)
      .orElseThrow(() ->
        new RuntimeException("Task not found with id: " + taskID)
      );

    Set<UserModel> assignedUsers = task.getAssignedUsers();

    for (UserModel user : assignedUsers) {
      socketIOController.sendMessage(
        new SocketMessageModel(
          "server",
          user.getEmail(),
          "task-" + taskID,
          "Task updated with ID: " + taskID
        )
      );
    }
  }

  public void notifyTaskDeletion(int taskID) {
    TaskModel task = taskRepository
      .findById(taskID)
      .orElseThrow(() ->
        new RuntimeException("Task not found with id: " + taskID)
      );

    Set<UserModel> assignedUsers = task.getAssignedUsers();

    for (UserModel user : assignedUsers) {
      socketIOController.sendMessage(
        new SocketMessageModel(
          "server",
          user.getEmail(),
          "task-" + taskID,
          "Task deleted with ID: " + taskID
        )
      );
    }
  }

  public boolean createTask(
    Integer parentProjectID,
    Integer parentOrgID,
    String taskName,
    String taskDescription,
    Integer assignerID,
    String taskPriority,
    Date dueDate
  ) {
    if (taskRepository.existsTaskByProjectAndName(parentProjectID, taskName)) {
      throw new RuntimeException(
        "Task with given name already exists in project"
      );
    }

    Authentication authentication = SecurityContextHolder.getContext()
      .getAuthentication();
    String currentUsername =
      ((UserDetails) authentication.getPrincipal()).getUsername();

    orgRepository
      .findById(parentOrgID)
      .orElseThrow(() ->
        new RuntimeException("Org not found with id: " + parentOrgID)
      );

    ProjectModel parentProject = projectRepository
      .findById(parentProjectID)
      .orElseThrow(() ->
        new RuntimeException("Project not found with id: " + parentProjectID)
      );

    TaskModel task = new TaskModel();
    task.setParentProjectID(parentProjectID);
    task.setParentOrgID(parentOrgID);
    task.setTaskName(taskName);
    task.setTaskDescription(taskDescription);
    task.setAssignerID(assignerID);
    task.setTaskPriority(taskPriority);
    task.setComplete(false);
    task.setDueDate(dueDate);
    task.setParentProject(parentProject);

    Set<Integer> participants = new HashSet<>();
    participants.add(assignerID);

    ChatModel chat = new ChatModel("task", taskName, participants);
    task.setChatID(chat.getChatID());

    chatRepository.save(chat);
    taskRepository.save(task);

    Set<TaskModel> projectTasks = parentProject.getProjectTasks();
    projectTasks.add(task);
    projectRepository.save(parentProject);

    addUser(task.getTaskID(), currentUsername);

    return true;
  }

  public Set<UserModel> getAssignedUsers(int taskID) {
    TaskModel task = taskRepository
      .findById(taskID)
      .orElseThrow(() ->
        new RuntimeException("Task not found with id: " + taskID)
      );
    return task.getAssignedUsers();
  }

  public Set<TaskDTO> getAllUserTasks(int userID, int orgID, int projectID) {
    Authentication authentication = SecurityContextHolder.getContext()
      .getAuthentication();
    String currentUsername =
      ((UserDetails) authentication.getPrincipal()).getUsername();
    UserModel currentUser = userRepository
      .findByEmail(currentUsername)
      .orElseThrow(() ->
        new RuntimeException("User not found with email: " + currentUsername)
      );
    if (currentUser.getUserID() == userID) {
      Set<TaskModel> tasks = taskRepository.getAllUserTasks(
        userID,
        orgID,
        projectID
      );

      return tasks
        .stream()
        .map(TaskDTO::new)
        .collect(Collectors.toSet());
    } else {
      throw new RuntimeException(
        "User does not have permission to access task list"
      );
    }
  }

  public void updateTask(
    int taskID,
    String taskName,
    String taskDescription,
    int assignerID,
    String taskPriority,
    Date dueDate,
    boolean isComplete
  ) {
    TaskModel task = taskRepository
      .findById(taskID)
      .orElseThrow(() ->
        new RuntimeException("Task not found with id: " + taskID)
      );
    Authentication authentication = SecurityContextHolder.getContext()
      .getAuthentication();
    String currentUsername =
      ((UserDetails) authentication.getPrincipal()).getUsername();

    UserModel currentUser = userRepository
      .findByEmail(currentUsername)
      .orElseThrow(() ->
        new RuntimeException("User not found with email: " + currentUsername)
      );

    if (task.getAssignerID() != currentUser.getUserID()) {
      throw new RuntimeException(
        "User does not have permission to update task"
      );
    }

    if (
      !taskName.equals(task.getTaskName()) &&
      taskRepository.existsTaskByProjectAndName(
        task.getParentProjectID(),
        taskName
      )
    ) {
      throw new RuntimeException(
        "Task with given name already exists in project"
      );
    }

    task.setTaskName(taskName);
    task.setTaskDescription(taskDescription);
    task.setAssignerID(assignerID);
    task.setTaskPriority(taskPriority);
    task.setDueDate(dueDate);
    task.setComplete(isComplete);

    taskRepository.save(task);

    sendTaskInfoToUsers(taskID);
  }

  public void deleteTask(int taskID) {
    TaskModel task = taskRepository
      .findById(taskID)
      .orElseThrow(() ->
        new RuntimeException("Task not found with id: " + taskID)
      );

    Authentication authentication = SecurityContextHolder.getContext()
      .getAuthentication();
    String currentUsername =
      ((UserDetails) authentication.getPrincipal()).getUsername();

    UserModel currentUser = userRepository
      .findByEmail(currentUsername)
      .orElseThrow(() ->
        new RuntimeException("User not found with email: " + currentUsername)
      );

    if (task.getAssignerID() != currentUser.getUserID()) {
      throw new RuntimeException(
        "User does not have permission to delete task"
      );
    }

    ProjectModel parentProject = projectRepository
      .findById(task.getParentOrgID())
      .orElseThrow(() ->
        new RuntimeException(
          "Parent project not found with id: " + task.getParentOrgID()
        )
      );
    Set<TaskModel> projectTasks = parentProject.getProjectTasks();
    projectTasks.remove(task);
    projectRepository.save(parentProject);

    taskRepository.deleteById(taskID);
  }

  public boolean validateAssigner(int taskID, int userID) {
    TaskModel task = taskRepository
      .findById(taskID)
      .orElseThrow(() ->
        new RuntimeException("Task not found with id: " + taskID)
      );
    return (task.getAssignerID() == userID);
  }

  public void updateTaskCompletionStatus(int taskID, boolean isCompleted) {
    TaskModel task = taskRepository
      .findById(taskID)
      .orElseThrow(() ->
        new RuntimeException("Task not found with id: " + taskID)
      );

    Authentication authentication = SecurityContextHolder.getContext()
      .getAuthentication();
    String currentUsername =
      ((UserDetails) authentication.getPrincipal()).getUsername();

    UserModel currentUser = userRepository
      .findByEmail(currentUsername)
      .orElseThrow(() ->
        new RuntimeException("User not found with email: " + currentUsername)
      );
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
      throw new RuntimeException(
        "User does not have permission to update completion status of task"
      );
    }
    taskRepository.updateTaskCompletedStatus(taskID, isCompleted);
  }

  public void addUser(int taskID, String email) {
    UserModel userToAdd = userRepository
      .findByEmail(email)
      .orElseThrow(() ->
        new RuntimeException("User not found with email: " + email)
      );

    TaskModel task = taskRepository
      .findById(taskID)
      .orElseThrow(() ->
        new RuntimeException("Task not found with id: " + taskID)
      );

    Authentication authentication = SecurityContextHolder.getContext()
      .getAuthentication();
    String currentUsername =
      ((UserDetails) authentication.getPrincipal()).getUsername();

    UserModel currentUser = userRepository
      .findByEmail(currentUsername)
      .orElseThrow(() ->
        new RuntimeException("User not found with email: " + currentUsername)
      );

    if (task.getAssignerID() != currentUser.getUserID()) {
      throw new RuntimeException(
        "User does not have permission to add user to task"
      );
    }
    task.getAssignedUsers().add(userToAdd);

    ChatModel chat = chatRepository.findById(task.getChatID()).orElseThrow(() ->
        new RuntimeException("Chat not found with id: " + task.getChatID()));

    chat.addParticipant(userToAdd.getUserID());
    chatRepository.save(chat);
    taskRepository.save(task);
  }

  public void removeUser(int taskID, String email) {
    UserModel userToRemove = userRepository
      .findByEmail(email)
      .orElseThrow(() ->
        new RuntimeException("User not found with email: " + email)
      );

    TaskModel task = taskRepository
      .findById(taskID)
      .orElseThrow(() ->
        new RuntimeException("Task not found with id: " + taskID)
      );

    Authentication authentication = SecurityContextHolder.getContext()
      .getAuthentication();
    String currentUsername =
      ((UserDetails) authentication.getPrincipal()).getUsername();

    UserModel currentUser = userRepository
      .findByEmail(currentUsername)
      .orElseThrow(() ->
        new RuntimeException("User not found with email: " + currentUsername)
      );

    if (task.getAssignerID() != currentUser.getUserID()) {
      throw new RuntimeException(
        "User does not have permission to remove user from  task"
      );
    }
    task.getAssignedUsers().remove(userToRemove);
    ChatModel chat = chatRepository.findById(task.getChatID()).orElseThrow(() ->
            new RuntimeException("Chat not found with id: " + task.getChatID()));

    chat.removeParticipant(userToRemove.getUserID());
    chatRepository.save(chat);
    taskRepository.save(task);
  }

  public String createTaskJson(TaskModel task) {
    String ret =
      "{" +
      "\"taskID\": " +
      task.getTaskID() +
      "," +
      "\"parentProjectID\": " +
      task.getParentProjectID() +
      "," +
      "\"parentOrgID\": " +
      task.getParentOrgID() +
      "," +
      "\"taskName\": \"" +
      task.getTaskName() +
      "\"," +
      "\"taskDescription\": \"" +
      task.getTaskDescription() +
      "\"," +
      "\"assignerID\": " +
      task.getAssignerID() +
      "," +
      "\"taskPriority\": \"" +
      task.getTaskPriority() +
      "\"," +
      "\"dueDate\": \"" +
      task.getDueDate() +
      "\"," +
      "\"isComplete\": " +
      task.isComplete() +
      "," +
      "\"assignedUsers\": [";
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

  public Set<TaskDTO> getAllTasks() {
    List<TaskModel> tasks = taskRepository.findAllSorted();

    return tasks
      .stream()
      .map(TaskDTO::new)
      .collect(Collectors.toSet());
  }
}
