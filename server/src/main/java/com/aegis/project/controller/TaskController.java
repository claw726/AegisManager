package com.aegis.project.controller;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aegis.project.service.TaskService;

@RestController
@RequestMapping("api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/createTask")
    public ResponseEntity<String> createTask(@RequestParam int parentProjectID, @RequestParam int parentOrgID, @RequestParam String taskName, @RequestParam String taskDescription, @RequestParam int assignerID, @RequestParam String taskPriority, @RequestParam Date dueDate) {
        // Log the input parameters
        logger.info("Received task creation request with parent project ID: {}, parent org ID: {}, name: {}, description: {}, assigner ID: {}, priority: {}, due date: {}", parentProjectID, parentOrgID, taskName, taskDescription, assignerID, taskPriority, dueDate);
        try {
            if (taskService.createTask(parentProjectID, parentOrgID, taskName, taskDescription, assignerID, taskPriority, dueDate)) {
                logger.info("Task created successfully for task name: {}", taskName);
                return ResponseEntity.ok("Task created successfully");
            } else {
                logger.warn("Task already exists or there was an error for task name: {}", taskName);
                return ResponseEntity.badRequest().body("Task already exists or there was an error");
            }
        } catch (Exception e) {
            logger.error("Error creating task with name: {}: {}", taskName, e.getMessage());
            return ResponseEntity.badRequest().body("Task already exists or there was an error");
        }
    }

    @GetMapping("/getAllUserTasks")
    public ResponseEntity<String> getAllUserTasks(@RequestParam int userID, @RequestParam(required = false, defaultValue = "-1") int orgID, @RequestParam(required = false, defaultValue = "-1") int projectID) {
        logger.info("Received task retrieval request for user ID: {}, org ID: {}, projectID: {} ", userID, orgID, projectID);
        try {
            return ResponseEntity.ok(taskService.getAllUserTasks(userID, orgID, projectID));
        } catch (Exception e) {
            logger.error("Error retrieving tasks for user ID {}, org ID {}, projectID {}: {}", userID, orgID, projectID, e.getMessage());
            return null;
        }
    }

    @GetMapping("/validateAssigner")
    public ResponseEntity<Boolean> validateAssigner(@RequestParam int userID, int taskID) {
        logger.info("Received validation request for user ID: {}, task ID: {} ", userID, taskID);
        try {
            return ResponseEntity.ok(taskService.validateAssigner(userID, taskID));
        } catch (Exception e) {
            logger.error("Error validating assigner for user ID {}, task ID {}: {}", userID, taskID, e.getMessage());
            return null;
        }
    }

    @PostMapping("/updateTaskCompletionStatus")
    public ResponseEntity<String> updateTaskCompletionStatus(int taskID, boolean completed) {
        try {
            taskService.updateTaskCompletionStatus(taskID, completed);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Task completion status updated successfully");
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Task not found with ID: " + taskID)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            else if (e.getMessage().contains("User does not have permission")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
            }
            else if (e.getMessage().contains("User not found with email")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            }
            else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }
        }
    }

    @GetMapping("/{taskID}/getTask")
    public ResponseEntity<String> getTask(@PathVariable int taskID) {
        try {
            return ResponseEntity.ok(taskService.getTask(taskID));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Task not found with ID: " + taskID)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }
        }
    }

    @PostMapping("/{taskID}/update")
    public ResponseEntity<String> updateTask(@PathVariable int taskID, @RequestParam String taskName, @RequestParam String taskDescription, @RequestParam int assignerID, @RequestParam String taskPriority, @RequestParam Date dueDate, @RequestParam boolean isComplete) {
        try {
            taskService.updateTask(taskID, taskName, taskDescription, assignerID, taskPriority, dueDate, isComplete);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Task updated successfully");
        }
        catch (RuntimeException e) {
            if (e.getMessage().equals("Task not found with ID: " + taskID)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else if (e.getMessage().contains("User")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }
        }
    }

    @DeleteMapping("/{taskID}/deleteTask")
    public ResponseEntity<String> deleteTask(int taskID) {
        try {
            taskService.deleteTask(taskID);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Taskdeleted successfully");
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Task not found with ID: " + taskID)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else if (e.getMessage().contains("User")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }
        }
    }

    @PostMapping("/{taskID}/addUser")
    public ResponseEntity<String> addUser(@PathVariable int taskID, @RequestParam String email) {
        try {
            taskService.addUser(taskID, email);
            return ResponseEntity.ok("User added successfully");
        }
        catch (RuntimeException e){
            if (e.getMessage().contains("User not found with email: ")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            } 
            else if (e.getMessage().equals("Task not found with id: " + taskID)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            else if (e.getMessage().equals("User does not have permission to add user to task")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
            else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        }
    }

    @PostMapping("/{taskID}/removeUser")
    public ResponseEntity<String> removeUser(@PathVariable int taskID, @RequestParam String email) {
        try {
            taskService.removeUser(taskID, email);
            return ResponseEntity.ok("User removed successfully");
        }
        catch (RuntimeException e){
            if (e.getMessage().contains("User not found with email: ")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            } 
            else if (e.getMessage().equals("Task not found with id: " + taskID)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            else if (e.getMessage().equals("User does not have permission to remove user from task")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
            else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        }
    }

    @PutMapping("/{taskID}/{newAssignerEmail}/assigner")
    public ResponseEntity<String> switchTaskAssigner(@PathVariable int taskID, @PathVariable String newAssignerEmail) {
        try {
            return ResponseEntity.ok(taskService.switchTaskAssigner(taskID, newAssignerEmail));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Task not found with ID: " + taskID)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else if (e.getMessage().equals("User not found with email: " + newAssignerEmail)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else if (e.getMessage().equals("User does not have permission to switch task assigner")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }
        }
    }

    
}
