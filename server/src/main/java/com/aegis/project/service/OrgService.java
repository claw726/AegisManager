package com.aegis.project.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.aegis.project.model.ChatModel;
import com.aegis.project.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.aegis.project.dto.OrgDTO;
import com.aegis.project.dto.ProjectDTO;
import com.aegis.project.dto.UserDTO;
import com.aegis.project.model.OrgModel;
import com.aegis.project.model.ProjectModel;
import com.aegis.project.model.UserModel;

import jakarta.transaction.Transactional;

@Service
public class OrgService {

    @Autowired
    private OrgRepository orgRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ChatRepository chatRepository;


    public boolean createOrg(
            String name,
            String description,
            int ownerID,
            String encodedImage
    ) {
        OrgModel org = new OrgModel();
        org.setOrgName(name);
        org.setOrgDescription(description);
        org.setOrgOwnerID(ownerID);
        org.setEncodedImage(encodedImage);

        Set<Integer> participants = new HashSet<>();
        participants.add(ownerID);

        ChatModel chat = new ChatModel("organization", name, participants);
        chatRepository.save(chat);

        org.setChatID(chat.getChatID());
        orgRepository.save(org);
        try {
            addUser(
                    org.getOrgID(),
                    userRepository.findById(ownerID).get().getEmail()
            );
        } catch (Exception e) {
            orgRepository.deleteById(org.getOrgID());
            throw new RuntimeException("Error adding owner to org");
        }
        return true;
    }

    public Set<OrgDTO> getAllOrgs() {
        List<OrgModel> orgs = orgRepository.findAll();

        return orgs
                .stream()
                .map(org
                        -> new OrgDTO(
                        org.getOrgID(),
                        org.getOrgName(),
                        org.getOrgDescription(),
                        org.getOrgOwnerID(),
                        org.getEncodedImage(),
                        getOrgMembers(org.getOrgID()),
                        org.getChatID()
                )
                )
                .collect(Collectors.toSet());
    }

    public OrgModel getOrg(int orgID) {
        OrgModel org = orgRepository
                .findById(orgID)
                .orElseThrow(() -> new RuntimeException("Org not found with id: " + orgID)
                );

        // Get the current user's email for permission checking
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        String currentUsername
                = ((UserDetails) authentication.getPrincipal()).getUsername();

        UserModel currentUser = userRepository
                .findByEmail(currentUsername)
                .orElseThrow(()
                        -> new RuntimeException("User not found with email: " + currentUsername)
                );

        // Check if the user has permission to access the organization
        boolean hasPermission
                = org
                        .getUsers()
                        .stream()
                        .anyMatch(user -> user.getUserID() == currentUser.getUserID())
                || org.getOrgOwnerID() == currentUser.getUserID();

        if (!hasPermission) {
            throw new RuntimeException("User does not have permission to get org");
        }

        return org; // Return the OrgModel directly
    }

