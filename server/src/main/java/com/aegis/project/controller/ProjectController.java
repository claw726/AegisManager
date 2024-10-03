package com.aegis.project.controller;

import com.aegis.project.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @DeleteMapping("/{projectID}")
    public ResponseEntity<String> deleteProject(int projectID) {
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
}
