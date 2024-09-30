package com.aegis.project.service;

import com.aegis.project.model.ProjectModel;
import com.aegis.project.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

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
