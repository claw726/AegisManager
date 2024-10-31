package com.aegis.project.controller;

import com.aegis.project.dto.ProjectDTO;
import com.aegis.project.dto.TaskDTO;
import com.aegis.project.dto.UserDTO;
import com.aegis.project.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ProjectControllerTest {

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private ProjectController projectController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateProject_Success() {
        String projectName = "Test Project";
        String projectDescription = "Test Description";
        int projectOwnerID = 1;
        int parentOrgID = 1;
        String encodedImage = "encodedImage";

        when(projectService.createProject(projectName, projectDescription, projectOwnerID, parentOrgID, encodedImage)).thenReturn(true);

        ResponseEntity<String> response = projectController.createProject(projectName, projectDescription, projectOwnerID, parentOrgID, encodedImage);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Project created successfully", response.getBody());
    }

    @Test
    public void testCreateProject_Error() {
        String projectName = "Test Project";
        String projectDescription = "Test Description";
        int projectOwnerID = 1;
        int parentOrgID = 1;
        String encodedImage = "encodedImage";

        when(projectService.createProject(projectName, projectDescription, projectOwnerID, parentOrgID, encodedImage)).thenThrow(new RuntimeException("Error creating project"));

        ResponseEntity<String> response = projectController.createProject(projectName, projectDescription, projectOwnerID, parentOrgID, encodedImage);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("There was an error creating the project", response.getBody());
    }

    @Test
    public void testGetProject_Success() {
        int projectID = 1;
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setProjectID(projectID);

        when(projectService.getProject(projectID)).thenReturn(projectDTO);

        ResponseEntity<ProjectDTO> response = projectController.getProject(projectID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(projectID, response.getBody().getProjectID());
    }

    @Test
    public void testGetProject_NotFound() {
        int projectID = 1;

        when(projectService.getProject(projectID)).thenThrow(new RuntimeException("Project not found with id: " + projectID));

        ResponseEntity<ProjectDTO> response = projectController.getProject(projectID);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testUpdateProject_Success() {
        int projectID = 1;
        String projectName = "Updated Project";
        String projectDescription = "Updated Description";
        int projectOwnerID = 1;
        String encodedImage = "encodedImage";

        doNothing().when(projectService).updateProject(projectID, projectName, projectDescription, projectOwnerID, encodedImage);

        ResponseEntity<String> response = projectController.updateProject(projectID, projectName, projectDescription, projectOwnerID, encodedImage);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertEquals("Project updated successfully", response.getBody());
    }

    @Test
    public void testUpdateProject_NotFound() {
        int projectID = 1;
        String projectName = "Updated Project";
        String projectDescription = "Updated Description";
        int projectOwnerID = 1;
        String encodedImage = "encodedImage";

        doThrow(new RuntimeException("Project not found with id: " + projectID)).when(projectService).updateProject(projectID, projectName, projectDescription, projectOwnerID, encodedImage);

        ResponseEntity<String> response = projectController.updateProject(projectID, projectName, projectDescription, projectOwnerID, encodedImage);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testDeleteProject_Success() {
        int projectID = 1;

        doNothing().when(projectService).deleteProject(projectID);

        ResponseEntity<String> response = projectController.deleteProject(projectID);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertEquals("Project deleted successfully", response.getBody());
    }

    @Test
    public void testDeleteProject_NotFound() {
        int projectID = 1;

        doThrow(new RuntimeException("Project not found with id: " + projectID)).when(projectService).deleteProject(projectID);

        ResponseEntity<String> response = projectController.deleteProject(projectID);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testAddUsers_Success() {
        int projectID = 1;
        List<Integer> userIDs = List.of(1, 2, 3);

        doNothing().when(projectService).addUsers(projectID, userIDs);

        ResponseEntity<String> response = projectController.addUsers(projectID, userIDs);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Users added successfully", response.getBody());
    }

    @Test
    public void testAddUsers_NotFound() {
        int projectID = 1;
        List<Integer> userIDs = List.of(1, 2, 3);

        doThrow(new RuntimeException("Project not found with id: " + projectID)).when(projectService).addUsers(projectID, userIDs);

        ResponseEntity<String> response = projectController.addUsers(projectID, userIDs);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testAddUser_Success() {
        int projectID = 1;
        String email = "test@example.com";

        doNothing().when(projectService).addUser(projectID, email);

        ResponseEntity<String> response = projectController.addUser(projectID, email);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User added successfully", response.getBody());
    }

    @Test
    public void testAddUser_NotFound() {
        int projectID = 1;
        String email = "test@example.com";

        doThrow(new RuntimeException("User not found with email: " + email)).when(projectService).addUser(projectID, email);

        ResponseEntity<String> response = projectController.addUser(projectID, email);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testRemoveUser_Success() {
        int projectID = 1;
        String email = "test@example.com";

        doNothing().when(projectService).removeUser(projectID, email);

        ResponseEntity<String> response = projectController.removeUser(projectID, email);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User removed successfully", response.getBody());
    }

    @Test
    public void testRemoveUser_NotFound() {
        int projectID = 1;
        String email = "test@example.com";

        doThrow(new RuntimeException("User not found with email: " + email)).when(projectService).removeUser(projectID, email);

        ResponseEntity<String> response = projectController.removeUser(projectID, email);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testGetAllTasksFromProject_Success() {
        int projectID = 1;
        Set<TaskDTO> tasks = new HashSet<>();
        tasks.add(new TaskDTO());

        when(projectService.getAllTasksFromProject(projectID)).thenReturn(tasks);

        ResponseEntity<Set<TaskDTO>> response = projectController.getAllTasksFromProject(projectID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    public void testGetAllTasksFromProject_NotFound() {
        int projectID = 1;

        doThrow(new RuntimeException("Project not found with id: " + projectID)).when(projectService).getAllTasksFromProject(projectID);

        ResponseEntity<Set<TaskDTO>> response = projectController.getAllTasksFromProject(projectID);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testGetUsers_Success() {
        int projectID = 1;
        Set<UserDTO> users = new HashSet<>();
        users.add(new UserDTO());

        when(projectService.getAssignedUsers(projectID)).thenReturn(users);

        ResponseEntity<Set<UserDTO>> response = projectController.getUsers(projectID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    public void testGetUsers_NotFound() {
        int projectID = 1;

        doThrow(new RuntimeException("Project not found with ID: " + projectID)).when(projectService).getAssignedUsers(projectID);

        ResponseEntity<Set<UserDTO>> response = projectController.getUsers(projectID);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testGetAllTasksForUser_Success() {
        int projectID = 1;
        String email = "test@example.com";
        Set<TaskDTO> tasks = new HashSet<>();
        tasks.add(new TaskDTO());

        when(projectService.getAllTasksForUser(projectID, email)).thenReturn(tasks);

        ResponseEntity<Set<TaskDTO>> response = projectController.getAllTasksForUser(projectID, email);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    public void testGetAllTasksForUser_NotFound() {
        int projectID = 1;
        String email = "test@example.com";

        doThrow(new RuntimeException("Project not found with ID: " + projectID)).when(projectService).getAllTasksForUser(projectID, email);

        ResponseEntity<Set<TaskDTO>> response = projectController.getAllTasksForUser(projectID, email);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testChangeArchivedStatus_Success() {
        int projectID = 1;
        boolean isArchived = true;

        doNothing().when(projectService).changeArchivedStatus(projectID, isArchived);

        ResponseEntity<String> response = projectController.changeArchivedStatus(projectID, isArchived);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Project archived status changed to " + isArchived + " successfully", response.getBody());
    }

    @Test
    public void testChangeArchivedStatus_NotFound() {
        int projectID = 1;
        boolean isArchived = true;

        doThrow(new RuntimeException("Project not found with id: " + projectID)).when(projectService).changeArchivedStatus(projectID, isArchived);

        ResponseEntity<String> response = projectController.changeArchivedStatus(projectID, isArchived);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}