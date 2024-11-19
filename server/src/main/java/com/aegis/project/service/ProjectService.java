package com.aegis.project.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.aegis.project.model.*;
import com.aegis.project.repository.*;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.aegis.project.controller.SocketIOController;
import com.aegis.project.dto.ProjectDTO;
import com.aegis.project.dto.TaskDTO;
import com.aegis.project.dto.UserDTO;
import com.aegis.project.exception.ProjectFetchException;
import com.aegis.project.exception.UserNotFoundException;

@Service
public class ProjectService {

    private static final Logger LOGGER = LoggerFactory.getLogger(
            ProjectService.class
    );

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrgRepository orgRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private SocketIOController socketIOController;
    @Autowired
    private ChatRepository chatRepository;

    public void editProject(int projectID) {
        ProjectModel project = projectRepository
                .findById(projectID)
                .orElseThrow(()
                        -> new RuntimeException("Project not found with id: " + projectID)
                );

        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        String currentUsername
                = ((UserDetails) authentication.getPrincipal()).getUsername();

        UserModel currentUser = userRepository
                .findByEmail(currentUsername)
                .orElseThrow(()
                        -> new RuntimeException("User not found with email: " + currentUsername)
                );

        if (project.getProjectOwnerID() != currentUser.getUserID()) {
            throw new RuntimeException(
                    "User does not have permission to edit project"
            );
        }
    }

    @Transactional
    public void deleteProject(int projectID) {
        ProjectModel project = projectRepository
                .findById(projectID)
                .orElseThrow(()
                        -> new RuntimeException("Project not found with id: " + projectID)
                );

        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        String currentUsername
                = ((UserDetails) authentication.getPrincipal()).getUsername();

        UserModel currentUser = userRepository
                .findByEmail(currentUsername)
                .orElseThrow(()
                        -> new RuntimeException("User not found with email: " + currentUsername)
                );

        if (project.getProjectOwnerID() != currentUser.getUserID()) {
            throw new RuntimeException(
                    "User does not have permission to delete project"
            );
        }

        Set<UserModel> users = project.getAssignedUsers();
        for (UserModel user : users) {
            socketIOController.sendMessage(
                    new SocketMessageModel(
                            "server",
                            user.getEmail(),
                            "project-" + projectID,
                            "Deleted project with ID: " + projectID
                    )
            );
        }

        taskRepository.deleteByParentProjectID(projectID);

        projectRepository.deleteById(projectID);


    }

    public boolean createProject(
            String projectName,
            String projectDescription,
            int projectOwnerID,
            int parentOrgID,
            String encodedImage
    ) {
        if (projectRepository.existsProjectByOrgAndName(parentOrgID, projectName)) {
            throw new RuntimeException(
                    "Project with given name already exists in org"
            );
        }
        ProjectModel project = new ProjectModel();
        project.setParentOrgID(parentOrgID);
        project.setProjectName(projectName);
        project.setProjectDescription(projectDescription);
        project.setProjectOwnerID(projectOwnerID);
        project.setEncodedImage(encodedImage);
        project.setParentOrgID(parentOrgID);
        project.setArchived(false);

        Set<Integer> participants = new HashSet<>();
        participants.add(projectOwnerID);
        ChatModel chat = new ChatModel("project", projectName, participants);
        chatRepository.save(chat);

        project.setChatID(chat.getChatID());
        projectRepository.save(project);

        try {
            addUser(
                    project.getProjectID(),
                    userRepository.findById(projectOwnerID).get().getEmail()
            );
        } catch (Exception e) {
            projectRepository.deleteById(project.getProjectID());
            chatRepository.deleteById(chat.getChatID());
            LOGGER.error("Error adding project owner to project");
            throw new RuntimeException("Error adding project owner to project");
        }

        OrgModel parentOrg = orgRepository
                .findById(parentOrgID)
                .orElseThrow(()
                        -> new RuntimeException("Org not found with id: " + parentOrgID)
                );

        project.setParentOrg(parentOrg);

        Set<ProjectModel> orgProjects = parentOrg.getOrgProjects();
        orgProjects.add(project);

        orgRepository.save(parentOrg);
        return true;
    }

