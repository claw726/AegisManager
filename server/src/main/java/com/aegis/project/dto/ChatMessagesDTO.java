package com.aegis.project.dto;

import java.util.List;

public class ChatMessagesDTO {
    private ChatDTO chat;
    private List<MessageDTO> messages;

    public ChatMessagesDTO(ChatDTO chat, List<MessageDTO> messages) {
        this.chat = chat;
        this.messages = messages;
    }

    public ChatDTO getChat() {
        return chat;
    }

    public void setChat(ChatDTO chat) {
        this.chat = chat;
    }

    public List<MessageDTO> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageDTO> messages) {
        this.messages = messages;
    }
}
