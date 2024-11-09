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

    public MessageDTO(int id, String content, int senderID, String senderName, String timestamp, boolean isDeleted, Set<Integer> readBy) {
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
    }

    public MessageDTO() {
    }

}