    public ProjectDTO getProject(int projectID) {
        ProjectModel project = projectRepository
                .findById(projectID)
                .orElseThrow(()
                        -> new RuntimeException("Project not found with id: " + projectID)
                );
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        String currentUsername
                = ((UserDetails) authentication.getPrincipal()).getUsername();

        UserModel currentUser = userRepository
                .findByEmail(currentUsername)
                .orElseThrow(()
                        -> new RuntimeException("User not found with email: " + currentUsername)
                );

        boolean hasPermission
                = project
                        .getAssignedUsers()
                        .stream()
                        .anyMatch(user -> user.getUserID() == currentUser.getUserID())
                || project.getProjectOwnerID() == currentUser.getUserID();

        if (!hasPermission) {
            LOGGER.error("User does not have permission to get project");
            throw new RuntimeException(
                    "User does not have permission to get project"
            );
        }

        return new ProjectDTO(project);
    }

    public ProjectDTO directlyGetProject(int projectID) {
        ProjectModel project = projectRepository
                .findById(projectID)
                .orElseThrow(()
                        -> new RuntimeException("Project not found with id: " + projectID)
                );
        return new ProjectDTO(project);
    }

    public void updateProject(
            int projectID,
            String projectName,
            String projectDescription,
            int projectOwnerID,
            String encodedImage
    ) {
        ProjectModel project = projectRepository
                .findById(projectID)
                .orElseThrow(()
                        -> new RuntimeException("Project not found with id: " + projectID)
                );
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        String currentUsername
                = ((UserDetails) authentication.getPrincipal()).getUsername();

        UserModel currentUser = userRepository
                .findByEmail(currentUsername)
                .orElseThrow(()
                        -> new RuntimeException("User not found with email: " + currentUsername)
                );

        if (project.getProjectOwnerID() != currentUser.getUserID()) {
            throw new RuntimeException(
                    "User does not have permission to update project"
            );
        }

        if (!projectName.equals(project.getProjectName())
                && projectRepository.existsProjectByOrgAndName(
                        project.getParentOrgID(),
                        projectName
                )) {
            throw new RuntimeException(
                    "Project with given name already exists in org"
            );
        }

        project.setProjectName(projectName);
        project.setProjectDescription(projectDescription);
        project.setProjectOwnerID(projectOwnerID);
        project.setEncodedImage(encodedImage);
        projectRepository.save(project);
    }

    public Set<TaskDTO> getProjectTasks(int projectID) {
        ProjectModel project = projectRepository
                .findById(projectID)
                .orElseThrow(()
                        -> new RuntimeException("Project not found with id: " + projectID)
                );

        Set<TaskModel> tasks = project.getProjectTasks();
        return tasks
                .stream()
                .map(TaskDTO::new)
                .collect(Collectors.toSet());
    }

    public Set<UserDTO> getAssignedUsers(int projectID) {
        ProjectModel project = projectRepository
                .findById(projectID)
                .orElseThrow(()
                        -> new RuntimeException("Project not found with id: " + projectID)
                );

        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        String currentUsername
                = ((UserDetails) authentication.getPrincipal()).getUsername();

        UserModel currentUser = userRepository
                .findByEmail(currentUsername)
                .orElseThrow(()
                        -> new RuntimeException("User not found with email: " + currentUsername)
                );
        boolean hasPermission = false;

        Set<UserModel> members = project.getAssignedUsers();

        for (UserModel user : members) {
            if (user.getUserID() == currentUser.getUserID()) {
                hasPermission = true;
                break;
            }
        }
        if (!hasPermission) {
            throw new RuntimeException("User does not have permission to view users");
        }

        return members
                .stream()
                .map(user -> new UserDTO(user))
                .collect(Collectors.toSet());
    }

    public String createProjectJson(ProjectModel project) {
        String ret
                = "{"
                + "\"projectID\": "
                + project.getProjectID()
                + ","
                + "\"parentOrgID\": "
                + project.getParentOrgID()
                + ","
                + "\"projectName\": \""
                + project.getProjectName()
                + "\","
                + "\"projectDescription\": \""
                + project.getProjectDescription()
                + "\","
                + "\"projectOwnerID\": "
                + project.getProjectOwnerID()
                + ","
                + "\"encodedImage\": \""
                + project.getEncodedImage()
                + "\","
                + "\"assignedUsers\": [";
        boolean first = true;
        for (UserModel user : project.getAssignedUsers()) {
            if (first) {
                first = false;
            } else {
                ret += ",";
            }
            ret += userService.createUserJson(user);
        }
        ret += "]}";
        return ret;
    }

