package com.aegis.project.controller;

import com.aegis.project.dto.TaskDTO;
import com.aegis.project.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @GetMapping("/{taskID}")
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
