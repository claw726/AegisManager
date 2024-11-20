package com.aegis.project.dto;

import com.aegis.project.model.MessageModel;
import java.util.Set;

public class MessageDTO {

  int id;
  String content;
  int senderID;
  String senderName;
  String timestamp;
  boolean isDeleted;
  Set<Integer> readBy;
  private String chatId;

  public MessageDTO(
    int id,
    String content,
    int senderID,
    String senderName,
    String timestamp,
    boolean isDeleted,
    Set<Integer> readBy
  ) {
    this.id = id;
    this.content = content;
    this.senderID = senderID;
    this.senderName = senderName;
    this.timestamp = timestamp;
    this.isDeleted = isDeleted;
    this.readBy = readBy;
  }

  public MessageDTO(MessageModel message) {
    this.id = message.getMessageID();
    this.content = message.getContent();
    this.senderID = message.getSenderID();
    this.senderName = message.getSenderName();
    this.timestamp = message.getTimestamp().toString();
    this.isDeleted = message.isDeleted();
    this.readBy = message.getReadBy();
    this.chatId = message.getChat().getType() + "-" + message.getChat().getChatID();
  }

  public MessageDTO() {}

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public int getSenderID() {
    return senderID;
  }

  public void setSenderID(int senderID) {
    this.senderID = senderID;
  }

  public String getSenderName() {
    return senderName;
  }

  public void setSenderName(String senderName) {
    this.senderName = senderName;
  }

  public String getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(String timestamp) {
    this.timestamp = timestamp;
  }

  public boolean isDeleted() {
    return isDeleted;
  }

  public void setDeleted(boolean deleted) {
    isDeleted = deleted;
  }

  public Set<Integer> getReadBy() {
    return readBy;
  }

  public void setReadBy(Set<Integer> readBy) {
    this.readBy = readBy;
  }

  public String getChatId() {
    return chatId;
  }

  public void setChatId(String chatId) {
    this.chatId = chatId;
  }
}