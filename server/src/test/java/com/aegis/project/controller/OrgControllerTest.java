package com.aegis.project.controller;

import com.aegis.project.AegisApplication;
import com.aegis.project.dto.OrgDTO;
import com.aegis.project.dto.ProjectDTO;
import com.aegis.project.dto.UserDTO;
import com.aegis.project.model.OrgModel;
import com.aegis.project.service.OrgService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class OrgControllerTest {

    @Mock
    private OrgService orgService;

    @InjectMocks
    private OrgController orgController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateOrg_Success() {
        String orgName = "Test Org";
        String orgDescription = "Test Description";
        int orgOwnerID = 1;
        String encodedImage = "encodedImage";

        when(orgService.createOrg(orgName, orgDescription, orgOwnerID, encodedImage)).thenReturn(true);
        ResponseEntity<String> response = orgController.createOrg(orgName, orgDescription, orgOwnerID, encodedImage);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Org created successfully", response.getBody());
    }

    @Test
    public void testCreateOrg_Error() {
        String orgName = "Test Org";
        String orgDescription = "Test Description";
        int orgOwnerID = 1;
        String encodedImage = "encodedImage";

        doThrow(new RuntimeException("Error creating org")).when(orgService).createOrg(orgName, orgDescription, orgOwnerID, encodedImage);

        ResponseEntity<String> response = orgController.createOrg(orgName, orgDescription, orgOwnerID, encodedImage);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("There was an error creating the org", response.getBody());
    }

    @Test
    public void testGetOrg_Success() {
        int orgID = 1;
        OrgModel orgModel = new OrgModel();
        orgModel.setOrgID(orgID);
        orgModel.setOrgName("Test Org");
        orgModel.setOrgDescription("Test Description");
        orgModel.setOrgOwnerID(1);
        orgModel.setEncodedImage("encodedImage");
        orgModel.setUsers(new HashSet<>());

        when(orgService.getOrg(orgID)).thenReturn(orgModel);

        ResponseEntity<OrgDTO> response = orgController.getOrg(orgID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(orgID, response.getBody().getOrgID());
    }

    @Test
    public void testGetOrg_NotFound() {
        int orgID = 1;

        doThrow(new RuntimeException("Org not found with id: " + orgID)).when(orgService).getOrg(orgID);

        ResponseEntity<OrgDTO> response = orgController.getOrg(orgID);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testGetAllProjectsFromOrg_Success() {
        int orgID = 1;
        Set<ProjectDTO> projects = new HashSet<>();
        projects.add(new ProjectDTO());

        when(orgService.getAllProjectsFromOrg(orgID)).thenReturn(projects);

        ResponseEntity<Set<ProjectDTO>> response = orgController.getAllProjectsFromOrg(orgID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    public void testGetAllProjectsFromOrg_NotFound() {
        int orgID = 1;

        doThrow(new RuntimeException("Org not found with ID: " + orgID)).when(orgService).getAllProjectsFromOrg(orgID);

        ResponseEntity<Set<ProjectDTO>> response = orgController.getAllProjectsFromOrg(orgID);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testUpdateOrg_Success() {
        int orgID = 1;
        String orgName = "Updated Org";
        String orgDescription = "Updated Description";
        int orgOwnerID = 1;
        String encodedImage = "encodedImage";

        doNothing().when(orgService).updateOrg(orgID, orgName, orgDescription, orgOwnerID, encodedImage);

        ResponseEntity<String> response = orgController.updateOrg(orgID, orgName, orgDescription, orgOwnerID, encodedImage);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertEquals("Org updated successfully", response.getBody());
    }

    @Test
    public void testUpdateOrg_NotFound() {
        int orgID = 1;
        String orgName = "Updated Org";
        String orgDescription = "Updated Description";
        int orgOwnerID = 1;
        String encodedImage = "encodedImage";

        doThrow(new RuntimeException("Org not found with id: " + orgID)).when(orgService).updateOrg(orgID, orgName, orgDescription, orgOwnerID, encodedImage);

        ResponseEntity<String> response = orgController.updateOrg(orgID, orgName, orgDescription, orgOwnerID, encodedImage);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testDeleteOrg_Success() {
        int orgID = 1;

        doNothing().when(orgService).deleteOrg(orgID);

        ResponseEntity<String> response = orgController.deleteOrg(orgID);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertEquals("Org deleted successfully", response.getBody());
    }

    @Test
    public void testDeleteOrg_NotFound() {
        int orgID = 1;

        doThrow(new RuntimeException("Org not found with id: " + orgID)).when(orgService).deleteOrg(orgID);

        ResponseEntity<String> response = orgController.deleteOrg(orgID);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testGetOrgMembers_Success() {
        int orgID = 1;
        Set<UserDTO> users = new HashSet<>();
        users.add(new UserDTO());

        when(orgService.getOrgMembers(orgID)).thenReturn(users);

        ResponseEntity<Set<UserDTO>> response = orgController.getOrgMembers(orgID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    public void testGetOrgMembers_NotFound() {
        int orgID = 1;

        doThrow(new RuntimeException("Org not found with id: " + orgID)).when(orgService).getOrgMembers(orgID);

        ResponseEntity<Set<UserDTO>> response = orgController.getOrgMembers(orgID);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testAddUser_Success() {
        int orgID = 1;
        String email = "test@example.com";

        doNothing().when(orgService).addUser(orgID, email);

        ResponseEntity<String> response = orgController.addUser(orgID, email);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User added successfully", response.getBody());
    }

    @Test
    public void testAddUser_NotFound() {
        int orgID = 1;
        String email = "test@example.com";

        doThrow(new RuntimeException("User not found with email: " + email)).when(orgService).addUser(orgID, email);

        ResponseEntity<String> response = orgController.addUser(orgID, email);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testRemoveUser_Success() {
        int orgID = 1;
        String email = "test@example.com";

        doNothing().when(orgService).removeUser(orgID, email);

        ResponseEntity<String> response = orgController.removeUser(orgID, email);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User removed successfully", response.getBody());
    }

    @Test
    public void testRemoveUser_NotFound() {
        int orgID = 1;
        String email = "test@example.com";

        doThrow(new RuntimeException("User not found with email: " + email)).when(orgService).removeUser(orgID, email);

        ResponseEntity<String> response = orgController.removeUser(orgID, email);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testGetAllOrgs_Success() {
        Set<OrgDTO> orgs = new HashSet<>();
        orgs.add(new OrgDTO());

        when(orgService.getAllOrgs()).thenReturn(orgs);

        ResponseEntity<Set<OrgDTO>> response = orgController.getAllOrgs();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    public void testGetAllOrgs_Error() {
        when(orgService.getAllOrgs()).thenThrow(new RuntimeException("Error fetching orgs"));

        ResponseEntity<Set<OrgDTO>> response = orgController.getAllOrgs();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void testGetArchivedProjects_Success() {
        int orgID = 1;
        Set<ProjectDTO> projects = new HashSet<>();
        projects.add(new ProjectDTO());

        when(orgService.getArchivedProjects(orgID)).thenReturn(projects);

        ResponseEntity<Set<ProjectDTO>> response = orgController.getArchivedProjects(orgID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    public void testGetArchivedProjects_NotFound() {
        int orgID = 1;

        doThrow(new RuntimeException("Org not found with id: " + orgID)).when(orgService).getArchivedProjects(orgID);

        ResponseEntity<Set<ProjectDTO>> response = orgController.getArchivedProjects(orgID);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}