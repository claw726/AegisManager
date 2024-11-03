package com.aegis.project.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.aegis.project.dto.TaskDTO;
import com.aegis.project.exception.TaskNotFoundException;
import com.aegis.project.service.TaskService;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

public class TaskControllerTest {

  @Mock
  private TaskService taskService;

  @InjectMocks
  private TaskController taskController;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void testCreateTask_Success() throws Exception {
    int parentProjectID = 1;
    int parentOrgID = 1;
    String taskName = "Test Task";
    String taskDescription = "Test Description";
    int assignerID = 1;
    String taskPriority = "High";
    String dueDate = "2023-12-31T23:59:59.999Z";
    Date parsedDueDate = new SimpleDateFormat(
      "yyyy-MM-dd'T'HH:mm:ss.SSSX"
    ).parse(dueDate);

    when(
      taskService.createTask(
        parentProjectID,
        parentOrgID,
        taskName,
        taskDescription,
        assignerID,
        taskPriority,
        parsedDueDate
      )
    ).thenReturn(true);

    ResponseEntity<String> response = taskController.createTask(
      parentProjectID,
      parentOrgID,
      taskName,
      taskDescription,
      assignerID,
      taskPriority,
      dueDate
    );

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Task created successfully", response.getBody());
  }

  @Test
  public void testCreateTask_Error() throws Exception {
    int parentProjectID = 1;
    int parentOrgID = 1;
    String taskName = "Test Task";
    String taskDescription = "Test Description";
    int assignerID = 1;
    String taskPriority = "High";
    String dueDate = "2023-12-31T23:59:59.999Z";
    Date parsedDueDate = new SimpleDateFormat(
      "yyyy-MM-dd'T'HH:mm:ss.SSSX"
    ).parse(dueDate);

    when(
      taskService.createTask(
        parentProjectID,
        parentOrgID,
        taskName,
        taskDescription,
        assignerID,
        taskPriority,
        parsedDueDate
      )
    ).thenThrow(new RuntimeException("Error creating task"));

    ResponseEntity<String> response = taskController.createTask(
      parentProjectID,
      parentOrgID,
      taskName,
      taskDescription,
      assignerID,
      taskPriority,
      dueDate
    );

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals(
      "Task already exists or there was an error",
      response.getBody()
    );
  }

  @Test
  public void testGetAllUserTasks_Success() {
    int userID = 1;
    int orgID = 1;
    int projectID = 1;
    Set<TaskDTO> tasks = new HashSet<>();
    tasks.add(new TaskDTO());

    when(taskService.getAllUserTasks(userID, orgID, projectID)).thenReturn(
      tasks
    );

    ResponseEntity<Set<TaskDTO>> response = taskController.getAllUserTasks(
      userID,
      orgID,
      projectID
    );

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(1, response.getBody().size());
  }

  @Test
  public void testGetAllUserTasks_Error() {
    int userID = 1;
    int orgID = 1;
    int projectID = 1;

    when(taskService.getAllUserTasks(userID, orgID, projectID)).thenThrow(
      new RuntimeException("Error fetching tasks")
    );

    try {
      taskController.getAllUserTasks(userID, orgID, projectID);
    } catch (Exception e) {
      assert (e.getMessage().contains("Error fetching tasks"));
    }
  }

  @Test
  public void testValidateAssigner_Success() {
    int userID = 1;
    int taskID = 1;

    when(taskService.validateAssigner(userID, taskID)).thenReturn(true);

    ResponseEntity<Boolean> response = taskController.validateAssigner(
      userID,
      taskID
    );

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(true, response.getBody());
  }

  @Test
  public void testValidateAssigner_Error() {
    int userID = 1;
    int taskID = 1;

    when(taskService.validateAssigner(userID, taskID)).thenThrow(
      new RuntimeException("Error validating assigner")
    );

    ResponseEntity<Boolean> response = taskController.validateAssigner(
      userID,
      taskID
    );

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals(false, response.getBody());
  }

  @Test
  public void testUpdateTaskCompletionStatus_Success() {
    int taskID = 1;
    boolean completed = true;

    doNothing().when(taskService).updateTaskCompletionStatus(taskID, completed);

    ResponseEntity<String> response = taskController.updateTaskCompletionStatus(
      taskID,
      completed
    );

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    assertEquals(
      "Task completion status updated successfully",
      response.getBody()
    );
  }

