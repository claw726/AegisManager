package com.aegis.project.service;

import com.aegis.project.model.ProjectModel;
import com.aegis.project.repository.ProjectRepository;
import com.aegis.project.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private TaskRepository taskRepository;

    public void deleteProject(int projectID) {
        if (!projectRepository.existsById(projectID)) {
            throw new RuntimeException("Project not found with ID: " + projectID);
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
