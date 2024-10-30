package com.aegis.project.dto;

public class InvitationDTO {

    private int invitationID;
    private int senderID;
    private String senderEmail;
    private int recipientID;
    private String recipientEmail;
    private String message;
    //TODO: make invitation type an enum?
    private int invitationType;

    public InvitationDTO(int invitationID, int senderID, String senderEmail, int recipientID, String recipientEmail, int invitationType, String message) {
        this.invitationID = invitationID;
        this.senderID = senderID;
        this.senderEmail = senderEmail;
        this.recipientID = recipientID;
        this.recipientEmail = recipientEmail;
        this.invitationType = invitationType;
        this.message = message;
    }

    public int getInvitationID() {
        return this.invitationID;
    }

    public void setInvitationID(int invitationID) {
        this.invitationID = invitationID;
    }

    public int getSenderID() {
        return this.senderID;
    }

    public void setSenderID(int senderID) {
        this.senderID = senderID;
    }

    public String getSenderEmail() {
        return this.senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public int getRecipientID() {
        return this.recipientID;
    }

    public void setRecipientID(int recipientID) {
        this.recipientID = recipientID;
    }

    public String getRecipientEmail() {
        return this.recipientEmail;
    }

    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getInvitationType() {
        return this.invitationType;
    }

    public void setInvitationType(int invitationType) {
        this.invitationType = invitationType;
    }

}
