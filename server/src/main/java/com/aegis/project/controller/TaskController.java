package com.aegis.project.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.aegis.project.dto.FileDTO;
import com.aegis.project.dto.TaskDTO;
import com.aegis.project.service.TaskService;

@RestController
@RequestMapping("api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    private static final Logger logger = LoggerFactory.getLogger(
            TaskController.class
    );

    @PostMapping("/createTask")
    public ResponseEntity<String> createTask(
            @RequestParam Integer parentProjectID,
            @RequestParam Integer parentOrgID,
            @RequestParam String taskName,
            @RequestParam String taskDescription,
            @RequestParam Integer assignerID,
            @RequestParam String taskPriority,
            @RequestParam String dueDate
    ) {
        // Log the input parameters
        logger.info(
                "Received task creation request with parent project ID: {}, parent org ID: {}, name: {}, description: {}, assigner ID: {}, priority: {}, due date: {}",
                parentProjectID,
                parentOrgID,
                taskName,
                taskDescription,
                assignerID,
                taskPriority,
                dueDate
        );
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ss.SSSX"
            );
            Date parsedDueDate = dateFormat.parse(dueDate);
            if (taskService.createTask(
                    parentProjectID,
                    parentOrgID,
                    taskName,
                    taskDescription,
                    assignerID,
                    taskPriority,
                    parsedDueDate
            )) {
                logger.info("Task created successfully for task name: {}", taskName);
                return ResponseEntity.ok("Task created successfully");
            } else {
                logger.warn(
                        "Task already exists or there was an error for task name: {}",
                        taskName
                );
                return ResponseEntity.badRequest()
                        .body("Task already exists or there was an error");
            }
        } catch (Exception e) {
            logger.error(
                    "Error creating task with name: {}: {}",
                    taskName,
                    e.getMessage()
            );
            return ResponseEntity.badRequest()
                    .body("Task already exists or there was an error");
        }
    }

    @GetMapping("/getAllUserTasks")
    public ResponseEntity<Set<TaskDTO>> getAllUserTasks(
            @RequestParam int userID,
            @RequestParam(required = false, defaultValue = "-1") int orgID,
            @RequestParam(required = false, defaultValue = "-1") int projectID
    ) {
        logger.info(
                "Received task retrieval request for user ID: {}, org ID: {}, projectID: {} ",
                userID,
                orgID,
                projectID
        );
        try {
            return ResponseEntity.ok(
                    taskService.getAllUserTasks(userID, orgID, projectID)
            );
        } catch (Exception e) {
            logger.error(
                    "Error retrieving tasks for user ID {}, org ID {}, projectID {}: {}",
                    userID,
                    orgID,
                    projectID,
                    e.getMessage()
            );
            if (e.getMessage().contains("User not found with email:")) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
            } else if (e
                    .getMessage()
                    .contains("User does not have permission to access task list")) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
            } else {
                throw new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        e.getMessage()
                );
            }
        }
    }

    @GetMapping("/validateAssigner")
    public ResponseEntity<Boolean> validateAssigner(
            @RequestParam int userID,
            int taskID
    ) {
        logger.info(
                "Received validation request for user ID: {}, task ID: {} ",
                userID,
                taskID
        );
        try {
            return ResponseEntity.ok(taskService.validateAssigner(userID, taskID));
        } catch (Exception e) {
            logger.error(
                    "Error validating assigner for user ID {}, task ID {}: {}",
                    userID,
                    taskID,
                    e.getMessage()
            );
            if (e.getMessage().contains("Task not found with id:")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(false);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                        false
                );
            }
        }
    }

    @PostMapping("/updateTaskCompletionStatus")
    public ResponseEntity<String> updateTaskCompletionStatus(
            @RequestParam int taskID,
            @RequestParam boolean completed
    ) {
        try {
            taskService.updateTaskCompletionStatus(taskID, completed);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(
                    "Task completion status updated successfully"
            );
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Task not found with id:")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else if (e.getMessage().contains("User not found with email")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else if (e.getMessage().contains("User does not have permission")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                        e.getMessage()
                );
            }
        }
    }

    @GetMapping("/{taskID}/getTask")
    public ResponseEntity<TaskDTO> getTask(@PathVariable int taskID) {
        try {
            TaskDTO task = taskService.getTask(taskID);
            logger.info("Got task: " + task);
            return ResponseEntity.ok(task);
        } catch (RuntimeException e) {
            logger.error("Error fetching task with ID: " + taskID, e);
            if (e.getMessage().contains("Task not found with id:")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                        null
                );
            }
        }
    }

    @PostMapping("/{taskID}/update")
    public ResponseEntity<String> updateTask(
            @PathVariable int taskID,
            @RequestParam String taskName,
            @RequestParam String taskDescription,
            @RequestParam int assignerID,
            @RequestParam String taskPriority,
            @RequestParam String dueDate,
            @RequestParam boolean isComplete
    ) {
        try {
            Date parsedDueDate = new SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ss.SSSX"
            ).parse(dueDate);
            taskService.updateTask(
                    taskID,
                    taskName,
                    taskDescription,
                    assignerID,
                    taskPriority,
                    parsedDueDate,
                    isComplete
            );
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(
                    "Task updated successfully"
            );
        } catch (ParseException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid date format",
                    e
            );
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Task not found with id:")) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
            } else if (e.getMessage().contains("User not found with email")) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
            } else if (e.getMessage().contains("User does not have permission")) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
            } else if (e
                    .getMessage()
                    .contains("Task with given name already exists in project")) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
            } else {
                throw new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        e.getMessage()
                );
            }
        }
    }

    @DeleteMapping("/{taskID}/deleteTask")
    public ResponseEntity<String> deleteTask(@PathVariable int taskID) {
        try {
            taskService.deleteTask(taskID);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(
                    "Task deleted successfully"
            );
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Task not found with id:")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else if (e.getMessage().contains("User not found with email")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else if (e.getMessage().contains("User does not have permission")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                        e.getMessage()
                );
            }
        }
    }

    @PostMapping("/{taskID}/addUser")
    public ResponseEntity<String> addUser(
            @PathVariable int taskID,
            @RequestParam String email
    ) {
        try {
            taskService.addUser(taskID, email);
            return ResponseEntity.ok("User added successfully");
        } catch (RuntimeException e) {
            if (e.getMessage().contains("User not found with email: ")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else if (e.getMessage().equals("Task not found with id: " + taskID)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else if (e
                    .getMessage()
                    .equals("User does not have permission to add user to task")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                        e.getMessage()
                );
            }
        }
    }

    @PostMapping("/{taskID}/removeUser")
    public ResponseEntity<String> removeUser(
            @PathVariable int taskID,
            @RequestParam String email
    ) {
        try {
            taskService.removeUser(taskID, email);
            return ResponseEntity.ok("User removed successfully");
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Task not found with id:")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else if (e.getMessage().contains("User not found with email")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else if (e.getMessage().contains("User does not have permission")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                        e.getMessage()
                );
            }
        }
    }

    @PutMapping("/{taskID}/{newAssignerEmail}/assigner")
    public ResponseEntity<String> switchTaskAssigner(
            @PathVariable int taskID,
            @PathVariable String newAssignerEmail
    ) {
        try {
            return ResponseEntity.ok(
                    taskService.switchTaskAssigner(taskID, newAssignerEmail)
            );
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Task not found with id:")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else if (e.getMessage().contains("User not found with email")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else if (e.getMessage().contains("User does not have permission")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                        e.getMessage()
                );
            }
        }
    }

    @PostMapping("/{taskID}/assignerDecision")
    public ResponseEntity<String> assignerDecision(
            @PathVariable int taskID,
            @RequestParam boolean accept
    ) {
        try {
            taskService.assignerDecision(taskID, accept);
            return ResponseEntity.ok("Assigner decision updated successfully");
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Task not found with id:")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else if (e.getMessage().contains("User not found with email")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                        e.getMessage()
                );
            }
        }
    }

    @GetMapping("/getAllTasks")
    public ResponseEntity<Set<TaskDTO>> getAllTasks() {
        try {
            return ResponseEntity.ok(taskService.getAllTasks());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/{taskID}/addFile")
    public ResponseEntity<String> addFile(@PathVariable int taskID, @RequestBody FileUploadRequest fileUploadRequest) {
        String fileName;
        String fileType;
        String fileContents;
        int uploaderID;
        System.out.println("Accessed");
        try {
            fileName = fileUploadRequest.getFileName();
            fileType = fileUploadRequest.getFileType();
            fileContents = fileUploadRequest.getFileContents();
            uploaderID = fileUploadRequest.getUploaderID();
        } catch (RuntimeException e) {
            logger.info("Invalid request: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        try {
            logger.info("Received file addition for task ID: {}, file name: {}, user ID: {} ", taskID, fileName, uploaderID);
            return ResponseEntity.ok(taskService.addFile(taskID, fileName, fileType, fileContents, uploaderID));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Task not found with id:")) {
                logger.info("No task found with ID: {}", taskID);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            } else if (e.getMessage().contains("User not found with id:")) {
                logger.info("No user found with ID: {}", uploaderID);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            } else if (e.getMessage().contains("Invalid Data URL format")) {
                logger.info("Invalid data URL format");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            } else if (e.getMessage().contains("does not belong to the task with id")) {
                logger.info("User with id: {} does not belong to the task with id: {}", uploaderID, taskID);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            } else if (e.getMessage().contains("File already exists with the same name, type and contents as file:")) {
                logger.info("File already exists with the same name, type and contents as file {} in task with id: {}", fileName, taskID);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
            } else if (e.getMessage().contains("Error parsing file contents:")) {
                logger.info("Error parsing file contents for file with name {}: {}", fileName, e.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }
            //"
        }
    }

    @GetMapping("/{taskID}/getFile")
    public ResponseEntity<FileDTO> getFile(@PathVariable int taskID, @RequestParam int fileID) {
        try {
            FileDTO file = taskService.getFile(taskID, fileID);
            logger.info("Got file: " + file);
            return ResponseEntity.ok(file);
        } catch (RuntimeException e) {
            logger.error("Error fetching task with ID: " + taskID, e);
            if (e.getMessage().contains("Task not found with id:")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            } else if (e.getMessage().contains("File not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                        null
                );
            }
        }
    }

    @GetMapping("/{taskID}/getAllFiles")
    public ResponseEntity<Set<FileDTO>> getAllTaskFiles(@PathVariable int taskID) {
        try {
            return ResponseEntity.ok(taskService.getAllTaskFiles(taskID));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/{taskID}/deleteFile")
    public ResponseEntity<String> deleteFile(@PathVariable int taskID, @RequestParam int fileID) {
        try {
            taskService.deleteFile(taskID, fileID);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(
                    "File deleted successfully"
            );
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Task not found with id:")) {
                logger.info("No task found with ID: {}", taskID);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else if (e.getMessage().contains("User not found with email")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else if (e.getMessage().contains("User does not have permission")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
            } else if (e.getMessage().contains("File not found with id:")) {
                logger.info("No file found with ID: {} in task with ID: {}", fileID, taskID);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else {
                logger.info(e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                        e.getMessage()
                );
            }
        }
    }

    private static class FileUploadRequest {

        private String fileName;
        private String fileType;
        private String fileContents; // Base64 encoded file content
        private int uploaderID;

        // Getters and setters
        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getFileType() {
            return fileType;
        }

        public void setFileType(String fileType) {
            this.fileType = fileType;
        }

        public String getFileContents() {
            return fileContents;
        }

        public void setFileContents(String fileContents) {
            this.fileContents = fileContents;
        }

        public int getUploaderID() {
            return uploaderID;
        }

        public void setUploaderID(int uploaderID) {
            this.uploaderID = uploaderID;
        }
    }

}
