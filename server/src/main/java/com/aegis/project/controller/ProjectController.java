package com.aegis.project.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.aegis.project.dto.ProjectDTO;

import com.aegis.project.service.ProjectService;

import java.util.List;

@RestController
@RequestMapping("api/projects")
public class ProjectController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private ProjectService projectService;

    @PostMapping("/createProject")
    public ResponseEntity<String> createProject(@RequestParam String projectName, @RequestParam String projectDescription,
                                                @RequestParam int projectOwnerID, @RequestParam int parentOrgID, @RequestParam String encodedImage) {
        try {
            logger.info("Received project creation request with name: {}, description: {}, owner ID: {}, parent org ID: {}",
                    projectName, projectDescription, projectOwnerID, parentOrgID);
            projectService.createProject(projectName, projectDescription, projectOwnerID, parentOrgID, encodedImage);
            logger.info("Project created successfully with name: {}", projectName);
            return ResponseEntity.ok("Project created successfully");
        }
        catch (RuntimeException e) {
            logger.error("Error creating project: " + e.getMessage());
            if (e.getMessage().contains("Project with given name already exists in org")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            else {
                return ResponseEntity.internalServerError().body("There was an error creating the project");
            }
            
        }
    }

    @GetMapping("/{projectID}/getProject")
    public ResponseEntity<ProjectDTO> getProject(@PathVariable int projectID) {
        try {
            logger.info("Received request to get project with ID: {}", projectID);
            return ResponseEntity.ok(projectService.getProject(projectID));
        }
        catch (RuntimeException e) {
            if (e.getMessage().equals("Project not found with ID: " + projectID)) {
                logger.info("Project not found with ID: {}", projectID);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            } else {
                logger.error("Error getting project: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        }
    }

    @PostMapping("/{projectID}/update")
    public ResponseEntity<String> updateProject(@PathVariable int projectID, @RequestParam String projectName, @RequestParam String projectDescription, @RequestParam int projectOwnerID){
        try {
            projectService.updateProject(projectID, projectName, projectDescription, projectOwnerID);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Project updated successfully");
        }
        catch (RuntimeException e) {
            if (e.getMessage().equals("Project not found with ID: " + projectID)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else if (e.getMessage().contains("User")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }
        }
    }

    @DeleteMapping("/{projectID}/deleteProject")
    public ResponseEntity<String> deleteProject(@PathVariable int projectID) {
        try {
            projectService.deleteProject(projectID);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Project deleted successfully");
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Project not found with ID: " + projectID)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else if (e.getMessage().contains("User")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }
        }
    }

    @PostMapping ("/{projectID}/addUsers")
    public ResponseEntity<String> addUsers(@PathVariable int projectID, @RequestBody List<Integer> userIDs) {
        try {
            projectService.addUsers(projectID, userIDs);
            return ResponseEntity.ok("Users added successfully");
        }
        catch (RuntimeException e){
            if (e.getMessage().contains("User not found with ID: ")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            else if (e.getMessage().equals("Project not found with id: " + projectID)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            else if (e.getMessage().equals("User does not have permission to add user to project")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
            }
            else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }
        }
    }

    @PostMapping("/{projectID}/addUser")
    public ResponseEntity<String> addUser(@PathVariable int projectID, @RequestParam String email) {
        try {
            projectService.addUser(projectID, email);
            return ResponseEntity.ok("User added successfully");
        }
        catch (RuntimeException e){
            if (e.getMessage().contains("User not found with email: ")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            } 
            else if (e.getMessage().equals("Project not found with id: " + projectID)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            else if (e.getMessage().equals("User does not have permission to add user to project")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
            else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        }
    }

    @PostMapping("/{projectID}/removeUser")
    public ResponseEntity<String> removeUser(@PathVariable int projectID, @RequestParam String email) {
        try {
            projectService.removeUser(projectID, email);
            return ResponseEntity.ok("User removed successfully");
        }
        catch (RuntimeException e){
            if (e.getMessage().contains("User not found with email: ")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            } 
            else if (e.getMessage().equals("Project not found with id: " + projectID)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            else if (e.getMessage().equals("User does not have permission to remove user from project")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
            else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        }
    }
}
