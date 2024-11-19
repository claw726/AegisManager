package com.aegis.project.dto;

import com.aegis.project.model.OrgModel;

import java.util.HashSet;
import java.util.Set;

public class OrgDTO {

  private int orgID;
  private String orgName;
  private String orgDescription;
  private int orgOwnerID;
  private Set<UserDTO> users = new HashSet<>();
  private String encodedImage;
  private int chatID;

  public OrgDTO(
    int orgID,
    String orgName,
    String orgDescription,
    int orgOwnerID,
    String encodedImage,
    Set<UserDTO> users,
    int chatID
  ) {
    this.orgID = orgID;
    this.orgName = orgName;
    this.orgDescription = orgDescription;
    this.orgOwnerID = orgOwnerID;
    this.encodedImage = encodedImage;
    this.users = users;
    this.chatID = chatID;
  }

  public OrgDTO(OrgModel org ) {
    this.orgID = org.getOrgID();
    this.orgName = org.getOrgName();
    this.orgDescription = org.getOrgDescription();
    this.orgOwnerID = org.getOrgOwnerID();
    this.encodedImage = org.getEncodedImage();
    this.users = org
      .getUsers()
      .stream()
      .map(UserDTO::new)
      .collect(java.util.stream.Collectors.toSet());
    this.chatID = org.getChatID();
  }

  public OrgDTO() {}

  public int getOrgID() {
    return orgID;
  }

  public void setOrgID(int orgID) {
    this.orgID = orgID;
  }

  public String getOrgName() {
    return orgName;
  }

  public void setOrgName(String orgName) {
    this.orgName = orgName;
  }

  public String getOrgDescription() {
    return orgDescription;
  }

  public void setOrgDescription(String orgDescription) {
    this.orgDescription = orgDescription;
  }

  public int getOrgOwnerID() {
    return orgOwnerID;
  }

  public void setOrgOwnerID(int orgOwnerID) {
    this.orgOwnerID = orgOwnerID;
  }

  public Set<UserDTO> getUsers() {
    return users;
  }

  public void setUsers(Set<UserDTO> users) {
    this.users = users;
  }

  public String getEncodedImage() {
    return encodedImage;
  }

  public void setEncodedImage(String encodedImage) {
    this.encodedImage = encodedImage;
  }

  public int getChatID() {
    return chatID;
  }

  public void setChatID(int chatID) {
    this.chatID = chatID;
  }
}
