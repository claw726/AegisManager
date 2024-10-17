package com.aegis.project.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.aegis.project.dto.ProjectDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.aegis.project.dto.OrgDTO;
import com.aegis.project.dto.UserDTO;
import com.aegis.project.model.OrgModel;
import com.aegis.project.model.ProjectModel;
import com.aegis.project.model.UserModel;
import com.aegis.project.repository.OrgRepository;
import com.aegis.project.repository.ProjectRepository;
import com.aegis.project.repository.UserRepository;

@Service
public class OrgService {

    @Autowired
    private OrgRepository orgRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private ProjectRepository taskRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private ProjectService projectService;

    public boolean createOrg(String name, String description, int ownerID, String encodedImage) {
        OrgModel org = new OrgModel();
        org.setOrgName(name);
        org.setOrgDescription(description);
        org.setOrgOwnerID(ownerID);
        org.setEncodedImage(encodedImage);
        orgRepository.save(org);
        addUser(org.getOrgID(), userRepository.findById(ownerID).get().getEmail());
        return true;
    }

    public Set<OrgDTO> getAllOrgs() {
        List<OrgModel> orgs = orgRepository.findAll();

        return orgs.stream()
                .map(org -> new OrgDTO(org.getOrgID(), org.getOrgName(), org.getOrgDescription(), org.getOrgOwnerID(), org.getEncodedImage(), getOrgMembers(org.getOrgID())))
                .collect(Collectors.toSet());
    }

    public OrgModel getOrg(int orgID) {
        OrgModel org = orgRepository.findById(orgID)
                .orElseThrow(() -> new RuntimeException("Org not found with id: " + orgID));

        // Get the current user's email for permission checking
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = ((UserDetails) authentication.getPrincipal()).getUsername();

        UserModel currentUser = userRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + currentUsername));

        // Check if the user has permission to access the organization
        boolean hasPermission = org.getUsers().stream()
                .anyMatch(user -> user.getUserID() == currentUser.getUserID())
                || org.getOrgOwnerID() == currentUser.getUserID();

        if (!hasPermission) {
            throw new RuntimeException("User does not have permission to get org");
        }

        return org; // Return the OrgModel directly
    }

    public Set<ProjectDTO> getAllProjectsFromOrg(int orgID) {
        OrgModel org = orgRepository.findById(orgID)
                .orElseThrow(() -> new RuntimeException("Org not found with id: " + orgID));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = ((UserDetails) authentication.getPrincipal()).getUsername();

        UserModel currentUser = userRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + currentUsername));
        boolean hasPermission = false;
        for (UserModel user : org.getUsers()) {
            if (user.getUserID() == currentUser.getUserID()) {
                hasPermission = true;
                break;
            }
        }
        if (org.getOrgOwnerID() == currentUser.getUserID()) {
            hasPermission = true;
        }
        if (!hasPermission) {
            throw new RuntimeException("User does not have permission to get projects from org");
        }

        List<ProjectModel> allProjects = projectRepository.findByParentOrgID(orgID);
        return allProjects.stream()
                .map(project -> new ProjectDTO(project.getProjectID(), project.getParentOrgID(), project.getProjectName(),
                        project.getProjectDescription(), project.getProjectOwnerID(), project.getEncodedImage(),
                        projectService.getAssignedUsers(project.getProjectID()), projectService.getProjectTasks(project.getProjectID())))
                .collect(Collectors.toSet());
    }

    public void updateOrg(int orgID, String orgName, String orgDescription, int orgOwnerID) {
        OrgModel org = orgRepository.findById(orgID)
                .orElseThrow(() -> new RuntimeException("Org not found with id: " + orgID));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = ((UserDetails) authentication.getPrincipal()).getUsername();

        UserModel currentUser = userRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + currentUsername));

        if (org.getOrgOwnerID() != currentUser.getUserID()) {
            throw new RuntimeException("User does not have permission to update org");
        }

        org.setOrgName(orgName);
        org.setOrgDescription(orgDescription);
        org.setOrgOwnerID(orgOwnerID);
        orgRepository.save(org);
    }

    public void deleteOrg(int orgID) {
        OrgModel org = orgRepository.findById(orgID)
                .orElseThrow(() -> new RuntimeException("Org not found with id: " + orgID));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = ((UserDetails) authentication.getPrincipal()).getUsername();

        UserModel currentUser = userRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + currentUsername));

        if (org.getOrgOwnerID() != currentUser.getUserID()) {
            throw new RuntimeException("User does not have permission to delete org");
        }

        projectRepository.deleteByParentOrgID(orgID);
        taskRepository.deleteByParentOrgID(orgID);

        orgRepository.deleteById(orgID);
    }

    public Set<UserDTO> getOrgMembers(int orgID) {
        OrgModel org = orgRepository.findById(orgID)
                .orElseThrow(() -> new RuntimeException("Org not found with ID: " + orgID));

        Set<UserModel> members = org.getUsers();
        return members.stream()
                .map(user -> new UserDTO(user.getUserID(), user.getUserName(), user.getEmail(), user.getProfilePicture()))
                .collect(Collectors.toSet());
    }

    public void addUser(int orgID, String email) {
        UserModel userToAdd = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        OrgModel org = orgRepository.findById(orgID)
                .orElseThrow(() -> new RuntimeException("Org not found with id: " + orgID));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = ((UserDetails) authentication.getPrincipal()).getUsername();

        UserModel currentUser = userRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + currentUsername));

        if (org.getOrgOwnerID() != currentUser.getUserID()) {
            throw new RuntimeException("User does not have permission to add user to org");
        }
        org.getUsers().add(userToAdd);
        orgRepository.save(org);

        userToAdd.getOrgs().add(org);
        userRepository.save(userToAdd);
    }

    public void removeUser(int orgID, String email) {
        UserModel userToRemove = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        OrgModel org = orgRepository.findById(orgID)
                .orElseThrow(() -> new RuntimeException("Org not found with id: " + orgID));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = ((UserDetails) authentication.getPrincipal()).getUsername();

        UserModel currentUser = userRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + currentUsername));

        if (org.getOrgOwnerID() != currentUser.getUserID()) {
            throw new RuntimeException("User does not have permission to remove user from org");
        }

        org.getUsers().remove(userToRemove);
        orgRepository.save(org);
    }

    public String createOrgJson(OrgModel org) {
        String ret = "{"
                + "\"orgID\": " + org.getOrgID() + ","
                + "\"orgName\": \"" + org.getOrgName() + "\","
                + "\"orgDescription\": \"" + org.getOrgDescription() + "\","
                + "\"orgOwnerID\": " + org.getOrgOwnerID() + ","
                + "\"encodedImage\": \"" + org.getEncodedImage() + "\","
                + "\"users\": [";
        boolean first = true;
        for (UserModel user : org.getUsers()) {
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
}
