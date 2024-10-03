package com.aegis.project.service;

import com.aegis.project.model.ProjectModel;
import com.aegis.project.model.UserModel;
import com.aegis.project.repository.ProjectRepository;
import com.aegis.project.repository.TaskRepository;
import com.aegis.project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserRepository userRepository;

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

    public boolean createProject(int parentOrgID, String projectName, String projectDescription, int projectOwnerID) {
        ProjectModel project = new ProjectModel();
        project.setParentOrgID(parentOrgID);
        project.setProjectName(projectName);
        project.setProjectDescription(projectDescription);
        project.setProjectOwnerID(projectOwnerID);
        projectRepository.save(project);
        return true;
    }
}