  @Test
  public void testUpdateTaskCompletionStatus_NotFound() {
    int taskID = 1;
    boolean completed = true;

    doThrow(new RuntimeException("Task not found with id: " + taskID))
      .when(taskService)
      .updateTaskCompletionStatus(taskID, completed);

    ResponseEntity<String> response = taskController.updateTaskCompletionStatus(
      taskID,
      completed
    );

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  public void testGetTask_Success() {
    int taskID = 1;
    TaskDTO taskDTO = new TaskDTO();
    taskDTO.setTaskID(taskID);

    when(taskService.getTask(taskID)).thenReturn(taskDTO);

    ResponseEntity<TaskDTO> response = taskController.getTask(taskID);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(taskID, response.getBody().getTaskID());
  }

  @Test
  public void testGetTask_NotFound() {
    int taskID = 1;

    when(taskService.getTask(taskID)).thenThrow(
      new TaskNotFoundException("Task not found with id: " + taskID)
    );

    ResponseEntity<TaskDTO> response = taskController.getTask(taskID);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  public void testUpdateTask_Success() throws Exception {
    int taskID = 1;
    String taskName = "Updated Task";
    String taskDescription = "Updated Description";
    int assignerID = 1;
    String taskPriority = "High";
    String dueDate = "2023-12-31T23:59:59.999Z";
    boolean isComplete = true;
    Date parsedDueDate = new SimpleDateFormat(
      "yyyy-MM-dd'T'HH:mm:ss.SSSX"
    ).parse(dueDate);

    doNothing()
      .when(taskService)
      .updateTask(
        taskID,
        taskName,
        taskDescription,
        assignerID,
        taskPriority,
        parsedDueDate,
        isComplete
      );

    ResponseEntity<String> response = taskController.updateTask(
      taskID,
      taskName,
      taskDescription,
      assignerID,
      taskPriority,
      dueDate,
      isComplete
    );

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    assertEquals("Task updated successfully", response.getBody());
  }

  @Test
  public void testUpdateTask_NotFound() throws Exception {
    int taskID = 1;
    String taskName = "Updated Task";
    String taskDescription = "Updated Description";
    int assignerID = 1;
    String taskPriority = "High";
    String dueDate = "2023-12-31T23:59:59.999Z";
    boolean isComplete = true;
    Date parsedDueDate = new SimpleDateFormat(
      "yyyy-MM-dd'T'HH:mm:ss.SSSX"
    ).parse(dueDate);

    doThrow(new RuntimeException("Task not found with id: " + taskID))
      .when(taskService)
      .updateTask(
        taskID,
        taskName,
        taskDescription,
        assignerID,
        taskPriority,
        parsedDueDate,
        isComplete
      );

    try {
      taskController.updateTask(
        taskID,
        taskName,
        taskDescription,
        assignerID,
        taskPriority,
        dueDate,
        isComplete
      );
      assert (false);
    } catch (Exception e) {
      assert (e.getMessage().contains("Task not found with id: " + taskID));
    }
  }

  @Test
  public void testDeleteTask_Success() {
    int taskID = 1;

    doNothing().when(taskService).deleteTask(taskID);

    ResponseEntity<String> response = taskController.deleteTask(taskID);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    assertEquals("Task deleted successfully", response.getBody());
  }

  @Test
  public void testDeleteTask_NotFound() {
    int taskID = 1;

    doThrow(new RuntimeException("Task not found with id: " + taskID))
      .when(taskService)
      .deleteTask(taskID);

    ResponseEntity<String> response = taskController.deleteTask(taskID);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  public void testAddUser_Success() {
    int taskID = 1;
    String email = "test@example.com";

    doNothing().when(taskService).addUser(taskID, email);

    ResponseEntity<String> response = taskController.addUser(taskID, email);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("User added successfully", response.getBody());
  }

  @Test
  public void testAddUser_NotFound() {
    int taskID = 1;
    String email = "test@example.com";

    doThrow(new RuntimeException("User not found with email: " + email))
      .when(taskService)
      .addUser(taskID, email);

    ResponseEntity<String> response = taskController.addUser(taskID, email);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  public void testRemoveUser_Success() {
    int taskID = 1;
    String email = "test@example.com";

    doNothing().when(taskService).removeUser(taskID, email);

    ResponseEntity<String> response = taskController.removeUser(taskID, email);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("User removed successfully", response.getBody());
  }

  @Test
  public void testRemoveUser_NotFound() {
    int taskID = 1;
    String email = "test@example.com";

    doThrow(new RuntimeException("User not found with email: " + email))
      .when(taskService)
      .removeUser(taskID, email);

    ResponseEntity<String> response = taskController.removeUser(taskID, email);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  public void testSwitchTaskAssigner_Success() {
    int taskID = 1;
    String newAssignerEmail = "newassigner@example.com";

    when(taskService.switchTaskAssigner(taskID, newAssignerEmail)).thenReturn(
      "Assigner switched successfully"
    );

    ResponseEntity<String> response = taskController.switchTaskAssigner(
      taskID,
      newAssignerEmail
    );

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Assigner switched successfully", response.getBody());
  }

  @Test
  public void testSwitchTaskAssigner_NotFound() {
    int taskID = 1;
    String newAssignerEmail = "newassigner@example.com";

    when(taskService.switchTaskAssigner(taskID, newAssignerEmail)).thenThrow(
      new RuntimeException("Task not found with id: " + taskID)
    );

    ResponseEntity<String> response = taskController.switchTaskAssigner(
      taskID,
      newAssignerEmail
    );

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  public void testAssignerDecision_Success() {
    int taskID = 1;
    boolean accept = true;

    when(taskService.assignerDecision(taskID, accept)).thenReturn(
      "Assigner decision updated successfully"
    );

    ResponseEntity<String> response = taskController.assignerDecision(
      taskID,
      accept
    );

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Assigner decision updated successfully", response.getBody());
  }

  @Test
  public void testAssignerDecision_NotFound() {
    int taskID = 1;
    boolean accept = true;

    when(taskService.assignerDecision(taskID, accept)).thenThrow(
      new RuntimeException("Task not found with id: " + taskID)
    );

    ResponseEntity<String> response = taskController.assignerDecision(
      taskID,
      accept
    );

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  public void testGetAllTasks_Success() {
    Set<TaskDTO> tasks = new HashSet<>();
    tasks.add(new TaskDTO());

    when(taskService.getAllTasks()).thenReturn(tasks);

    ResponseEntity<Set<TaskDTO>> response = taskController.getAllTasks();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(1, response.getBody().size());
  }

  @Test
  public void testGetAllTasks_Error() {
    when(taskService.getAllTasks()).thenThrow(
      new RuntimeException("Error fetching tasks")
    );

    ResponseEntity<Set<TaskDTO>> response = taskController.getAllTasks();

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
  }
}
