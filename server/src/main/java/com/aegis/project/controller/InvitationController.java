package com.aegis.project.controller;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aegis.project.dto.InvitationDTO;
import com.aegis.project.service.InvitationService;

@RestController
@RequestMapping("api/invitations")
public class InvitationController {

    private static final Logger logger = LoggerFactory.getLogger(
            InvitationController.class
    );

    @Autowired
    private InvitationService invitationService;

    @PostMapping("/createInvitation")
    public ResponseEntity<String> createInvitation(
            @RequestParam String senderEmail,
            @RequestParam String recipientEmail,
            @RequestParam int invitationType,
            @RequestParam String message
    ) {
        try {
            logger.info(
                    "Received invitation creation request from : {}, to: {}, message: {}",
                    senderEmail,
                    recipientEmail,
                    message
            );
            invitationService.createInvitation(
                    senderEmail,
                    recipientEmail,
                    invitationType,
                    message
            );
            logger.info("Invitation created successfully with message: {}", message);
            return ResponseEntity.ok("Invitation created successfully");
        } catch (RuntimeException e) {
            logger.error("Error creating invite: " + e.getMessage());
            if (e.getMessage().contains("Invitation with given message")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
            } else if (e.getMessage().contains("Cannot send an invitation to yourself")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            } else if (e.getMessage().contains("User not found with email:")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }
        }
    }

    @GetMapping("/{invitationID}")
    public ResponseEntity<InvitationDTO> getInvitation(
            @PathVariable int invitationID
    ) {
        try {
            return ResponseEntity.ok(invitationService.getInvitation(invitationID));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Invitation not found with ID: " + invitationID)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                        null
                );
            }
        }
    }

    @GetMapping("/getRecipientInvitations")
    public ResponseEntity<Set<InvitationDTO>> getRecipientInvitations(
            @RequestParam int userID
    ) {
        try {
            return ResponseEntity.ok(
                    invitationService.getRecipientInvitations(userID)
            );
        } catch (RuntimeException e) {
            if (e.getMessage().contains("User not found with email:")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                        null
                );
            }
        }
    }

    @PostMapping("/{invitationID}/accept")
    public ResponseEntity<Boolean> accept(@PathVariable int invitationID) {
        try {
            return ResponseEntity.ok(
                    invitationService.acceptInvitation(invitationID)
            );
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Invitation not found with ID: " + invitationID)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                        null
                );
            }
        }
    }

    @PostMapping("/{invitationID}/reject")
    public ResponseEntity<Boolean> reject(@PathVariable int invitationID) {
        try {
            return ResponseEntity.ok(
                    invitationService.rejectInvitation(invitationID)
            );
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Invitation not found with ID: " + invitationID)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                        null
                );
            }
        }
    }

    @DeleteMapping("/{invitationID}/deleteInvitation")
    public ResponseEntity<String> deleteInvitation(
            @PathVariable int invitationID
    ) {
        try {
            invitationService.deleteInvitation(invitationID);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(
                    "Invitation deleted successfully"
            );
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Invitation not found with id:")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else if (e.getMessage().contains("User not found with email")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else if (e.getMessage().contains("User does not have permission")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                        e.getMessage()
                );
            }
        }
    }
}
