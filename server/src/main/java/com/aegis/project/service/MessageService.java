package com.aegis.project.service;

import com.aegis.project.controller.SocketIOController;
import com.aegis.project.dto.ChatDTO;
import com.aegis.project.dto.ChatMessagesDTO;
import com.aegis.project.dto.MessageDTO;
import com.aegis.project.exception.UserNotFoundException;
import com.aegis.project.model.*;
import com.aegis.project.repository.ChatRepository;
import com.aegis.project.repository.MessageRepository;
import com.aegis.project.repository.OrgRepository;
import com.aegis.project.repository.UserRepository;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MessageService {
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrgRepository orgRepository;

    @Autowired
    private SocketIOController socketIOController;

    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);

    public MessageDTO addMessage(int chatID, String content) {
        try {
            // 1. Get chat and verify it exists
            ChatModel chat = chatRepository.findById(chatID).orElseThrow(() ->
                    new RuntimeException("Chat not found with id: " + chatID));

            // 2. Get current user and verify they exist
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUsername = ((UserDetails) authentication.getPrincipal()).getUsername();
            UserModel currentUser = userRepository.findByEmail(currentUsername).orElseThrow(() ->
                    new RuntimeException("User not found with email: " + currentUsername));

            // 3. Verify user is a participant
            if (!chat.getParticipants().contains(currentUser.getUserID())) {
                throw new RuntimeException("User not authorized to add messages to chat: " + chatID);
            }

            // 4. Create and save message
            MessageModel message = new MessageModel(chat, currentUser.getUserID(), currentUser.getUserName(), content);
            message.markAsRead(currentUser.getUserID());
            messageRepository.save(message);

            chat.setLastMessage(message);
            chatRepository.save(chat);

            // 5. Notify other participants
            Set<Integer> participants = chat.getParticipants();
            for (int participant : participants) {
                if (participant == currentUser.getUserID()) continue; // Skip current user

                UserModel user = userRepository.findById(participant).orElseThrow(() ->
                        new RuntimeException("User not found with id: " + participant));

                if (socketIOController.isUserConnected(user.getEmail())) {
                    socketIOController.sendMessageWithDTO(new SocketMessageModel(
                        currentUsername,
                        user.getEmail(),
                        "message-" + chatID,
                        content),
                        new MessageDTO(message));
                }
            }

            return new MessageDTO(message);
        } catch (Exception e) {
            logger.error("Error adding message to chat {}: {}", chatID, e.getMessage());
            throw new RuntimeException("Error adding message to chat: " + e.getMessage(), e);
        }
    }

    public List<MessageDTO> getMessages(int chatID) {
        try {
            // Verify chat exists
            ChatModel chat = chatRepository.findById(chatID).orElseThrow(() ->
                    new RuntimeException("Chat not found with id: " + chatID));

            // Get current user and verify they exist
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUsername = ((UserDetails) authentication.getPrincipal()).getUsername();

            UserModel currentUser = userRepository.findByEmail(currentUsername).orElseThrow(() ->
                    new UserNotFoundException("User not found with email {}" + currentUsername));


            // Check authorization before proceeding
            if (!chat.getParticipants().contains(currentUser.getUserID())) {
                throw new RuntimeException("User not authorized to view messages in chat: " + chatID);
            }

            // Fetch and process messages
            List<MessageModel> messages = messageRepository.findByChat_ChatIDOrderByTimestamp(chatID);

            // Mark any existing messages as read
            if (!messages.isEmpty()) {
                for (MessageModel message : messages) {
                    message.markAsRead(currentUser.getUserID());
                    messageRepository.save(message);
                }
            }

            // Return empty list if no messages, otherwise return DTOs
            return messages.stream()
                    .map(MessageDTO::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching messages for chat {}: {}", chatID, e.getMessage());
            throw new RuntimeException("Error fetching messages for chat: " + e.getMessage(), e);
        }
    }

    public void markDeleted(int messageID) {
        MessageModel message = messageRepository.findById(messageID).orElseThrow(() ->
                new RuntimeException("Message not found with id: " + messageID));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = ((UserDetails) authentication.getPrincipal()).getUsername();

        UserModel currentUser = userRepository.findByEmail(currentUsername).orElseThrow(() ->
                new RuntimeException("User not found with email: " + currentUsername));

        if (message.getSenderID() != currentUser.getUserID()) {
            throw new RuntimeException("User not authorized to delete message with id: " + messageID);
        }

        message.setDeleted(true);
        messageRepository.save(message);

        ChatModel chat = message.getChat();
        if (chat.getLastMessage().equals(message.getContent())) {
            List<MessageModel> messages = messageRepository.findByChat_ChatIDOrderByTimestamp(chat.getChatID());
            if (!messages.isEmpty()) {
                chat.setLastMessage(messages.getLast());
            } else {
                MessageModel lastMessage = new MessageModel(chat, message.getSenderID(), message.getSenderName(), "This message has been deleted");
                chat.setLastMessage(lastMessage);
            }
            chatRepository.save(chat);
        }

        Set<Integer> participants = chat.getParticipants();
        for (int participant : participants) {
            if (participant == currentUser.getUserID()) continue; // Skip current user

            UserModel user = userRepository.findById(participant).orElseThrow(() ->
                    new RuntimeException("User not found with id: " + participant));

            if (socketIOController.isUserConnected(user.getEmail())) {
                socketIOController.sendMessageWithDTO(new SocketMessageModel(
                        currentUsername,
                        user.getEmail(),
                        "message-" + chat.getChatID(),
                        "Message with ID " + messageID + " has been deleted"),
                        new MessageDTO(message));
            }
        }

        logger.info("Message with ID {} has been deleted", messageID);
    }

    public List<ChatMessagesDTO> getOrgMessages(int orgID) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = ((UserDetails) authentication.getPrincipal()).getUsername();

        UserModel currentUser = userRepository.findByEmail(currentUsername).orElseThrow(() ->
                new RuntimeException("User not found with email: " + currentUsername));

        OrgModel org = orgRepository.findById(orgID).orElseThrow(() ->
                new RuntimeException("Organization not found with id: " + orgID));

        if (currentUser.getUserID() != org.getOrgOwnerID()) {
            throw new RuntimeException("User not authorized to view messages in organization: " + orgID);
        }

        Set<UserModel> users = org.getUsers();
        List<ChatMessagesDTO> chatMessagesDTOs = new ArrayList<>();

        for (UserModel user : users) {
            List<ChatModel> chats = chatRepository.findByParticipantsContaining(user.getUserID());
            for (ChatModel chat : chats) {
                boolean allParticipantsInOrg = true;
                for (int participant : chat.getParticipants()) {
                    if (!users.contains(userRepository.findById(participant).orElseThrow(() ->
                            new RuntimeException("User not found with id: " + participant)))) {
                        allParticipantsInOrg = false;
                        break;
                    }
                }
                if (!allParticipantsInOrg) {
                    continue;
                }
                List<MessageModel> messages = chat.getMessages();
                List<MessageDTO> messageDTOs = messages.stream()
                        .map(MessageDTO::new)
                        .collect(Collectors.toList());
                ChatDTO chatDTO = new ChatDTO(chat);
                ChatMessagesDTO chatMessagesDTO = new ChatMessagesDTO(chatDTO, messageDTOs);
                boolean chatExists = false;
                for (ChatMessagesDTO chatMessagesDTO1 : chatMessagesDTOs) {
                    if (chatMessagesDTO1.getChat().getId().equals(chatDTO.getId())) {
                        chatExists = true;
                        break;
                    }
                }
                if (!chatExists) {
                    chatMessagesDTOs.add(chatMessagesDTO);
                }
            }
        }

        return chatMessagesDTOs;
    }
}