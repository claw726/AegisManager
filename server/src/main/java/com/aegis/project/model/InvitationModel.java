package com.aegis.project.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "invitations")
public class InvitationModel {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int InvitationID;

  @Column(name = "sender_ID")
  private int SenderID;

  @Column(name = "sender_email")
  private String SenderEmail;

  @Column(name = "recipient_ID")
  private int RecipientID;

  @Column(name = "recipient_email")
  private String RecipientEmail;

  @Column(name = "message")
  private String Message;

  @Column(name = "invitation_type")
  private int invitationType;

  public int getInvitationID() {
    return this.InvitationID;
  }

  public void setInvitationID(int InvitationID) {
    this.InvitationID = InvitationID;
  }

  public int getSenderID() {
    return this.SenderID;
  }

  public void setSenderID(int SenderID) {
    this.SenderID = SenderID;
  }

  public String getSenderEmail() {
    return this.SenderEmail;
  }

  public void setSenderEmail(String SenderEmail) {
    this.SenderEmail = SenderEmail;
  }

  public int getRecipientID() {
    return this.RecipientID;
  }

  public void setRecipientID(int RecipientID) {
    this.RecipientID = RecipientID;
  }

  public String getRecipientEmail() {
    return this.RecipientEmail;
  }

  public void setRecipientEmail(String RecipientEmail) {
    this.RecipientEmail = RecipientEmail;
  }

  public String getMessage() {
    return this.Message;
  }

  public void setMessage(String Message) {
    this.Message = Message;
  }

  public int getInvitationType() {
    return this.invitationType;
  }

  public void setInvitationType(int invitationType) {
    this.invitationType = invitationType;
  }
}
