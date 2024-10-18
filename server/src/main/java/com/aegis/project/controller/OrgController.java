package com.aegis.project.controller;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aegis.project.dto.OrgDTO;
import com.aegis.project.dto.ProjectDTO;
import com.aegis.project.dto.UserDTO;
import com.aegis.project.model.OrgModel;
import com.aegis.project.service.OrgService;

import io.jsonwebtoken.lang.Collections;

import com.aegis.project.dto.ProjectDTO;

@RestController
@RequestMapping("api/orgs")
public class OrgController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private OrgService orgService;

    @PostMapping("/createOrg")
    public ResponseEntity<String> createOrg(@RequestParam String orgName, @RequestParam String orgDescription,
            @RequestParam int orgOwnerID, @RequestParam String encodedImage) {
        try {
            logger.info("Received org creation request with name: {}, description: {}, owner ID: {}, encodedImage: {}",
                    orgName, orgDescription, orgOwnerID, encodedImage);
            orgService.createOrg(orgName, orgDescription, orgOwnerID, encodedImage);
            logger.info("Org created successfully with name: {}", orgName);
            return ResponseEntity.ok("Org created successfully");
        } catch (RuntimeException e) {
            logger.error("Error creating org: " + e.getMessage());
            return ResponseEntity.internalServerError().body("There was an error creating the org");
        }
    }

    @GetMapping("/{orgID}/getOrg")
    public ResponseEntity<OrgDTO> getOrg(@PathVariable int orgID) {
        try {
            OrgModel org = orgService.getOrg(orgID); // Fetch the organization model

            // Create the OrgDTO using the constructor
            OrgDTO orgDTO = new OrgDTO(
                    org.getOrgID(),
                    org.getOrgName(),
                    org.getOrgDescription(),
                    org.getOrgOwnerID(),
                    //org.getEncodedImage()
                    org.getEncodedImage(), // Ensure this is set correctly
                    org.getUsers().stream()
                            .map(user -> new UserDTO(user.getUserID(), user.getUserName(), user.getEmail(), user.getProfilePicture())) // Convert UserModel to UserDTO
                            .collect(Collectors.toSet())
            );

            return ResponseEntity.ok(orgDTO); // Return the DTO
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Org not found with id:")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            } else if (e.getMessage().contains("User not found with email:")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            } else if (e.getMessage().contains("User does not have permission to get org")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        }
    }

    @GetMapping("/{orgID}/getAllProjectsFromOrg")
    public ResponseEntity<Set<ProjectDTO>> getAllProjectsFromOrg(@PathVariable int orgID) {
        try {
            logger.info("Received request to get all projects from org with ID: {}", orgID);
            Set<ProjectDTO> projects = orgService.getAllProjectsFromOrg(orgID);
            logger.info("Returning {} projects from org with ID: {}", projects.size(), orgID);
            return ResponseEntity.ok(projects);
        } catch (RuntimeException e) {
            logger.error("Error getting all projects from org: " + e.getMessage());
            if (e.getMessage().contains("Org not found with ID:")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            } else if (e.getMessage().contains("User not found with email:")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            } else if (e.getMessage().contains("User does not have permission to get projects from org")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptySet());
            }
        }
    }

    @PostMapping("/{orgID}/update")
    public ResponseEntity<String> updateOrg(@PathVariable int orgID, @RequestParam String orgName, @RequestParam String orgDescription, @RequestParam int orgOwnerID) {
        try {
            orgService.updateOrg(orgID, orgName, orgDescription, orgOwnerID);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Org updated successfully");
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Org not found with id:")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else if (e.getMessage().contains("User not found with email:")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else if (e.getMessage().contains("User does not have permission to update org")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }
        }
    }

    @DeleteMapping("/{orgID}/deleteOrg")
    public ResponseEntity<String> deleteOrg(@PathVariable int orgID) {
        try {
            orgService.deleteOrg(orgID);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Org deleted successfully");
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Org not found with id:")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else if (e.getMessage().contains("User not found with email:")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else if (e.getMessage().contains("User does not have permission to delete org")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }
        }
    }

    @GetMapping("/{orgID}/members")
    public ResponseEntity<Set<UserDTO>> getOrgMembers(@PathVariable int orgID) {
        try {
            return ResponseEntity.ok(orgService.getOrgMembers(orgID));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Org not found with id:")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        }
    }

    @PostMapping("/{orgID}/addUser")
    public ResponseEntity<String> addUser(@PathVariable int orgID, @RequestParam String email) {
        try {
            orgService.addUser(orgID, email);
            return ResponseEntity.ok("User added successfully");
        } catch (RuntimeException e) {
            if (e.getMessage().contains("User not found with email: ")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else if (e.getMessage().contains("Org not found with id:")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else if (e.getMessage().contains("User does not have permission to add user to org")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }
        }
    }

    @PostMapping("/{orgID}/removeUser")
    public ResponseEntity<String> removeUser(@PathVariable int orgID, @RequestParam String email) {
        try {
            orgService.removeUser(orgID, email);
            return ResponseEntity.ok("User removed successfully");
        } catch (RuntimeException e) {
            if (e.getMessage().contains("User not found with email: ")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else if (e.getMessage().contains("Org not found with id:")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else if (e.getMessage().contains("User does not have permission to remove user from org")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }
        }
    }

    @GetMapping("/getAllOrgs")
    public ResponseEntity<Set<OrgDTO>> getAllOrgs() {
        try {
            return ResponseEntity.ok(orgService.getAllOrgs());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