    public Set<ProjectDTO> getAllProjectsFromOrg(int orgID) {
        OrgModel org = orgRepository
                .findById(orgID)
                .orElseThrow(() -> new RuntimeException("Org not found with id: " + orgID)
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
                = org
                        .getUsers()
                        .stream()
                        .anyMatch(user -> user.getUserID() == currentUser.getUserID())
                || org.getOrgOwnerID() == currentUser.getUserID();

        if (!hasPermission) {
            throw new RuntimeException(
                    "User does not have permission to get projects from org"
            );
        }

        List<ProjectModel> allProjects = projectRepository.findByParentOrgID(orgID);
        return allProjects
                .stream()
                .map(project -> new ProjectDTO(project))
                .collect(Collectors.toSet());
    }

    public void updateOrg(
            int orgID,
            String orgName,
            String orgDescription,
            int orgOwnerID,
            String encodedImage
    ) {
        OrgModel org = orgRepository
                .findById(orgID)
                .orElseThrow(() -> new RuntimeException("Org not found with id: " + orgID)
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

        if (org.getOrgOwnerID() != currentUser.getUserID()) {
            throw new RuntimeException("User does not have permission to update org");
        }

        org.setOrgName(orgName);
        org.setOrgDescription(orgDescription);
        org.setOrgOwnerID(orgOwnerID);
        org.setEncodedImage(encodedImage);
        orgRepository.save(org);
    }

    @Transactional
    public void deleteOrg(int orgID) {
        OrgModel org = orgRepository
                .findById(orgID)
                .orElseThrow(() -> new RuntimeException("Org not found with id: " + orgID)
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

        if (org.getOrgOwnerID() != currentUser.getUserID()) {
            throw new RuntimeException("User does not have permission to delete org");
        }

        for (UserModel user : org.getUsers()) {
            user.getOrgs().remove(org);
            userRepository.save(user);
        }

        ChatModel chat = chatRepository.findById(org.getChatID()).orElseThrow(() ->
                new RuntimeException("Chat not found with id: " + org.getChatID()));

        chatRepository.delete(chat);

        taskRepository.deleteByParentOrgID(orgID);
        projectRepository.deleteByParentOrgID(orgID);
        orgRepository.deleteById(orgID);
    }

    public Set<UserDTO> getOrgMembers(int orgID) {
        OrgModel org = orgRepository
                .findById(orgID)
                .orElseThrow(() -> new RuntimeException("Org not found with id: " + orgID)
                );

        Set<UserModel> members = org.getUsers();
        return members
                .stream()
                .map(user -> new UserDTO(user))
                .collect(Collectors.toSet());
    }

    public void addUser(int orgID, String email) {
        UserModel userToAdd = userRepository
                .findByEmail(email)
                .orElseThrow(()
                        -> new RuntimeException("User not found with email: " + email)
                );

        OrgModel org = orgRepository
                .findById(orgID)
                .orElseThrow(() -> new RuntimeException("Org not found with id: " + orgID)
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

        if (org.getOrgOwnerID() != currentUser.getUserID()) {
            throw new RuntimeException(
                    "User does not have permission to add user to org"
            );
        }
        org.getUsers().add(userToAdd);
        ChatModel chat = chatRepository.findById(org.getChatID()).orElseThrow(() ->
                new RuntimeException("Chat not found with id: " + org.getChatID()));
        chat.addParticipant(userToAdd.getUserID());
        orgRepository.save(org);

        userToAdd.getOrgs().add(org);
        userRepository.save(userToAdd);
    }

    public void directlyAddUser(int orgID, String email) {
        UserModel userToAdd = userRepository
                .findByEmail(email)
                .orElseThrow(()
                        -> new RuntimeException("User not found with email: " + email)
                );

        OrgModel org = orgRepository
                .findById(orgID)
                .orElseThrow(() -> new RuntimeException("Org not found with id: " + orgID)
                );

        org.getUsers().add(userToAdd);
        ChatModel chat = chatRepository.findById(org.getChatID()).orElseThrow(() ->
                new RuntimeException("Chat not found with id: " + org.getChatID()));
        chat.addParticipant(userToAdd.getUserID());
        orgRepository.save(org);

        userToAdd.getOrgs().add(org);
        userRepository.save(userToAdd);
    }

    public void removeUser(int orgID, String email) {
        UserModel userToRemove = userRepository
                .findByEmail(email)
                .orElseThrow(()
                        -> new RuntimeException("User not found with email: " + email)
                );

        OrgModel org = orgRepository
                .findById(orgID)
                .orElseThrow(() -> new RuntimeException("Org not found with id: " + orgID)
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

        if (org.getOrgOwnerID() != currentUser.getUserID()) {
            throw new RuntimeException(
                    "User does not have permission to remove user from org"
            );
        }

        org.getUsers().remove(userToRemove);
        ChatModel chat = chatRepository.findById(org.getChatID()).orElseThrow(() ->
                new RuntimeException("Chat not found with id: " + org.getChatID()));
        chat.removeParticipant(userToRemove.getUserID());
        orgRepository.save(org);

        userToRemove.getOrgs().remove(org);
        userRepository.save(userToRemove);
    }

    public Set<ProjectDTO> getArchivedProjects(int orgID) {
        orgRepository
                .findById(orgID)
                .orElseThrow(() -> new RuntimeException("Org not found with id: " + orgID)
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

        List<ProjectModel> allProjects = projectRepository.findByParentOrgID(orgID);

        List<ProjectModel> accessibleProjects = new ArrayList<>();
        for (ProjectModel project : allProjects) {
            if (project.getProjectOwnerID() == currentUser.getUserID() /*|| org.getOrgOwnerID() == currentUser.getUserID()*/) {
                if (project.isArchived()) {
                    accessibleProjects.add(project);
                }
            }
        }

        if (accessibleProjects.isEmpty()) {
            throw new RuntimeException(
                    "There are no archived projects accessible by this user"
            );
        }

        return accessibleProjects
                .stream()
                .filter(ProjectModel::isArchived)
                .map(project -> new ProjectDTO(project))
                .collect(Collectors.toSet());
    }

    public String createOrgJson(OrgModel org) {
        String ret
                = "{"
                + "\"orgID\": "
                + org.getOrgID()
                + ","
                + "\"orgName\": \""
                + org.getOrgName()
                + "\","
                + "\"orgDescription\": \""
                + org.getOrgDescription()
                + "\","
                + "\"orgOwnerID\": "
                + org.getOrgOwnerID()
                + ","
                + "\"encodedImage\": \""
                + org.getEncodedImage()
                + "\","
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
