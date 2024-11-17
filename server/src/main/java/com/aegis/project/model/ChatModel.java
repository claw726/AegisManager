package com.aegis.project.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "chats")
public class ChatModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int chatID;

    @Column(name = "type")
    private String type;

    @Column(name = "title")
    private String title;

    @ElementCollection
    @CollectionTable(name = "chat_members", joinColumns = @JoinColumn(name = "chat_id"))
    @Column(name = "user_id")
    private Set<Integer> participants = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "unread_count_per_user", joinColumns = @JoinColumn(name = "chat_id"))
    private Map<Integer, Integer> unreadCountPerUser = new HashMap<>();

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL)
    private List<MessageModel> messages = new ArrayList<>();

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "last_message")
    private String lastMessage;

    public ChatModel() {
    }

    public ChatModel(String type, String title, Set<Integer> participants) {
        this.type = type;
        this.title = title;
        this.participants = participants;
        this.createdDate = LocalDateTime.now();
        this.lastMessage = "";
    }

    public void addParticipant(int userID) {
        participants.add(userID);
    }

    public void removeParticipant(int userID) {
        participants.remove(userID);
    }

    public void resetUnreadCount(int userID) {
        unreadCountPerUser.put(userID, 0);
    }

    public void incrementUnreadCount(int userID) {
        unreadCountPerUser.put(userID, unreadCountPerUser.getOrDefault(userID, 0) + 1);
    }

    public int getChatID() {
        return chatID;
    }

    public void setChatID(int chatID) {
        this.chatID = chatID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Set<Integer> getParticipants() {
        return participants;
    }

    public void setParticipants(Set<Integer> participants) {
        this.participants = participants;
    }

    public Map<Integer, Integer> getUnreadCountPerUser() {
        return unreadCountPerUser;
    }

    public void setUnreadCountPerUser(Map<Integer, Integer> unreadCountPerUser) {
        this.unreadCountPerUser = unreadCountPerUser;
    }

    public List<MessageModel> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageModel> messages) {
        this.messages = messages;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public void addMessage(MessageModel message) {
        lastMessage = message.getContent();
        messages.addLast(message);
    }
}
