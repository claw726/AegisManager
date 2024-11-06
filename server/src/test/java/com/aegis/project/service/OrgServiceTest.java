package com.aegis.project.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.aegis.project.dto.OrgDTO;
import com.aegis.project.dto.ProjectDTO;
import com.aegis.project.dto.UserDTO;
import com.aegis.project.model.OrgModel;
import com.aegis.project.model.ProjectModel;
import com.aegis.project.model.UserModel;
import com.aegis.project.repository.OrgRepository;
import com.aegis.project.repository.ProjectRepository;
import com.aegis.project.repository.TaskRepository;
import com.aegis.project.repository.UserRepository;
import java.util.*;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class OrgServiceTest {

  @Mock
  private OrgRepository orgRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private ProjectRepository projectRepository;

  @Mock
  private TaskRepository taskRepository;

  @Mock
  private Authentication authentication;

  @Mock
  private UserDetails userDetails;

  @Mock
  private UserService userService;

  @InjectMocks
  private OrgService orgService;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }

  @Test
  public void testCreateOrg_Success() {
    OrgModel org = new OrgModel();
    org.setOrgName("New Org");
    org.setOrgDescription("Description");
    org.setOrgOwnerID(1);

    UserModel currentUser = new UserModel();
    currentUser.setUserID(1);
    currentUser.setEmail("currentuser@example.com");

    when(authentication.getPrincipal()).thenReturn(userDetails);
    when(userDetails.getUsername()).thenReturn("currentuser@example.com");
    when(userRepository.findByEmail("currentuser@example.com")).thenReturn(
      Optional.of(currentUser)
    );
    when(userRepository.findById(1)).thenReturn(Optional.of(currentUser));
    when(orgRepository.save(any(OrgModel.class))).thenReturn(org);

    // Spy on the orgService object
    OrgService orgServiceSpy = spy(orgService);

    // Mock the addUser method
    doNothing().when(orgServiceSpy).addUser(anyInt(), anyString());

    boolean isCreated = orgServiceSpy.createOrg(
      "New Org",
      "Description",
      1,
      "image"
    );

    assertTrue(isCreated);

    ArgumentCaptor<OrgModel> orgCaptor = ArgumentCaptor.forClass(
      OrgModel.class
    );
    verify(orgRepository, times(1)).save(orgCaptor.capture());
    assertEquals("New Org", orgCaptor.getValue().getOrgName());
    assertEquals("Description", orgCaptor.getValue().getOrgDescription());
  }

  @Test
  public void testGetAllOrgs_Success() {
    OrgModel org1 = new OrgModel();
    org1.setOrgID(1);
    org1.setOrgName("Org 1");
    org1.setOrgDescription("Description 1");
    org1.setOrgOwnerID(1);
    org1.setEncodedImage("image1");

    OrgModel org2 = new OrgModel();
    org2.setOrgID(2);
    org2.setOrgName("Org 2");
    org2.setOrgDescription("Description 2");
    org2.setOrgOwnerID(2);
    org2.setEncodedImage("image2");

    when(orgRepository.findAll()).thenReturn(List.of(org1, org2));
    when(orgRepository.findById(1)).thenReturn(Optional.of(org1));
    when(orgRepository.findById(2)).thenReturn(Optional.of(org2));

    List<OrgDTO> orgs = orgService
      .getAllOrgs()
      .stream()
      .sorted(Comparator.comparingInt(OrgDTO::getOrgID))
      .collect(Collectors.toList());

    assertEquals(2, orgs.size());
    assertEquals(1, orgs.get(0).getOrgID());
    assertEquals(2, orgs.get(1).getOrgID());
  }

  @Test
  public void testGetOrg_Success() {
    int orgID = 1;
    OrgModel org = new OrgModel();
    org.setOrgID(orgID);

    UserModel currentUser = new UserModel();
    currentUser.setUserID(1);
    currentUser.setEmail("currentuser@example.com");

    org.setUsers(Set.of(currentUser));

    when(orgRepository.findById(orgID)).thenReturn(Optional.of(org));
    when(authentication.getPrincipal()).thenReturn(userDetails);
    when(userDetails.getUsername()).thenReturn("currentuser@example.com");
    when(userRepository.findByEmail("currentuser@example.com")).thenReturn(
      Optional.of(currentUser)
    );

    OrgModel orgModel = orgService.getOrg(orgID);

    assertNotNull(orgModel);
    assertEquals(orgID, orgModel.getOrgID());
  }

  @Test
  public void testGetOrg_NotFound() {
    int orgID = 1;

    when(orgRepository.findById(orgID)).thenReturn(Optional.empty());

    Exception exception = assertThrows(RuntimeException.class, () -> {
      orgService.getOrg(orgID);
    });

    assertEquals("Org not found with id: " + orgID, exception.getMessage());
  }

  @Test
  public void testGetAllProjectsFromOrg_Success() {
    int orgID = 1;
    OrgModel org = new OrgModel();
    org.setOrgID(orgID);

    ProjectModel project1 = new ProjectModel();
    project1.setProjectID(1);
    ProjectModel project2 = new ProjectModel();
    project2.setProjectID(2);

    UserModel currentUser = new UserModel();
    currentUser.setUserID(1);
    currentUser.setEmail("currentuser@example.com");

    org.setUsers(Set.of(currentUser));

    when(orgRepository.findById(orgID)).thenReturn(Optional.of(org));
    when(projectRepository.findByParentOrgID(orgID)).thenReturn(
      List.of(project1, project2)
    );
    when(authentication.getPrincipal()).thenReturn(userDetails);
    when(userDetails.getUsername()).thenReturn("currentuser@example.com");
    when(userRepository.findByEmail("currentuser@example.com")).thenReturn(
      Optional.of(currentUser)
    );

    Set<ProjectDTO> projects = orgService.getAllProjectsFromOrg(orgID);

    assertEquals(2, projects.size());
    assertTrue(projects.stream().anyMatch(p -> p.getProjectID() == 1));
    assertTrue(projects.stream().anyMatch(p -> p.getProjectID() == 2));
  }

  @Test
  public void testUpdateOrg_Success() {
    int orgID = 1;
    OrgModel org = new OrgModel();
    org.setOrgID(orgID);
    org.setOrgName("Updated Org");
    org.setOrgDescription("Updated Description");
    org.setOrgOwnerID(1);

    UserModel currentUser = new UserModel();
    currentUser.setUserID(1);
    currentUser.setEmail("currentuser@example.com");

    when(orgRepository.findById(orgID)).thenReturn(Optional.of(org));
    when(orgRepository.save(any(OrgModel.class))).thenReturn(org);
    when(authentication.getPrincipal()).thenReturn(userDetails);
    when(userDetails.getUsername()).thenReturn("currentuser@example.com");
    when(userRepository.findByEmail("currentuser@example.com")).thenReturn(
      Optional.of(currentUser)
    );

    orgService.updateOrg(
      orgID,
      "Updated Org",
      "Updated Description",
      1,
      "image"
    );

    OrgModel updatedOrg = orgService.getOrg(orgID);

    assertNotNull(updatedOrg);
    assertEquals("Updated Org", updatedOrg.getOrgName());
    assertEquals("Updated Description", updatedOrg.getOrgDescription());
    verify(orgRepository, times(1)).save(org);
  }

  @Test
  public void testUpdateOrg_NotFound() {
    int orgID = 1;

    when(orgRepository.findById(orgID)).thenReturn(Optional.empty());

    Exception exception = assertThrows(RuntimeException.class, () -> {
      orgService.updateOrg(
        orgID,
        "Updated Org",
        "Updated Description",
        1,
        "image"
      );
    });

    assertEquals("Org not found with id: " + orgID, exception.getMessage());
  }

  @Test
  public void testDeleteOrg_Success() {
    int orgID = 1;
    OrgModel org = new OrgModel();
    org.setOrgID(orgID);
    org.setOrgOwnerID(1);
    UserModel user = new UserModel();
    user.setUserID(1);
    org.setUsers(Set.of(user));

    when(orgRepository.findById(orgID)).thenReturn(Optional.of(org));
    when(authentication.getPrincipal()).thenReturn(userDetails);
    when(userDetails.getUsername()).thenReturn("currentuser@example.com");
    when(userRepository.findByEmail("currentuser@example.com")).thenReturn(
      Optional.of(user)
    );

    orgService.deleteOrg(orgID);

    verify(orgRepository, times(1)).deleteById(orgID);
    verify(projectRepository, times(1)).deleteByParentOrgID(orgID);
    verify(taskRepository, times(1)).deleteByParentOrgID(orgID);
  }

  @Test
  public void testDeleteOrg_NotFound() {
    int orgID = 1;

    when(orgRepository.findById(orgID)).thenReturn(Optional.empty());

    Exception exception = assertThrows(RuntimeException.class, () -> {
      orgService.deleteOrg(orgID);
    });

    assertEquals("Org not found with id: " + orgID, exception.getMessage());
  }

  @Test
  public void testDeleteOrg_NoPermission() {
    int orgID = 1;
    OrgModel org = new OrgModel();
    org.setOrgID(orgID);
    org.setOrgOwnerID(2);
    UserModel user = new UserModel();
    user.setUserID(1);

    when(orgRepository.findById(orgID)).thenReturn(Optional.of(org));
    when(authentication.getPrincipal()).thenReturn(userDetails);
    when(userDetails.getUsername()).thenReturn("currentuser@example.com");
    when(userRepository.findByEmail("currentuser@example.com")).thenReturn(
      Optional.of(user)
    );

    Exception exception = assertThrows(RuntimeException.class, () -> {
      orgService.deleteOrg(orgID);
    });

    assertEquals(
      "User does not have permission to delete org",
      exception.getMessage()
    );
  }

  @Test
  public void testGetOrgMembers_Success() {
    int orgID = 1;
    OrgModel org = new OrgModel();
    UserModel user = new UserModel();
    user.setEmail("test@example.com");
    org.setUsers(Set.of(user));

    when(orgRepository.findById(orgID)).thenReturn(Optional.of(org));

    Set<UserDTO> members = orgService.getOrgMembers(orgID);

    assertEquals(1, members.size());
    assertEquals("test@example.com", members.iterator().next().getEmail());
  }

  @Test
  public void testGetOrgMembers_OrgNotFound() {
    int orgID = 1;

    when(orgRepository.findById(orgID)).thenReturn(Optional.empty());

    Exception exception = assertThrows(RuntimeException.class, () -> {
      orgService.getOrgMembers(orgID);
    });

    assertEquals("Org not found with id: " + orgID, exception.getMessage());
  }

  @Test
  public void testAddUser_Success() {
    int orgID = 1;
    String email = "newuser@example.com";
    UserModel userToAdd = new UserModel();
    userToAdd.setEmail(email);
    OrgModel org = new OrgModel();
    org.setOrgOwnerID(1);
    UserModel currentUser = new UserModel();
    currentUser.setUserID(1);

    when(userRepository.findByEmail(email)).thenReturn(Optional.of(userToAdd));
    when(orgRepository.findById(orgID)).thenReturn(Optional.of(org));
    when(authentication.getPrincipal()).thenReturn(userDetails);
    when(userDetails.getUsername()).thenReturn("currentuser@example.com");
    when(userRepository.findByEmail("currentuser@example.com")).thenReturn(
      Optional.of(currentUser)
    );

    orgService.addUser(orgID, email);

    verify(orgRepository, times(1)).save(org);
    verify(userRepository, times(1)).save(userToAdd);
    assertTrue(org.getUsers().contains(userToAdd));
    assertTrue(userToAdd.getOrgs().contains(org));
  }

  @Test
  public void testAddUser_UserNotFound() {
    int orgID = 1;
    String email = "newuser@example.com";

    when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

    Exception exception = assertThrows(RuntimeException.class, () -> {
      orgService.addUser(orgID, email);
    });

    assertEquals("User not found with email: " + email, exception.getMessage());
  }

  @Test
  public void testAddUser_OrgNotFound() {
    int orgID = 1;
    String email = "newuser@example.com";
    UserModel userToAdd = new UserModel();
    userToAdd.setEmail(email);

    when(userRepository.findByEmail(email)).thenReturn(Optional.of(userToAdd));
    when(orgRepository.findById(orgID)).thenReturn(Optional.empty());

    Exception exception = assertThrows(RuntimeException.class, () -> {
      orgService.addUser(orgID, email);
    });

    assertEquals("Org not found with id: " + orgID, exception.getMessage());
  }

  @Test
  public void testAddUser_NoPermission() {
    int orgID = 1;
    String email = "newuser@example.com";
    UserModel userToAdd = new UserModel();
    userToAdd.setEmail(email);
    OrgModel org = new OrgModel();
    org.setOrgOwnerID(1);
    UserModel currentUser = new UserModel();
    currentUser.setUserID(2);

    when(userRepository.findByEmail(email)).thenReturn(Optional.of(userToAdd));
    when(orgRepository.findById(orgID)).thenReturn(Optional.of(org));
    when(authentication.getPrincipal()).thenReturn(userDetails);
    when(userDetails.getUsername()).thenReturn("currentuser@example.com");
    when(userRepository.findByEmail("currentuser@example.com")).thenReturn(
      Optional.of(currentUser)
    );

    Exception exception = assertThrows(RuntimeException.class, () -> {
      orgService.addUser(orgID, email);
    });

    assertEquals(
      "User does not have permission to add user to org",
      exception.getMessage()
    );
  }

  @Test
  public void testRemoveUser_Success() {
    int orgID = 1;
    String email = "user@example.com";
    UserModel userToRemove = new UserModel();
    userToRemove.setEmail(email);
    OrgModel org = new OrgModel();
    org.setOrgOwnerID(1);
    org.setUsers(new HashSet<>(Set.of(userToRemove)));
    UserModel currentUser = new UserModel();
    currentUser.setUserID(1);

    when(userRepository.findByEmail(email)).thenReturn(
      Optional.of(userToRemove)
    );
    when(orgRepository.findById(orgID)).thenReturn(Optional.of(org));
    when(authentication.getPrincipal()).thenReturn(userDetails);
    when(userDetails.getUsername()).thenReturn("currentuser@example.com");
    when(userRepository.findByEmail("currentuser@example.com")).thenReturn(
      Optional.of(currentUser)
    );

    orgService.removeUser(orgID, email);

    verify(orgRepository, times(1)).save(org);
    verify(userRepository, times(1)).save(userToRemove);
    assertFalse(org.getUsers().contains(userToRemove));
    assertFalse(userToRemove.getOrgs().contains(org));
  }

  @Test
  public void testRemoveUser_UserNotFound() {
    int orgID = 1;
    String email = "user@example.com";

    when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

    Exception exception = assertThrows(RuntimeException.class, () -> {
      orgService.removeUser(orgID, email);
    });

    assertEquals("User not found with email: " + email, exception.getMessage());
  }

  @Test
  public void testRemoveUser_OrgNotFound() {
    int orgID = 1;
    String email = "user@example.com";
    UserModel userToRemove = new UserModel();
    userToRemove.setEmail(email);

    when(userRepository.findByEmail(email)).thenReturn(
      Optional.of(userToRemove)
    );
    when(orgRepository.findById(orgID)).thenReturn(Optional.empty());

    Exception exception = assertThrows(RuntimeException.class, () -> {
      orgService.removeUser(orgID, email);
    });

    assertEquals("Org not found with id: " + orgID, exception.getMessage());
  }

  @Test
  public void testRemoveUser_NoPermission() {
    int orgID = 1;
    String email = "user@example.com";
    UserModel userToRemove = new UserModel();
    userToRemove.setEmail(email);
    OrgModel org = new OrgModel();
    org.setOrgOwnerID(1);
    org.setUsers(new HashSet<>(Set.of(userToRemove)));
    UserModel currentUser = new UserModel();
    currentUser.setUserID(2);

    when(userRepository.findByEmail(email)).thenReturn(
      Optional.of(userToRemove)
    );
    when(orgRepository.findById(orgID)).thenReturn(Optional.of(org));
    when(authentication.getPrincipal()).thenReturn(userDetails);
    when(userDetails.getUsername()).thenReturn("currentuser@example.com");
    when(userRepository.findByEmail("currentuser@example.com")).thenReturn(
      Optional.of(currentUser)
    );

    Exception exception = assertThrows(RuntimeException.class, () -> {
      orgService.removeUser(orgID, email);
    });

    assertEquals(
      "User does not have permission to remove user from org",
      exception.getMessage()
    );
  }

  @Test
  public void testGetArchivedProjects_Success() {
    int orgID = 1;
    OrgModel org = new OrgModel();
    UserModel currentUser = new UserModel();
    currentUser.setUserID(1);
    ProjectModel project = new ProjectModel();
    project.setProjectOwnerID(1);
    project.setArchived(true);

    when(orgRepository.findById(orgID)).thenReturn(Optional.of(org));
    when(authentication.getPrincipal()).thenReturn(userDetails);
    when(userDetails.getUsername()).thenReturn("currentuser@example.com");
    when(userRepository.findByEmail("currentuser@example.com")).thenReturn(
      Optional.of(currentUser)
    );
    when(projectRepository.findByParentOrgID(orgID)).thenReturn(
      List.of(project)
    );

    Set<ProjectDTO> archivedProjects = orgService.getArchivedProjects(orgID);

    assertEquals(1, archivedProjects.size());
    assertTrue(archivedProjects.iterator().next().getIsArchived());
  }

  @Test
  public void testGetArchivedProjects_OrgNotFound() {
    int orgID = 1;

    when(orgRepository.findById(orgID)).thenReturn(Optional.empty());

    Exception exception = assertThrows(RuntimeException.class, () -> {
      orgService.getArchivedProjects(orgID);
    });

    assertEquals("Org not found with id: " + orgID, exception.getMessage());
  }

  @Test
  public void testGetArchivedProjects_NoPermission() {
    int orgID = 1;
    OrgModel org = new OrgModel();
    UserModel currentUser = new UserModel();
    currentUser.setUserID(2);
    ProjectModel project = new ProjectModel();
    project.setProjectOwnerID(1);
    project.setArchived(true);

    when(orgRepository.findById(orgID)).thenReturn(Optional.of(org));
    when(authentication.getPrincipal()).thenReturn(userDetails);
    when(userDetails.getUsername()).thenReturn("currentuser@example.com");
    when(userRepository.findByEmail("currentuser@example.com")).thenReturn(
      Optional.of(currentUser)
    );
    when(projectRepository.findByParentOrgID(orgID)).thenReturn(
      List.of(project)
    );

    Exception exception = assertThrows(RuntimeException.class, () -> {
      orgService.getArchivedProjects(orgID);
    });

    assertEquals(
      "There are no archived projects accessible by this user",
      exception.getMessage()
    );
  }

  @Test
  public void testCreateOrgJson() {
    OrgModel org = new OrgModel();
    org.setOrgID(1);
    org.setOrgName("Test Org");
    org.setOrgDescription("Description");
    org.setOrgOwnerID(1);
    org.setEncodedImage("image");
    UserModel user = new UserModel();
    user.setUserID(1);
    user.setUserName("Test User");
    user.setEmail("test@example.com");
    org.setUsers(Set.of(user));

    when(userService.createUserJson(user)).thenReturn(
      "{\"userID\": 1,\"userName\": \"Test User\",\"email\": \"test@example.com\"}"
    );

    String orgJson = orgService.createOrgJson(org);

    assertEquals(
      "{\"orgID\": 1,\"orgName\": \"Test Org\",\"orgDescription\": \"Description\",\"orgOwnerID\": 1,\"encodedImage\": \"image\",\"users\": [{\"userID\": 1,\"userName\": \"Test User\",\"email\": \"test@example.com\"}]}",
      orgJson
    );
  }
}