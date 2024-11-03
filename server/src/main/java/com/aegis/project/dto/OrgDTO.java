package com.aegis.project.dto;

import java.util.HashSet;
import java.util.Set;

public class OrgDTO {

  private int orgID;
  private String orgName;
  private String orgDescription;
  private int orgOwnerID;
  private Set<UserDTO> users = new HashSet<>();
  private String encodedImage;

  public OrgDTO(
    int orgID,
    String orgName,
    String orgDescription,
    int orgOwnerID,
    String encodedImage,
    Set<UserDTO> users
  ) {
    this.orgID = orgID;
    this.orgName = orgName;
    this.orgDescription = orgDescription;
    this.orgOwnerID = orgOwnerID;
    this.encodedImage = encodedImage;
    this.users = users;
    //this.users = orgService.getOrgMembers(orgID);
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
}
