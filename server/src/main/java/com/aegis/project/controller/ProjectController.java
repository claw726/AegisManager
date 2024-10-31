package com.aegis.project.controller;

import java.util.List;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aegis.project.dto.ProjectDTO;
import com.aegis.project.dto.TaskDTO;
import com.aegis.project.dto.UserDTO;
import com.aegis.project.exception.ProjectFetchException;
import com.aegis.project.exception.UserNotFoundException;
import com.aegis.project.service.ProjectService;

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
        } catch (RuntimeException e) {
            logger.error("Error creating project: " + e.getMessage());
            if (e.getMessage().contains("Project with given name already exists in org")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            } else if (e.getMessage().contains("Org not found with id:")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else {
                return ResponseEntity.internalServerError().body("There was an error creating the project");
            }

        }
    }

    @GetMapping("/{projectID}/getProject")
    public ResponseEntity<ProjectDTO> getProject(@PathVariable int projectID) {
        try {
            logger.info("Received request to get project with ID: {}", projectID);
            return ResponseEntity.ok(projectService.getProject(projectID));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Project not found with id:")) {
                logger.info("Project not found with ID: {}", projectID);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            } else if (e.getMessage().contains("User not found with email:")) {
                logger.info("User not found with email: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            } else if (e.getMessage().contains("User does not have permission to get project")) {
                logger.info("User does not have permission to get project: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            } else {
                logger.error("Error getting project: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        }
    }

    @PostMapping("/{projectID}/update")
    public ResponseEntity<String> updateProject(@PathVariable int projectID, @RequestParam String projectName,
            @RequestParam String projectDescription, @RequestParam int projectOwnerID,
            @RequestParam String encodedImage) {
        try {
            projectService.updateProject(projectID, projectName, projectDescription, projectOwnerID, encodedImage);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Project updated successfully");
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Project not found with id:")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else if (e.getMessage().contains("User not found with email:")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else if (e.getMessage().contains("User does not have permission to update project")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
            } else if (e.getMessage().contains("Project with given name already exists in org")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
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
            if (e.getMessage().contains("Project not found with id:")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else if (e.getMessage().contains("User not found with email:")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else if (e.getMessage().contains("User does not have permission to delete project")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }
        }
    }

    @PostMapping("/{projectID}/addUsers")
    public ResponseEntity<String> addUsers(@PathVariable int projectID, @RequestBody List<Integer> userIDs) {
        try {
            projectService.addUsers(projectID, userIDs);
            return ResponseEntity.ok("Users added successfully");
        } catch (RuntimeException e) {
            if (e.getMessage().contains("User not found with id: ")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else if (e.getMessage().contains("Project not found with id:")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else if (e.getMessage().contains("User does not have permission to add user to project")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }
        }
    }

    @PostMapping("/{projectID}/addUser")
    public ResponseEntity<String> addUser(@PathVariable int projectID, @RequestParam String email) {
        try {
            projectService.addUser(projectID, email);
            return ResponseEntity.ok("User added successfully");
        } catch (RuntimeException e) {
            if (e.getMessage().contains("User not found with email: ")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else if (e.getMessage().contains("Project not found with id:")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else if (e.getMessage().contains("User does not have permission to add user to project")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }
        }
    }

    @PostMapping("/{projectID}/removeUser")
    public ResponseEntity<String> removeUser(@PathVariable int projectID, @RequestParam String email) {
        try {
            projectService.removeUser(projectID, email);
            return ResponseEntity.ok("User removed successfully");
        } catch (RuntimeException e) {
            if (e.getMessage().contains("User not found with email:")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else if (e.getMessage().contains("Project not found with id:")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else if (e.getMessage().contains("Parent org not found with id:")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else if (e.getMessage().contains("User does not have permission to remove user from project")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }
        }
    }

    @GetMapping("/{projectID}/getAllTasksFromProject")
    public ResponseEntity<Set<TaskDTO>> getAllTasksFromProject(@PathVariable int projectID) {
        try {
            return ResponseEntity.ok(projectService.getAllTasksFromProject(projectID));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Project not found with id:")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            } else if (e.getMessage().contains("User not found with email:")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            } else if (e.getMessage().contains("User does not have permission to get tasks from project")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        }
    }

    @GetMapping("/{projectID}/getUsers")
    public ResponseEntity<Set<UserDTO>> getUsers(@PathVariable int projectID) {
        try {
            return ResponseEntity.ok(projectService.getAssignedUsers(projectID));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Project not found with ID: " + projectID)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            } else if (e.getMessage().contains("User not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            } else if (e.getMessage().contains("User does not have permission to view users")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        }
    }

    @GetMapping("/{projectID}/getAllTasksForUser")
    public ResponseEntity<Set<TaskDTO>> getAllTasksForUser(@PathVariable int projectID, @RequestParam String email) {
        try {
            return ResponseEntity.ok(projectService.getAllTasksForUser(projectID, email));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Project not found with ID: " + projectID)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            } else if (e.getMessage().contains("User not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            } else if (e.getMessage().contains("User does not have permission to view users")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        }
    }

    @PostMapping("/{projectID}/changeArchivedStatus")
    public ResponseEntity<String> changeArchivedStatus(@PathVariable int projectID, @RequestParam boolean isArchived) {
        try {
            projectService.changeArchivedStatus(projectID, isArchived);
            return ResponseEntity.ok("Project archived status changed to " + isArchived + " successfully");
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Project not found with id:")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else if (e.getMessage().contains("User not found with email:")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else if (e.getMessage().contains("User does not have permission to change archived status")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }
        }
    }

    @GetMapping("/getAllUserProjects")
    public ResponseEntity<?> getAllUserProjects(@RequestParam String email) {
        try {
            Set<ProjectDTO> projects = projectService.getAllUserProjects(email);
            return ResponseEntity.ok(projects);
        } catch (UserNotFoundException e) {
            logger.error("User not found: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Error fetching Projects");
        } catch (ProjectFetchException e) {
            logger.error("Error fetching projects: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching projects");
        } catch (Exception e) {
            logger.error("Unexpected Error: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occured");
        }
    }
}
