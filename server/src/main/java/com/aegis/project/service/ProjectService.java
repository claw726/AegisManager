package com.aegis.project.service;

import com.aegis.project.model.ProjectModel;
import com.aegis.project.model.UserModel;
import com.aegis.project.dto.TaskDTO;
import com.aegis.project.dto.UserDTO;
import com.aegis.project.model.OrgModel;
import com.aegis.project.model.TaskModel;
import com.aegis.project.repository.ProjectRepository;
import com.aegis.project.repository.OrgRepository;
import com.aegis.project.repository.TaskRepository;
import com.aegis.project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.HashSet;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrgRepository orgRepository;
    @Autowired
    private SimpMessagingTemplate simpMessageTemplate;



    public void editProject(int projectID) {
        ProjectModel project = projectRepository.findById(projectID)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + projectID));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = ((UserDetails) authentication.getPrincipal()).getUsername();

        UserModel currentUser = userRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + currentUsername));

        if (project.getProjectOwnerID() != currentUser.getUserID()) {
            throw new RuntimeException("User does not have permission to edit project");
        }
    }

    public void deleteProject(int projectID) {
        ProjectModel project = projectRepository.findById(projectID)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + projectID));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = ((UserDetails) authentication.getPrincipal()).getUsername();

        UserModel currentUser = userRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + currentUsername));

        if (project.getProjectOwnerID() != currentUser.getUserID()) {
            throw new RuntimeException("User does not have permission to delete project");
        }

        taskRepository.deleteByParentProjectID(projectID);

        projectRepository.deleteById(projectID);
    }

    public boolean createProject(String projectName, String projectDescription, int projectOwnerID, int parentOrgID, String encodedImage) {
        if (projectRepository.existsProjectByOrgAndName(parentOrgID, projectName)) {
            throw new RuntimeException("Project with given name already exists in org");
        }
        ProjectModel project = new ProjectModel();
        project.setParentOrgID(parentOrgID);
        project.setProjectName(projectName);
        project.setProjectDescription(projectDescription);
        project.setProjectOwnerID(projectOwnerID);
        project.setEncodedImage(encodedImage);
        projectRepository.save(project);

        OrgModel parentOrg = orgRepository.findById(parentOrgID)
            .orElseThrow(() -> new RuntimeException("Org not found with id: " + parentOrgID));
        Set<ProjectModel> orgProjects = parentOrg.getOrgProjects();
        orgProjects.add(project);
        orgRepository.save(parentOrg);
        return true;
    }

    public String getProject(int projectID) {
        ProjectModel project = projectRepository.findById(projectID)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + projectID));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = ((UserDetails) authentication.getPrincipal()).getUsername();

        UserModel currentUser = userRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + currentUsername));
        boolean hasPermission = false;
        for (UserModel user : project.getAssignedUsers()) {
            if (user.getUserID() == currentUser.getUserID()) {
                hasPermission = true;
                break;
            }
        }
        if (project.getProjectOwnerID() != currentUser.getUserID()) {
            hasPermission = true;
        }
        if (!hasPermission) {
            throw new RuntimeException("User does not have permission to get project");
        }
        return createProjectJson(project);
    }

    public void updateProject(int projectID, String projectName, String projectDescription, int projectOwnerID) {
        ProjectModel project = projectRepository.findById(projectID)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + projectID));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = ((UserDetails) authentication.getPrincipal()).getUsername();

        UserModel currentUser = userRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + currentUsername));

        if (project.getProjectOwnerID() != currentUser.getUserID()) {
            throw new RuntimeException("User does not have permission to update project");
        }
        project.setProjectName(projectName);
        project.setProjectDescription(projectDescription);
        project.setProjectOwnerID(projectOwnerID);
        projectRepository.save(project);        
    }

    public Set<TaskDTO> getProjectTasks(int projectID) {
        ProjectModel project = projectRepository.findById(projectID)
                .orElseThrow(() -> new RuntimeException("Project not found with ID: " + projectID));

        Set<TaskModel> tasks = project.getProjectTasks();
        return tasks.stream()
                .map(task -> new TaskDTO(task.getTaskID(), task.getParentProjectID(), task.getParentOrgID(), task.getTaskName(), task.getTaskDescription(), task.getAssignerID(), task.getTaskPriority(), task.getDueDate(), task.isComplete()))
                .collect(Collectors.toSet());
    }

    public Set<UserDTO> getAssignedUsers(int projectID) {
        ProjectModel project = projectRepository.findById(projectID)
                .orElseThrow(() -> new RuntimeException("Project not found with ID: " + projectID));

        Set<UserModel> members = project.getAssignedUsers();
        return members.stream()
                .map(user -> new UserDTO(user.getUserID(), user.getUserName(), user.getEmail(), user.getProfilePicture()))
                .collect(Collectors.toSet());
    }

    public String createProjectJson(ProjectModel project) {
        return "{"
                + "\"projectID\": " + project.getProjectID() + ","
                + "\"parentOrgID\": " + project.getParentOrgID() + ","
                + "\"projectName\": " + project.getProjectName() + ","
                + "\"projectDescription\": " + project.getProjectDescription() + ","
                + "\"projectOwnerID\": " + project.getProjectOwnerID() + "\""
                + "\"encodedImage\": " + project.getEncodedImage() + "\""
                + "}";
    }

    public void addUsers(int projectID, List<Integer> userIDs) {
        ProjectModel project = projectRepository.findById(projectID)
            .orElseThrow(() -> new RuntimeException("Project not found with id: " + projectID));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = ((UserDetails) authentication.getPrincipal()).getUsername();

        UserModel currentUser = userRepository.findByEmail(currentUsername)
            .orElseThrow(() -> new RuntimeException("User not found with email: " + currentUsername));

        if (project.getProjectOwnerID() != currentUser.getUserID()) {
            throw new RuntimeException("User does not have permission to add users to project");
        }

        for (int userID : userIDs) {
            UserModel userToAdd = userRepository.findById(userID)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userID));
            project.getAssignedUsers().add(userToAdd);
            simpMessageTemplate.convertAndSendToUser(userToAdd.getEmail(), "/queue/project-updates",
                    "User added to project with ID: " + projectID);
        }
        projectRepository.save(project);
    }

    public void addUser(int projectID, String email) {
        UserModel userToAdd = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        
        ProjectModel project = projectRepository.findById(projectID)
            .orElseThrow(() -> new RuntimeException("Project not found with id: " + projectID));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = ((UserDetails) authentication.getPrincipal()).getUsername();

        UserModel currentUser = userRepository.findByEmail(currentUsername)
            .orElseThrow(() -> new RuntimeException("User not found with email: " + currentUsername));

        if (project.getProjectOwnerID() != currentUser.getUserID()) {
            throw new RuntimeException("User does not have permission to add user to project");
        }
        project.getAssignedUsers().add(userToAdd);
        simpMessageTemplate.convertAndSendToUser(userToAdd.getEmail(), "/queue/project-updates",
                "User added to project with ID: " + projectID);
        projectRepository.save(project);
    }

    public void removeUser(int projectID, String email){
        UserModel userToRemove = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        
        ProjectModel project = projectRepository.findById(projectID)
            .orElseThrow(() -> new RuntimeException("Project not found with id: " + projectID));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = ((UserDetails) authentication.getPrincipal()).getUsername();

        UserModel currentUser = userRepository.findByEmail(currentUsername)
            .orElseThrow(() -> new RuntimeException("User not found with email: " + currentUsername));

        if (project.getProjectOwnerID() != currentUser.getUserID()) {
            throw new RuntimeException("User does not have permission to remove user from project");
        }

        project.getAssignedUsers().remove(userToRemove);
        projectRepository.save(project);

        OrgModel parentOrg = orgRepository.findById(project.getParentOrgID())
            .orElseThrow(() -> new RuntimeException("Parent org not found with id: " + projectID));
        parentOrg.getOrgProjects().remove(project);
        orgRepository.save(parentOrg);
        taskRepository.deleteByParentProjectID(projectID);
    }
}