    public void addUsers(int projectID, List<Integer> userIDs) {
        ProjectModel project = projectRepository
                .findById(projectID)
                .orElseThrow(()
                        -> new RuntimeException("Project not found with id: " + projectID)
                );

        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        String currentUsername
                = ((UserDetails) authentication.getPrincipal()).getUsername();

        UserModel currentUser = userRepository
                .findByEmail(currentUsername)
                .orElseThrow(()
                        -> new RuntimeException("User not found with email: " + currentUsername)
                );

        if (project.getProjectOwnerID() != currentUser.getUserID()) {
            throw new RuntimeException(
                    "User does not have permission to add users to project"
            );
        }

        ChatModel chat = chatRepository.findById(project.getChatID()).orElseThrow(() ->
                new RuntimeException("Chat not found with id: " + project.getChatID()));

        for (int userID : userIDs) {
            UserModel userToAdd = userRepository
                    .findById(userID)
                    .orElseThrow(()
                            -> new RuntimeException("User not found with id: " + userID)
                    );
            project.getAssignedUsers().add(userToAdd);
            chat.addParticipant(userID);
            socketIOController.sendMessage(
                    new SocketMessageModel(
                            "server",
                            userToAdd.getEmail(),
                            "project-" + projectID,
                            "User added to project with ID: " + projectID
                    )
            );
        }
        chatRepository.save(chat);
        projectRepository.save(project);
    }

    public void addUser(int projectID, String email) {
        ProjectModel project = projectRepository
                .findById(projectID)
                .orElseThrow(()
                        -> new RuntimeException("Project not found with id: " + projectID)
                );

        UserModel userToAdd = userRepository
                .findByEmail(email)
                .orElseThrow(()
                        -> new RuntimeException("User not found with email: " + email)
                );

        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        String currentUsername
                = ((UserDetails) authentication.getPrincipal()).getUsername();

        UserModel currentUser = userRepository
                .findByEmail(currentUsername)
                .orElseThrow(()
                        -> new RuntimeException("User not found with email: " + currentUsername)
                );

        if (project.getProjectOwnerID() != currentUser.getUserID()) {
            throw new RuntimeException(
                    "User does not have permission to add user to project"
            );
        }
        project.getAssignedUsers().add(userToAdd);
        socketIOController.sendMessage(
                new SocketMessageModel(
                        "server",
                        userToAdd.getEmail(),
                        "project-" + projectID,
                        "User added to project with ID: " + projectID
                )
        );

        ChatModel chat = chatRepository.findById(project.getChatID()).orElseThrow(() ->
                new RuntimeException("Chat not found with id: " + project.getChatID()));
        chat.addParticipant(userToAdd.getUserID());

        chatRepository.save(chat);
        projectRepository.save(project);
    }

    public void directlyAddUser(int projectID, String email) {
        ProjectModel project = projectRepository
                .findById(projectID)
                .orElseThrow(()
                        -> new RuntimeException("Project not found with id: " + projectID)
                );

        UserModel userToAdd = userRepository
                .findByEmail(email)
                .orElseThrow(()
                        -> new RuntimeException("User not found with email: " + email)
                );
        project.getAssignedUsers().add(userToAdd);
        socketIOController.sendMessage(
                new SocketMessageModel(
                        "server",
                        userToAdd.getEmail(),
                        "project-" + projectID,
                        "User added to project with ID: " + projectID
                )
        );

        ChatModel chat = chatRepository.findById(project.getChatID()).orElseThrow(() ->
                new RuntimeException("Chat not found with id: " + project.getChatID()));
        chat.addParticipant(userToAdd.getUserID());

        chatRepository.save(chat);
        projectRepository.save(project);
    }

    public void removeUser(int projectID, String email) {
        ProjectModel project = projectRepository
                .findById(projectID)
                .orElseThrow(()
                        -> new RuntimeException("Project not found with id: " + projectID)
                );

        UserModel userToRemove = userRepository
                .findByEmail(email)
                .orElseThrow(()
                        -> new RuntimeException("User not found with email: " + email)
                );

        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        String currentUsername
                = ((UserDetails) authentication.getPrincipal()).getUsername();

        UserModel currentUser = userRepository
                .findByEmail(currentUsername)
                .orElseThrow(()
                        -> new RuntimeException("User not found with email: " + currentUsername)
                );

        if (project.getProjectOwnerID() != currentUser.getUserID()) {
            throw new RuntimeException(
                    "User does not have permission to remove user from project"
            );
        }

        project.getAssignedUsers().remove(userToRemove);

        socketIOController.sendMessage(
                new SocketMessageModel(
                        "server",
                        userToRemove.getEmail(),
                        "project-" + projectID,
                        "User removed from project with ID: " + projectID
                )
        );

        ChatModel chat = chatRepository.findById(project.getChatID()).orElseThrow(() ->
                new RuntimeException("Chat not found with id: " + project.getChatID()));
        chat.removeParticipant(userToRemove.getUserID());

        chatRepository.save(chat);
        projectRepository.save(project);
    }

