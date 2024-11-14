package com.aegis.project.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
public class MessageModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private int messageID;

    @ManyToOne
    @JoinColumn(name = "chat_id")
    private ChatModel chat;

    @Column(name = "sender_id")
    private int senderID;

    @Column(name = "sender_name")
    private String senderName;

    @Column(name = "content")
    private String content;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @ElementCollection
    @CollectionTable(name = "read_by", joinColumns = @JoinColumn(name = "message_id"))
    @Column(name = "user_id")
    private Set<Integer> readBy = new HashSet<>();

    public MessageModel() {
    }

    public MessageModel(ChatModel chat, int senderID, String senderName, String content) {
        this.chat = chat;
        this.senderID = senderID;
        this.senderName = senderName;
        this.content = content;
        this.timestamp = LocalDateTime.now();
        this.isDeleted = false;
    }

    public void markAsRead(int userID) {
        readBy.add(userID);
    }

    public int getMessageID() {
        return messageID;
    }

    public void setMessageID(int messageID) {
        this.messageID = messageID;
    }

    public ChatModel getChat() {
        return chat;
    }

    public void setChat(ChatModel chat) {
        this.chat = chat;
    }

    public int getSenderID() {
        return senderID;
    }

    public void setSenderID(int senderID) {
        this.senderID = senderID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
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

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
}