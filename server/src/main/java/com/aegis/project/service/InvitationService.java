package com.aegis.project.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aegis.project.dto.InvitationDTO;
import com.aegis.project.dto.ProjectDTO;
import com.aegis.project.dto.TaskDTO;
import com.aegis.project.model.InvitationModel;
import com.aegis.project.model.OrgModel;
import com.aegis.project.model.UserModel;
import com.aegis.project.repository.InvitationRepository;
import com.aegis.project.repository.UserRepository;

@Service
public class InvitationService {

    @Autowired
    private InvitationRepository invitationRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private TaskService taskService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private OrgService orgService;

    @Autowired
    private UserRepository userRepository;

    public boolean createInvitation(
            String senderEmail,
            String recipientEmail,
            int invitationType,
            String message
    ) {
        if (invitationType >= 2 && invitationType <= 4 && invitationRepository.existsInvitationByMessageAndRecipient(message, recipientEmail)) {
            throw new RuntimeException(
                    "Invitation with given message has already been sent to the user"
            );
        }
        if (invitationType == 1 && invitationRepository.existsInvitationByMessage(message)) {
            throw new RuntimeException(
                    "Invitation with given message has already been sent"
            );
        }
        InvitationModel invite = new InvitationModel();

        UserModel sender = userService
                .findUserByEmail(senderEmail)
                .orElseThrow(()
                        -> new RuntimeException("User not found with email: " + senderEmail)
                );
        UserModel recipient = userService
                .findUserByEmail(recipientEmail)
                .orElseThrow(()
                        -> new RuntimeException("User not found with email: " + recipientEmail)
                );

        invite.setMessage(message);
        invite.setSenderID(sender.getUserID());
        invite.setSenderEmail(senderEmail);
        invite.setRecipientID(recipient.getUserID());
        invite.setRecipientEmail(recipientEmail);
        invite.setInvitationType(invitationType);

        invitationRepository.save(invite);
        return true;
    }

    public InvitationDTO getInvitation(int invitationID) {
        InvitationModel invitation = invitationRepository
                .findById(invitationID)
                .orElseThrow(()
                        -> new RuntimeException("Invitation not found with ID: " + invitationID)
                );
        InvitationDTO invitationDTO = new InvitationDTO(
                invitation.getInvitationID(),
                invitation.getSenderID(),
                invitation.getSenderEmail(),
                invitation.getRecipientID(),
                invitation.getRecipientEmail(),
                invitation.getInvitationType(),
                invitation.getMessage()
        );
        return invitationDTO;
    }

    public void deleteInvitation(int invitationID) {
        invitationRepository
                .findById(invitationID)
                .orElseThrow(()
                        -> new RuntimeException("Invitation not found with id: " + invitationID)
                );
        invitationRepository.deleteById(invitationID);
    }

