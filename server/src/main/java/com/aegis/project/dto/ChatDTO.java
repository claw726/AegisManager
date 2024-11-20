package com.aegis.project.dto;

import com.aegis.project.model.ChatModel;
import com.aegis.project.model.MessageModel;

import java.time.LocalDateTime;
import java.util.Set;

public class ChatDTO {
    String id;
    String type;
    Set<Integer> participants;
    String title;
    MessageDTO lastMessage;
    LocalDateTime createdDate;

    public ChatDTO(String id, String type, Set<Integer> participants, String title, MessageDTO lastMessage, LocalDateTime createdDate) {
        this.id = id;
        this.type = type;
        this.participants = participants;
        this.title = title;
        this.lastMessage = lastMessage;
        this.createdDate = createdDate;
    }

    public ChatDTO(ChatModel chat) {
        this.id = String.format("%s-%d", chat.getType(), chat.getChatID());
        this.type = chat.getType();
        this.participants = chat.getParticipants();
        this.title = chat.getTitle();
        if (chat.getLastMessage() != null) {
            this.lastMessage = new MessageDTO(chat.getLastMessage());
        } else {
            this.lastMessage = null;
        }
        this.createdDate = chat.getCreatedDate();
    }

    public ChatDTO() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Set<Integer> getParticipants() {
        return participants;
    }

    public void setParticipants(Set<Integer> participants) {
        this.participants = participants;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public MessageDTO getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(MessageDTO lastMessage) {
        this.lastMessage = lastMessage;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
}
