package com.aegis.project.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.aegis.project.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.aegis.project.dto.ProjectDTO;
import com.aegis.project.dto.TaskDTO;
import com.aegis.project.dto.UserDTO;
import com.aegis.project.model.OrgModel;
import com.aegis.project.model.ProjectModel;
import com.aegis.project.model.TaskModel;
import com.aegis.project.model.UserModel;
import com.aegis.project.repository.OrgRepository;
import com.aegis.project.repository.ProjectRepository;
import com.aegis.project.repository.TaskRepository;
import com.aegis.project.repository.UserRepository;
import com.aegis.project.service.ProjectService;
import com.aegis.project.service.UserService;

public class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private OrgRepository orgRepository;
    @Mock
    private UserService userService;
    @Mock
    private SimpMessagingTemplate simpMessageTemplate;
    @Mock
    private Authentication authentication;
    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private ProjectService projectService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    public void testEditProject_Success() {
        int projectID = 1;
        ProjectModel project = new ProjectModel();
        project.setProjectID(projectID);
        project.setProjectOwnerID(1);

        UserModel currentUser = new UserModel();
        currentUser.setUserID(1);
        currentUser.setEmail("currentuser@example.com");

        when(projectRepository.findById(projectID)).thenReturn(Optional.of(project));
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("currentuser@example.com");
        when(userRepository.findByEmail("currentuser@example.com")).thenReturn(Optional.of(currentUser));

        projectService.editProject(projectID);

        verify(projectRepository, times(1)).findById(projectID);
    }

    @Test
    public void testDeleteProject_Success() {
        int projectID = 1;
        ProjectModel project = new ProjectModel();
        project.setProjectID(projectID);
        project.setProjectOwnerID(1);

        UserModel currentUser = new UserModel();
        currentUser.setUserID(1);
        currentUser.setEmail("currentuser@example.com");

        when(projectRepository.findById(projectID)).thenReturn(Optional.of(project));
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("currentuser@example.com");
        when(userRepository.findByEmail("currentuser@example.com")).thenReturn(Optional.of(currentUser));

        projectService.deleteProject(projectID);

        verify(taskRepository, times(1)).deleteByParentProjectID(projectID);
        verify(projectRepository, times(1)).deleteById(projectID);
    }

    @Test
    public void testCreateProject_Success() {
        String projectName = "New Project";
        String projectDescription = "Description";
        int projectOwnerID = 1;
        int parentOrgID = 1;
        String encodedImage = "image";

        ProjectModel project = new ProjectModel();
        project.setProjectID(1);
        project.setProjectName(projectName);
        project.setProjectDescription(projectDescription);
        project.setProjectOwnerID(projectOwnerID);
        project.setParentOrgID(parentOrgID);
        project.setEncodedImage(encodedImage);

        UserModel owner = new UserModel();
        owner.setUserID(projectOwnerID);
        owner.setEmail("owner@example.com");

        OrgModel org = new OrgModel();
        org.setOrgID(parentOrgID);

        when(projectRepository.existsProjectByOrgAndName(parentOrgID, projectName)).thenReturn(false);
        when(userRepository.findById(projectOwnerID)).thenReturn(Optional.of(owner));
        when(orgRepository.findById(parentOrgID)).thenReturn(Optional.of(org));
        when(projectRepository.save(any(ProjectModel.class))).thenReturn(project);

        // Spy on the projectService object
        ProjectService projectServiceSpy = spy(projectService);

        // Mock the addUser method
        doNothing().when(projectServiceSpy).addUser(anyInt(), anyString());

        boolean isCreated = projectServiceSpy.createProject(projectName, projectDescription, projectOwnerID, parentOrgID, encodedImage);

        assertTrue(isCreated);

        ArgumentCaptor<ProjectModel> projectCaptor = ArgumentCaptor.forClass(ProjectModel.class);
        verify(projectRepository, times(1)).save(projectCaptor.capture());
        assertEquals("New Project", projectCaptor.getValue().getProjectName());
        assertEquals("Description", projectCaptor.getValue().getProjectDescription());
    }

    @Test
    public void testGetProject_Success() {
        int projectID = 1;
        ProjectModel project = new ProjectModel();
        project.setProjectID(projectID);
        project.setProjectOwnerID(1);

        UserModel currentUser = new UserModel();
        currentUser.setUserID(1);
        currentUser.setEmail("currentuser@example.com");

        when(projectRepository.findById(projectID)).thenReturn(Optional.of(project));
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("currentuser@example.com");
        when(userRepository.findByEmail("currentuser@example.com")).thenReturn(Optional.of(currentUser));

        ProjectDTO projectDTO = projectService.getProject(projectID);

        assertNotNull(projectDTO);
        assertEquals(projectID, projectDTO.getProjectID());
    }

    @Test
    public void testUpdateProject_Success() {
        int projectID = 1;
        String projectName = "Updated Project";
        String projectDescription = "Updated Description";
        int projectOwnerID = 1;
        String encodedImage = "updatedImage";

        ProjectModel project = new ProjectModel();
        project.setProjectID(projectID);
        project.setProjectOwnerID(projectOwnerID);

        UserModel currentUser = new UserModel();
        currentUser.setUserID(projectOwnerID);
        currentUser.setEmail("currentuser@example.com");

        when(projectRepository.findById(projectID)).thenReturn(Optional.of(project));
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("currentuser@example.com");
        when(userRepository.findByEmail("currentuser@example.com")).thenReturn(Optional.of(currentUser));

        projectService.updateProject(projectID, projectName, projectDescription, projectOwnerID, encodedImage);

        verify(projectRepository, times(1)).save(project);
        assertEquals(projectName, project.getProjectName());
        assertEquals(projectDescription, project.getProjectDescription());
        assertEquals(encodedImage, project.getEncodedImage());
    }

    @Test
    public void testGetProjectTasks_Success() {
        int projectID = 1;
        ProjectModel project = new ProjectModel();
        project.setProjectID(projectID);

        TaskModel task1 = new TaskModel();
        task1.setTaskID(1);
        task1.setAssignerID(1); // Ensure assignerID is set
        TaskModel task2 = new TaskModel();
        task2.setTaskID(2);
        task2.setAssignerID(1); // Ensure assignerID is set

        project.setProjectTasks(Set.of(task1, task2));

        when(projectRepository.findById(projectID)).thenReturn(Optional.of(project));

        Set<TaskDTO> tasks = projectService.getProjectTasks(projectID);

        assertEquals(2, tasks.size());
    }

    @Test
    public void testGetAssignedUsers_Success() {
        int projectID = 1;
        ProjectModel project = new ProjectModel();
        project.setProjectID(projectID);

        UserModel user1 = new UserModel();
        user1.setUserID(1);
        UserModel user2 = new UserModel();
        user2.setUserID(2);

        project.setAssignedUsers(Set.of(user1, user2));

        UserModel currentUser = new UserModel();
        currentUser.setUserID(1);
        currentUser.setEmail("currentuser@example.com");

        when(projectRepository.findById(projectID)).thenReturn(Optional.of(project));
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("currentuser@example.com");
        when(userRepository.findByEmail("currentuser@example.com")).thenReturn(Optional.of(currentUser));

        Set<UserDTO> users = projectService.getAssignedUsers(projectID);

        assertEquals(2, users.size());
    }

    @Test
    public void testCreateProjectJson_Success() {
        ProjectModel project = new ProjectModel();
        project.setProjectID(1);
        project.setProjectName("Project");
        project.setProjectDescription("Description");
        project.setProjectOwnerID(1);
        project.setEncodedImage("image");

        UserModel user1 = new UserModel();
        user1.setUserID(1);
        user1.setUserName("User1");
        user1.setEmail("user1@example.com");

        project.setAssignedUsers(Set.of(user1));

        when(userService.createUserJson(user1)).thenReturn("{\"userID\": 1,\"userName\": \"User1\",\"email\": \"user1@example.com\"}");

        String projectJson = projectService.createProjectJson(project);

        assertTrue(projectJson.contains("\"projectID\": 1"));
        assertTrue(projectJson.contains("\"projectName\": \"Project\""));
        assertTrue(projectJson.contains("\"assignedUsers\": [{\"userID\": 1,\"userName\": \"User1\",\"email\": \"user1@example.com\"}]"));
    }

    @Test
    public void testAddUsers_Success() {
        int projectID = 1;
        List<Integer> userIDs = List.of(1, 2);

        ProjectModel project = new ProjectModel();
        project.setProjectID(projectID);
        project.setProjectOwnerID(1);

        UserModel currentUser = new UserModel();
        currentUser.setUserID(1);
        currentUser.setEmail("currentuser@example.com");

        UserModel user1 = new UserModel();
        user1.setUserID(1);
        user1.setEmail("user1@example.com");

        UserModel user2 = new UserModel();
        user2.setUserID(2);
        user2.setEmail("user2@example.com");

        when(projectRepository.findById(projectID)).thenReturn(Optional.of(project));
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("currentuser@example.com");
        when(userRepository.findByEmail("currentuser@example.com")).thenReturn(Optional.of(currentUser));
        when(userRepository.findById(1)).thenReturn(Optional.of(user1));
        when(userRepository.findById(2)).thenReturn(Optional.of(user2));

        projectService.addUsers(projectID, userIDs);

        verify(projectRepository, times(1)).save(project);
        assertTrue(project.getAssignedUsers().contains(user1));
        assertTrue(project.getAssignedUsers().contains(user2));
    }

    @Test
    public void testAddUser_Success() {
        int projectID = 1;
        String email = "user@example.com";

        ProjectModel project = new ProjectModel();
        project.setProjectID(projectID);
        project.setProjectOwnerID(1);

        UserModel currentUser = new UserModel();
        currentUser.setUserID(1);
        currentUser.setEmail("currentuser@example.com");

        UserModel userToAdd = new UserModel();
        userToAdd.setUserID(2);
        userToAdd.setEmail(email);

        when(projectRepository.findById(projectID)).thenReturn(Optional.of(project));
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("currentuser@example.com");
        when(userRepository.findByEmail("currentuser@example.com")).thenReturn(Optional.of(currentUser));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userToAdd));

        projectService.addUser(projectID, email);

        verify(projectRepository, times(1)).save(project);
        assertTrue(project.getAssignedUsers().contains(userToAdd));
    }

    @Test
    public void testRemoveUser_Success() {
        int projectID = 1;
        String email = "user@example.com";

        ProjectModel project = new ProjectModel();
        project.setProjectID(projectID);
        project.setProjectOwnerID(1);

        UserModel currentUser = new UserModel();
        currentUser.setUserID(1);
        currentUser.setEmail("currentuser@example.com");

        UserModel userToRemove = new UserModel();
        userToRemove.setUserID(2);
        userToRemove.setEmail(email);

        Set<UserModel> assignedUsers = new HashSet<>();
        assignedUsers.add(userToRemove);
        project.setAssignedUsers(assignedUsers);

        when(projectRepository.findById(projectID)).thenReturn(Optional.of(project));
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("currentuser@example.com");
        when(userRepository.findByEmail("currentuser@example.com")).thenReturn(Optional.of(currentUser));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userToRemove));

        projectService.removeUser(projectID, email);

        verify(projectRepository, times(1)).save(project);
        assertFalse(project.getAssignedUsers().contains(userToRemove));
    }

    @Test
    public void testGetAllTasksFromProject_Success() {
        int projectID = 1;
        ProjectModel project = new ProjectModel();
        project.setProjectID(projectID);

        TaskModel task1 = new TaskModel();
        task1.setTaskID(1);
        task1.setAssignerID(1); // Ensure assignerID is set
        TaskModel task2 = new TaskModel();
        task2.setTaskID(2);
        task2.setAssignerID(1); // Ensure assignerID is set

        project.setProjectTasks(Set.of(task1, task2));

        UserModel currentUser = new UserModel();
        currentUser.setUserID(1);
        currentUser.setEmail("currentuser@example.com");

        project.setAssignedUsers(Set.of(currentUser)); // Ensure the current user is assigned to the project

        when(projectRepository.findById(projectID)).thenReturn(Optional.of(project));
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("currentuser@example.com");
        when(userRepository.findByEmail("currentuser@example.com")).thenReturn(Optional.of(currentUser));
        when(taskRepository.findByParentProjectID(projectID)).thenReturn(List.of(task1, task2));

        // Mock the save method to simulate saving tasks
        when(taskRepository.save(any(TaskModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Set<TaskDTO> tasks = projectService.getAllTasksFromProject(projectID);

        assertEquals(2, tasks.size());
    }

    @Test
    public void testGetAllTasksForUser_Success() {
        int projectID = 1;
        String email = "user@example.com";

        ProjectModel project = new ProjectModel();
        project.setProjectID(projectID);

        TaskModel task1 = new TaskModel();
        task1.setTaskID(1);
        task1.setAssignerID(1); // Ensure assignerID is set
        TaskModel task2 = new TaskModel();
        task2.setTaskID(2);
        task2.setAssignerID(1); // Ensure assignerID is set

        UserModel currentUser = new UserModel();
        currentUser.setUserID(1);
        currentUser.setEmail("currentuser@example.com");

        UserModel user = new UserModel();
        user.setUserID(1);
        user.setEmail(email);

        project.setAssignedUsers(Set.of(user));

        when(projectRepository.findById(projectID)).thenReturn(Optional.of(project));
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("currentuser@example.com");
        when(userRepository.findByEmail("currentuser@example.com")).thenReturn(Optional.of(currentUser));
        when(taskRepository.findByParentProjectIDAndUserID(projectID, currentUser.getUserID())).thenReturn(List.of(task1, task2));

        Set<TaskDTO> tasks = projectService.getAllTasksForUser(projectID, email);

        assertEquals(2, tasks.size());
    }

    @Test
    public void testChangeArchivedStatus_Success() {
        int projectID = 1;
        boolean isArchived = true;

        ProjectModel project = new ProjectModel();
        project.setProjectID(projectID);
        project.setProjectOwnerID(1);

        UserModel currentUser = new UserModel();
        currentUser.setUserID(1);
        currentUser.setEmail("currentuser@example.com");

        when(projectRepository.findById(projectID)).thenReturn(Optional.of(project));
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("currentuser@example.com");
        when(userRepository.findByEmail("currentuser@example.com")).thenReturn(Optional.of(currentUser));

        projectService.changeArchivedStatus(projectID, isArchived);

        verify(projectRepository, times(1)).save(project);
        assertTrue(project.isArchived());
    }

    @Test
    public void testGetAllUserProjects_Success() {
        String email = "user@example.com";

        UserModel user = new UserModel();
        user.setUserID(1);
        user.setEmail(email);

        ProjectModel project1 = new ProjectModel();
        project1.setProjectID(1);
        ProjectModel project2 = new ProjectModel();
        project2.setProjectID(2);

        when(userService.findUserByEmail(email)).thenReturn(Optional.of(user));
        when(projectRepository.findAllUserProjects(user.getUserID())).thenReturn(List.of(project1, project2));

        Set<ProjectDTO> projects = projectService.getAllUserProjects(email);

        assertEquals(2, projects.size());
    }

    @Test
    public void testEditProject_ProjectNotFound() {
        int projectID = 1;

        when(projectRepository.findById(projectID)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            projectService.editProject(projectID);
        });

        assertEquals("Project not found with id: " + projectID, exception.getMessage());
    }

    @Test
    public void testDeleteProject_ProjectNotFound() {
        int projectID = 1;

        when(projectRepository.findById(projectID)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            projectService.deleteProject(projectID);
        });

        assertEquals("Project not found with id: " + projectID, exception.getMessage());
    }

    @Test
    public void testGetProject_ProjectNotFound() {
        int projectID = 1;

        when(projectRepository.findById(projectID)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            projectService.getProject(projectID);
        });

        assertEquals("Project not found with id: " + projectID, exception.getMessage());
    }

    @Test
    public void testUpdateProject_ProjectNotFound() {
        int projectID = 1;

        when(projectRepository.findById(projectID)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            projectService.updateProject(projectID, "Updated Project", "Updated Description", 1, "updatedImage");
        });

        assertEquals("Project not found with id: " + projectID, exception.getMessage());
    }

    @Test
    public void testAddUser_ProjectNotFound() {
        int projectID = 1;
        String email = "user@example.com";

        when(projectRepository.findById(projectID)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            projectService.addUser(projectID, email);
        });

        assertEquals("Project not found with id: " + projectID, exception.getMessage());
    }

    @Test
    public void testRemoveUser_ProjectNotFound() {
        int projectID = 1;
        String email = "user@example.com";

        when(projectRepository.findById(projectID)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            projectService.removeUser(projectID, email);
        });

        assertEquals("Project not found with id: " + projectID, exception.getMessage());
    }

    @Test
    public void testGetAllTasksFromProject_ProjectNotFound() {
        int projectID = 1;

        when(projectRepository.findById(projectID)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            projectService.getAllTasksFromProject(projectID);
        });

        assertEquals("Project not found with id: " + projectID, exception.getMessage());
    }

    @Test
    public void testGetAllTasksForUser_ProjectNotFound() {
        int projectID = 1;
        String email = "user@example.com";

        when(projectRepository.findById(projectID)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            projectService.getAllTasksForUser(projectID, email);
        });

        assertEquals("Project not found with id: " + projectID, exception.getMessage());
    }

    @Test
    public void testChangeArchivedStatus_ProjectNotFound() {
        int projectID = 1;
        boolean isArchived = true;

        when(projectRepository.findById(projectID)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            projectService.changeArchivedStatus(projectID, isArchived);
        });

        assertEquals("Project not found with id: " + projectID, exception.getMessage());
    }

    @Test
    public void testGetAllUserProjects_UserNotFound() {
        String email = "user@example.com";

        when(userService.findUserByEmail(email)).thenReturn(Optional.empty());

        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            projectService.getAllUserProjects(email);
        });

        assertEquals("User not found with email: " + email, exception.getMessage());
    }
}