    public Set<TaskDTO> getAllTasksFromProject(int projectID) {
        ProjectModel project = projectRepository
                .findById(projectID)
                .orElseThrow(()
                        -> new RuntimeException("Project not found with id: " + projectID)
                );
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        String currentUsername
                = ((UserDetails) authentication.getPrincipal()).getUsername();

        UserModel currentUser = userRepository
                .findByEmail(currentUsername)
                .orElseThrow(()
                        -> new RuntimeException("User not found with email: " + currentUsername)
                );
        boolean hasPermission = false;
        for (UserModel user : project.getAssignedUsers()) {
            if (user.getUserID() == currentUser.getUserID()) {
                hasPermission = true;
                break;
            }
        }
        if (project.getProjectOwnerID() == currentUser.getUserID()) {
            hasPermission = true;
        }
        if (!hasPermission) {
            throw new RuntimeException(
                    "User does not have permission to get tasks from project"
            );
        }
        List<TaskModel> allTasks = taskRepository.findByParentProjectID(projectID);
        return allTasks
                .stream()
                .map(TaskDTO::new)
                .collect(Collectors.toSet());
    }

    public Set<TaskDTO> getAllTasksForUser(int projectID, String email) {
        ProjectModel project = projectRepository
                .findById(projectID)
                .orElseThrow(()
                        -> new RuntimeException("Project not found with id: " + projectID)
                );
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        String currentUsername
                = ((UserDetails) authentication.getPrincipal()).getUsername();

        UserModel currentUser = userRepository
                .findByEmail(currentUsername)
                .orElseThrow(()
                        -> new RuntimeException("User not found with email: " + currentUsername)
                );
        boolean hasPermission = false;
        for (UserModel user : project.getAssignedUsers()) {
            if (user.getUserID() == currentUser.getUserID()) {
                hasPermission = true;
                break;
            }
        }
        if (project.getProjectOwnerID() == currentUser.getUserID()) {
            hasPermission = true;
        }
        if (!hasPermission) {
            throw new RuntimeException(
                    "User does not have permission to get tasks from project"
            );
        }

        List<TaskModel> allTasks = taskRepository.findByParentProjectIDAndUserID(
                projectID,
                currentUser.getUserID()
        );
        return allTasks
                .stream()
                .map(TaskDTO::new)
                .collect(Collectors.toSet());
    }

    public void changeArchivedStatus(int projectID, boolean isArchived) {
        ProjectModel project = projectRepository
                .findById(projectID)
                .orElseThrow(()
                        -> new RuntimeException("Project not found with id: " + projectID)
                );

        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        String currentUsername
                = ((UserDetails) authentication.getPrincipal()).getUsername();

        UserModel currentUser = userRepository
                .findByEmail(currentUsername)
                .orElseThrow(()
                        -> new RuntimeException("User not found with email: " + currentUsername)
                );

        if (project.getProjectOwnerID() != currentUser.getUserID()) {
            throw new RuntimeException(
                    "User does not have permission to change project archived status"
            );
        }

        project.setArchived(isArchived);
        projectRepository.save(project);
    }

    public Set<ProjectDTO> getAllUserProjects(String email) {
        try {
            UserModel user = userService
                    .findUserByEmail(email)
                    .orElseThrow(() -> new UserNotFoundException(email));

            List<ProjectModel> projects = projectRepository.findAllUserProjects(
                    user.getUserID()
            );

            return projects.stream().map(ProjectDTO::new).collect(Collectors.toSet());
        } catch (UserNotFoundException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(
                    "Error fetching projects for user {}: {}",
                    email,
                    e.getMessage(),
                    e
            );
            throw new ProjectFetchException(
                    "Error fetching projects for user: " + email,
                    e
            );
        }
    }
}