    public boolean acceptInvitation(int invitationID) {
        InvitationModel invitation = invitationRepository
                .findById(invitationID)
                .orElseThrow(()
                        -> new RuntimeException("Invitation not found with id: " + invitationID)
                );

        //If it is a task notification invite, then do the task reassignment
        if (invitation.getInvitationType() == 1) {
            String message = invitation.getMessage();
            int taskID = Integer.parseInt(message.substring(0, message.indexOf(":")));
            TaskDTO task = taskService.getTask(taskID);
            taskService.directlySwitchTaskAssigner(
                    taskID,
                    invitation.getRecipientEmail()
            );
            createInvitation(invitation.getRecipientEmail(), invitation.getSenderEmail(), 0, invitation.getRecipientEmail() + " has accepted your request to become the task assigner of " + task.getTaskName());
        } //Invitation to add a user to an organization
        else if (invitation.getInvitationType() == 2) {
            String message = invitation.getMessage();
            int orgID = Integer.parseInt(message.substring(0, message.indexOf(":")));
            OrgModel org = orgService.directlyGetOrg(orgID);
            orgService.directlyAddUser(orgID, invitation.getRecipientEmail());
            createInvitation(invitation.getRecipientEmail(), invitation.getSenderEmail(), 0, invitation.getRecipientEmail() + " has accepted your request to join the organization " + org.getOrgName());

        } //Invitation to add a user to a project
        else if (invitation.getInvitationType() == 3) {
            String message = invitation.getMessage();
            int projectID = Integer.parseInt(message.substring(0, message.indexOf(":")));
            ProjectDTO project = projectService.directlyGetProject(projectID);
            projectService.directlyAddUser(projectID, invitation.getRecipientEmail());
            createInvitation(invitation.getRecipientEmail(), invitation.getSenderEmail(), 0, invitation.getRecipientEmail() + " has accepted your request to join the project " + project.getProjectName());

        } //Invitation to add a user to a task
        else if (invitation.getInvitationType() == 4) {
            String message = invitation.getMessage();
            int taskID = Integer.parseInt(message.substring(0, message.indexOf(":")));
            TaskDTO task = taskService.getTask(taskID);
            taskService.directlyAddUser(taskID, invitation.getRecipientEmail());
            createInvitation(invitation.getRecipientEmail(), invitation.getSenderEmail(), 0, invitation.getRecipientEmail() + " has accepted your request to join the task " + task.getTaskName());
        }
        deleteInvitation(invitationID);
        return true;
    }

    public boolean rejectInvitation(int invitationID) {
        InvitationModel invitation = invitationRepository
                .findById(invitationID)
                .orElseThrow(()
                        -> new RuntimeException("Invitation not found with id: " + invitationID)
                );
        if (invitation.getInvitationType() == 1) {
            String message = invitation.getMessage();
            int taskID = Integer.parseInt(message.substring(0, message.indexOf(":")));
            TaskDTO task = taskService.getTask(taskID);
            createInvitation(invitation.getRecipientEmail(), invitation.getSenderEmail(), 0, invitation.getRecipientEmail() + " has rejected your request to become the task assigner of " + task.getTaskName());
        } else if (invitation.getInvitationType() == 2) {
            String message = invitation.getMessage();
            int orgID = Integer.parseInt(message.substring(0, message.indexOf(":")));
            OrgModel org = orgService.directlyGetOrg(orgID);
            createInvitation(invitation.getRecipientEmail(), invitation.getSenderEmail(), 0, invitation.getRecipientEmail() + " has rejected your request to join the organization " + org.getOrgName());

        } //Invitation to add a user to a project
        else if (invitation.getInvitationType() == 3) {
            String message = invitation.getMessage();
            int projectID = Integer.parseInt(message.substring(0, message.indexOf(":")));
            ProjectDTO project = projectService.directlyGetProject(projectID);
            createInvitation(invitation.getRecipientEmail(), invitation.getSenderEmail(), 0, invitation.getRecipientEmail() + " has rejected your request to join the project " + project.getProjectName());

        } //Invitation to add a user to a task
        else if (invitation.getInvitationType() == 4) {
            String message = invitation.getMessage();
            int taskID = Integer.parseInt(message.substring(0, message.indexOf(":")));
            TaskDTO task = taskService.getTask(taskID);
            createInvitation(invitation.getRecipientEmail(), invitation.getSenderEmail(), 0, invitation.getRecipientEmail() + " has rejected your request to join the task " + task.getTaskName());
        }

        deleteInvitation(invitationID);
        return true;
    }

    public Set<InvitationDTO> getRecipientInvitations(int userID) {
        userRepository
                .findById(userID)
                .orElseThrow(()
                        -> new RuntimeException("User not found with ID: " + userID)
                );
        List<InvitationModel> invitations
                = invitationRepository.getRecipientInvitations(userID);

        return invitations
                .stream()
                .map(invitation
                        -> new InvitationDTO(
                        invitation.getInvitationID(),
                        invitation.getSenderID(),
                        invitation.getSenderEmail(),
                        invitation.getRecipientID(),
                        invitation.getRecipientEmail(),
                        invitation.getInvitationType(),
                        invitation.getMessage()
                )
                )
                .collect(Collectors.toSet());
    }
}
