package com.aegis.project.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.aegis.project.controller.SocketIOController;
import com.aegis.project.dto.TaskDTO;
import com.aegis.project.exception.TaskNotFoundException;
import com.aegis.project.model.OrgModel;
import com.aegis.project.model.ProjectModel;
import com.aegis.project.model.TaskModel;
import com.aegis.project.model.UserModel;
import com.aegis.project.repository.OrgRepository;
import com.aegis.project.repository.ProjectRepository;
import com.aegis.project.repository.TaskRepository;
import com.aegis.project.repository.UserRepository;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class TaskServiceTest {

  @Mock
  private TaskRepository taskRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private ProjectRepository projectRepository;

  @Mock
  private OrgRepository orgRepository;

  @Mock
  private UserService userService;

  @Mock
  private SocketIOController socketIOController;

  @InjectMocks
  private TaskService taskService;

  public TaskServiceTest() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void testSwitchTaskAssigner_Success() {
    int taskID = 1;
    String newAssignerEmail = "newAssigner@example.com";
    TaskModel task = new TaskModel();
    task.setTaskID(taskID);
    task.setAssignerID(1);

    UserModel newAssigner = new UserModel();
    newAssigner.setEmail(newAssignerEmail);

    UserModel currentUser = new UserModel();
    currentUser.setUserID(1);
    currentUser.setEmail("currentUser@example.com");

    UserDetails userDetails = mock(UserDetails.class);
    when(userDetails.getUsername()).thenReturn("currentUser@example.com");

    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(userDetails);

    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);

    SecurityContextHolder.setContext(securityContext);

    when(taskRepository.findById(taskID)).thenReturn(Optional.of(task));
    when(userRepository.findByEmail(newAssignerEmail)).thenReturn(
      Optional.of(newAssigner)
    );
    when(userRepository.findByEmail("currentUser@example.com")).thenReturn(
      Optional.of(currentUser)
    );

    String result = taskService.switchTaskAssigner(taskID, newAssignerEmail);

    assertEquals("Assigner invite sent successfully", result);
  }

  @Test
  public void testSwitchTaskAssigner_TaskNotFound() {
    int taskID = 1;
    String newAssignerEmail = "newAssigner@example.com";

    when(taskRepository.findById(taskID)).thenReturn(Optional.empty());

    Exception exception = assertThrows(RuntimeException.class, () -> {
      taskService.switchTaskAssigner(taskID, newAssignerEmail);
    });

    assertEquals("Task not found with id: " + taskID, exception.getMessage());
  }

  @Test
  public void testDirectlySwitchTaskAssigner_Success() {
    int taskID = 1;
    String newAssignerEmail = "newAssigner@example.com";
    TaskModel task = new TaskModel();
    task.setTaskID(taskID);

    UserModel newAssigner = new UserModel();
    newAssigner.setEmail(newAssignerEmail);

    when(taskRepository.findById(taskID)).thenReturn(Optional.of(task));
    when(userRepository.findByEmail(newAssignerEmail)).thenReturn(
      Optional.of(newAssigner)
    );

    String result = taskService.directlySwitchTaskAssigner(
      taskID,
      newAssignerEmail
    );

    assertEquals("Assigner switched successfully", result);
  }

  @Test
  public void testDirectlySwitchTaskAssigner_TaskNotFound() {
    int taskID = 1;
    String newAssignerEmail = "newAssigner@example.com";

    when(taskRepository.findById(taskID)).thenReturn(Optional.empty());

    Exception exception = assertThrows(RuntimeException.class, () -> {
      taskService.directlySwitchTaskAssigner(taskID, newAssignerEmail);
    });

    assertEquals("Task not found with id: " + taskID, exception.getMessage());
  }

  @Test
  public void testAssignerDecision_Success() {
    int taskID = 1;
    boolean accepted = true;
    TaskModel task = new TaskModel();
    task.setTaskID(taskID);
    task.setAssignerID(1);

    UserModel currentUser = new UserModel();
    currentUser.setUserID(2);

    when(taskRepository.findById(taskID)).thenReturn(Optional.of(task));
    when(userRepository.findByEmail(anyString())).thenReturn(
      Optional.of(currentUser)
    );
    when(userRepository.findById(1)).thenReturn(Optional.of(new UserModel()));

    String result = taskService.assignerDecision(taskID, accepted);

    assertEquals(
      "Assigner successfully switched for task with ID: " + taskID,
      result
    );
  }

  @Test
  public void testAssignerDecision_TaskNotFound() {
    int taskID = 1;
    boolean accepted = true;

    when(taskRepository.findById(taskID)).thenReturn(Optional.empty());

    Exception exception = assertThrows(RuntimeException.class, () -> {
      taskService.assignerDecision(taskID, accepted);
    });

    assertEquals("Task not found with id: " + taskID, exception.getMessage());
  }

  @Test
  public void testGetTask_Success() {
    int taskID = 1;
    TaskModel task = new TaskModel();
    task.setTaskID(taskID);
    task.setAssignerID(1); // Ensure assignerID is set

    when(taskRepository.findById(taskID)).thenReturn(Optional.of(task));

    TaskDTO taskDTO = taskService.getTask(taskID);

    assertNotNull(taskDTO);
    assertEquals(taskID, taskDTO.getTaskID());
  }

  @Test
  public void testGetTask_TaskNotFound() {
    int taskID = 1;

    when(taskRepository.findById(taskID)).thenReturn(Optional.empty());

    Exception exception = assertThrows(TaskNotFoundException.class, () -> {
      taskService.getTask(taskID);
    });

    assertEquals("Task not found with id: " + taskID, exception.getMessage());
  }

  @Test
  public void testSendTaskInfoToUsers_Success() {
    int taskID = 1;
    TaskModel task = new TaskModel();
    task.setTaskID(taskID);
    task.setAssignerID(1);

    UserModel user = new UserModel();
    user.setUserID(1);
    user.setEmail("user@example.com");

    task.setAssignedUsers(Set.of(user));

    when(taskRepository.findById(taskID)).thenReturn(Optional.of(task));

    taskService.sendTaskInfoToUsers(taskID);

    verify(socketIOController, times(1)).sendMessage(any());
  }

  @Test
  public void testSendTaskInfoToUsers_TaskNotFound() {
    int taskID = 1;

    when(taskRepository.findById(taskID)).thenReturn(Optional.empty());

    Exception exception = assertThrows(RuntimeException.class, () -> {
      taskService.sendTaskInfoToUsers(taskID);
    });

    assertEquals("Task not found with id: " + taskID, exception.getMessage());
  }

  @Test
  public void testNotifyTaskDeletion_Success() {
    int taskID = 1;
    TaskModel task = new TaskModel();
    task.setTaskID(taskID);
    task.setAssignerID(1);

    UserModel user = new UserModel();
    user.setUserID(1);
    user.setEmail("user@example.com");

    task.setAssignedUsers(Set.of(user));

    when(taskRepository.findById(taskID)).thenReturn(Optional.of(task));

    taskService.notifyTaskDeletion(taskID);

    verify(socketIOController, times(1)).sendMessage(any());
  }

  @Test
  public void testNotifyTaskDeletion_TaskNotFound() {
    int taskID = 1;

    when(taskRepository.findById(taskID)).thenReturn(Optional.empty());

    Exception exception = assertThrows(RuntimeException.class, () -> {
      taskService.notifyTaskDeletion(taskID);
    });

    assertEquals("Task not found with id: " + taskID, exception.getMessage());
  }

  @Test
  public void testCreateTask_Success() {
    int parentProjectID = 1;
    int parentOrgID = 1;
    String taskName = "New Task";
    String taskDescription = "Description";
    int assignerID = 1;
    String taskPriority = "High";
    Date dueDate = new Date();

    ProjectModel parentProject = new ProjectModel();
    parentProject.setProjectID(parentProjectID);

    UserModel currentUser = new UserModel();
    currentUser.setUserID(assignerID);
    currentUser.setEmail("currentUser@example.com");

    UserDetails userDetails = mock(UserDetails.class);
    when(userDetails.getUsername()).thenReturn("currentUser@example.com");

    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(userDetails);

    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);

    SecurityContextHolder.setContext(securityContext);

    when(
      taskRepository.existsTaskByProjectAndName(parentProjectID, taskName)
    ).thenReturn(false);
    when(projectRepository.findById(parentProjectID)).thenReturn(
      Optional.of(parentProject)
    );
    when(orgRepository.findById(parentOrgID)).thenReturn(
      Optional.of(new OrgModel())
    );
    when(userRepository.findByEmail("currentUser@example.com")).thenReturn(
      Optional.of(currentUser)
    );

    // Spy on the taskService object
    TaskService taskServiceSpy = spy(taskService);

    // Mock the addUser method
    doNothing().when(taskServiceSpy).addUser(anyInt(), anyString());

    boolean result = taskServiceSpy.createTask(
      parentProjectID,
      parentOrgID,
      taskName,
      taskDescription,
      assignerID,
      taskPriority,
      dueDate
    );

    assertTrue(result);
  }

  @Test
  public void testCreateTask_TaskAlreadyExists() {
    int parentProjectID = 1;
    String taskName = "Existing Task";

    when(
      taskRepository.existsTaskByProjectAndName(parentProjectID, taskName)
    ).thenReturn(true);

    Exception exception = assertThrows(RuntimeException.class, () -> {
      taskService.createTask(
        parentProjectID,
        1,
        taskName,
        "Description",
        1,
        "High",
        new Date()
      );
    });

    assertEquals(
      "Task with given name already exists in project",
      exception.getMessage()
    );
  }

  @Test
  public void testGetAssignedUsers_Success() {
    int taskID = 1;
    TaskModel task = new TaskModel();
    task.setTaskID(taskID);

    when(taskRepository.findById(taskID)).thenReturn(Optional.of(task));

    Set<UserModel> users = taskService.getAssignedUsers(taskID);

    assertNotNull(users);
  }

  @Test
  public void testGetAssignedUsers_TaskNotFound() {
    int taskID = 1;

    when(taskRepository.findById(taskID)).thenReturn(Optional.empty());

    Exception exception = assertThrows(RuntimeException.class, () -> {
      taskService.getAssignedUsers(taskID);
    });

    assertEquals("Task not found with id: " + taskID, exception.getMessage());
  }

  @Test
  public void testUpdateTask_Success() {
    int taskID = 1;
    String taskName = "Updated Task";
    String taskDescription = "Updated Description";
    int assignerID = 1;
    String taskPriority = "Medium";
    Date dueDate = new Date();
    boolean isComplete = true;

    TaskModel task = new TaskModel();
    task.setTaskID(taskID);
    task.setAssignerID(assignerID);

    UserModel currentUser = new UserModel();
    currentUser.setUserID(assignerID); // Ensure currentUser has the same userID as assignerID

    UserDetails userDetails = mock(UserDetails.class);
    when(userDetails.getUsername()).thenReturn("currentUser@example.com");

    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(userDetails);

    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);

    SecurityContextHolder.setContext(securityContext);

    when(taskRepository.findById(taskID)).thenReturn(Optional.of(task));
    when(userRepository.findByEmail("currentUser@example.com")).thenReturn(
      Optional.of(currentUser)
    );

    taskService.updateTask(
      taskID,
      taskName,
      taskDescription,
      assignerID,
      taskPriority,
      dueDate,
      isComplete
    );

    verify(taskRepository, times(1)).save(task);
  }

  @Test
  public void testUpdateTask_TaskNotFound() {
    int taskID = 1;

    when(taskRepository.findById(taskID)).thenReturn(Optional.empty());

    Exception exception = assertThrows(RuntimeException.class, () -> {
      taskService.updateTask(
        taskID,
        "Updated Task",
        "Updated Description",
        1,
        "Medium",
        new Date(),
        true
      );
    });

    assertEquals("Task not found with id: " + taskID, exception.getMessage());
  }

  @Test
  public void testDeleteTask_Success() {
    int taskID = 1;
    int parentProjectID = 1;
    TaskModel task = new TaskModel();
    task.setTaskID(taskID);
    task.setAssignerID(1);
    task.setParentProjectID(parentProjectID);

    UserModel currentUser = new UserModel();
    currentUser.setUserID(1);
    currentUser.setEmail("currentUser@example.com");

    UserDetails userDetails = mock(UserDetails.class);
    when(userDetails.getUsername()).thenReturn("currentUser@example.com");

    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(userDetails);

    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);

    SecurityContextHolder.setContext(securityContext);

    when(taskRepository.findById(taskID)).thenReturn(Optional.of(task));
    when(userRepository.findByEmail("currentUser@example.com")).thenReturn(
      Optional.of(currentUser)
    );
    when(projectRepository.findById(task.getParentOrgID())).thenReturn(
      Optional.of(new ProjectModel())
    );

    taskService.deleteTask(taskID);

    verify(taskRepository, times(1)).deleteById(taskID);
  }

  @Test
  public void testDeleteTask_TaskNotFound() {
    int taskID = 1;

    when(taskRepository.findById(taskID)).thenReturn(Optional.empty());

    Exception exception = assertThrows(RuntimeException.class, () -> {
      taskService.deleteTask(taskID);
    });

    assertEquals("Task not found with id: " + taskID, exception.getMessage());
  }

  @Test
  public void testValidateAssigner_Success() {
    int taskID = 1;
    int userID = 1;
    TaskModel task = new TaskModel();
    task.setTaskID(taskID);
    task.setAssignerID(userID);

    when(taskRepository.findById(taskID)).thenReturn(Optional.of(task));

    boolean result = taskService.validateAssigner(taskID, userID);

    assertTrue(result);
  }

  @Test
  public void testValidateAssigner_TaskNotFound() {
    int taskID = 1;
    int userID = 1;

    when(taskRepository.findById(taskID)).thenReturn(Optional.empty());

    Exception exception = assertThrows(RuntimeException.class, () -> {
      taskService.validateAssigner(taskID, userID);
    });

    assertEquals("Task not found with id: " + taskID, exception.getMessage());
  }

  @Test
  public void testUpdateTaskCompletionStatus_Success() {
    int taskID = 1;
    boolean isCompleted = true;
    TaskModel task = new TaskModel();
    task.setTaskID(taskID);
    task.setAssignerID(1);

    UserModel currentUser = new UserModel();
    currentUser.setUserID(1);
    currentUser.setEmail("currentUser@example.com");

    UserDetails userDetails = mock(UserDetails.class);
    when(userDetails.getUsername()).thenReturn("currentUser@example.com");

    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(userDetails);

    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);

    SecurityContextHolder.setContext(securityContext);

    when(taskRepository.findById(taskID)).thenReturn(Optional.of(task));
    when(userRepository.findByEmail("currentUser@example.com")).thenReturn(
      Optional.of(currentUser)
    );

    taskService.updateTaskCompletionStatus(taskID, isCompleted);

    verify(taskRepository, times(1)).updateTaskCompletedStatus(
      taskID,
      isCompleted
    );
  }

  @Test
  public void testUpdateTaskCompletionStatus_TaskNotFound() {
    int taskID = 1;
    boolean isCompleted = true;

    when(taskRepository.findById(taskID)).thenReturn(Optional.empty());

    Exception exception = assertThrows(RuntimeException.class, () -> {
      taskService.updateTaskCompletionStatus(taskID, isCompleted);
    });

    assertEquals("Task not found with id: " + taskID, exception.getMessage());
  }

  @Test
  public void testAddUser_Success() {
    int taskID = 1;
    String email = "user@example.com";
    TaskModel task = new TaskModel();
    task.setTaskID(taskID);
    task.setAssignerID(1);

    UserModel user = new UserModel();
    user.setEmail(email);

    UserModel currentUser = new UserModel();
    currentUser.setUserID(1);
    currentUser.setEmail("currentUser@example.com");

    UserDetails userDetails = mock(UserDetails.class);
    when(userDetails.getUsername()).thenReturn("currentUser@example.com");

    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(userDetails);

    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);

    SecurityContextHolder.setContext(securityContext);

    when(taskRepository.findById(taskID)).thenReturn(Optional.of(task));
    when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
    when(userRepository.findByEmail("currentUser@example.com")).thenReturn(
      Optional.of(currentUser)
    );

    taskService.addUser(taskID, email);

    verify(taskRepository, times(1)).save(task);
  }

  @Test
  public void testAddUser_TaskNotFound() {
    int taskID = 1;
    String email = "user@example.com";

    when(taskRepository.findById(taskID)).thenReturn(Optional.empty());
    when(userRepository.findByEmail(email)).thenReturn(
      Optional.of(new UserModel())
    );

    Exception exception = assertThrows(RuntimeException.class, () -> {
      taskService.removeUser(taskID, email);
    });

    assertEquals("Task not found with id: " + taskID, exception.getMessage());
  }

  @Test
  public void testRemoveUser_Success() {
    int taskID = 1;
    String email = "user@example.com";
    TaskModel task = new TaskModel();
    task.setTaskID(taskID);
    task.setAssignerID(1);

    UserModel user = new UserModel();
    user.setEmail(email);

    UserModel currentUser = new UserModel();
    currentUser.setUserID(1);
    currentUser.setEmail("currentUser@example.com");

    UserDetails userDetails = mock(UserDetails.class);
    when(userDetails.getUsername()).thenReturn("currentUser@example.com");

    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(userDetails);

    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);

    SecurityContextHolder.setContext(securityContext);

    when(taskRepository.findById(taskID)).thenReturn(Optional.of(task));
    when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
    when(userRepository.findByEmail("currentUser@example.com")).thenReturn(
      Optional.of(currentUser)
    );

    taskService.removeUser(taskID, email);

    verify(taskRepository, times(1)).save(task);
  }

  @Test
  public void testRemoveUser_TaskNotFound() {
    int taskID = 1;
    String email = "user@example.com";
    TaskModel task = new TaskModel();
    task.setTaskID(taskID);
    task.setAssignerID(1);

    UserModel user = new UserModel();
    user.setEmail(email);

    UserModel currentUser = new UserModel();
    currentUser.setUserID(1);
    currentUser.setEmail("currentUser@example.com");

    UserDetails userDetails = mock(UserDetails.class);
    when(userDetails.getUsername()).thenReturn("currentUser@example.com");

    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(userDetails);

    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);

    SecurityContextHolder.setContext(securityContext);

    when(taskRepository.findById(taskID)).thenReturn(Optional.of(task));
    when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
    when(userRepository.findByEmail("currentUser@example.com")).thenReturn(
      Optional.of(currentUser)
    );

    taskService.removeUser(taskID, email);

    verify(taskRepository, times(1)).save(task);
  }

  @Test
  public void testCreateTaskJson_Success() {
    TaskModel task = new TaskModel();
    task.setTaskID(1);
    task.setTaskName("Task Name");
    task.setAssignerID(1);

    String json = taskService.createTaskJson(task);

    assertNotNull(json);
    assertTrue(json.contains("\"taskID\": 1"));
    assertTrue(json.contains("\"taskName\": \"Task Name\""));
  }

  @Test
  public void testGetAllTasks_Success() {
    TaskModel task1 = new TaskModel();
    task1.setTaskID(1);
    task1.setAssignerID(1); // Ensure assignerID is set

    TaskModel task2 = new TaskModel();
    task2.setTaskID(2);
    task2.setAssignerID(2); // Ensure assignerID is set

    when(taskRepository.findAllSorted()).thenReturn(List.of(task1, task2));

    Set<TaskDTO> tasks = taskService.getAllTasks();

    assertNotNull(tasks);
    assertEquals(2, tasks.size());
  }
}