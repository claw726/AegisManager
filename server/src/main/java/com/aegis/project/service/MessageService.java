package com.aegis.project.service;

import com.aegis.project.controller.SocketIOController;
import com.aegis.project.dto.MessageDTO;
import com.aegis.project.exception.UserNotFoundException;
import com.aegis.project.model.ChatModel;
import com.aegis.project.model.MessageModel;
import com.aegis.project.model.SocketMessageModel;
import com.aegis.project.model.UserModel;
import com.aegis.project.repository.ChatRepository;
import com.aegis.project.repository.MessageRepository;
import com.aegis.project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

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
    private SocketIOController socketIOController;

    public MessageDTO addMessage(int chatID, String content) {
        ChatModel chat = chatRepository.findById(chatID).orElseThrow(() ->
                new RuntimeException("Chat not found with id: " + chatID));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = ((UserDetails) authentication.getPrincipal()).getUsername();

        UserModel currentUser = userRepository.findByEmail(currentUsername).orElseThrow(() ->
                new RuntimeException("User not found with email: " + currentUsername));

        MessageModel message = new MessageModel(chat, currentUser.getUserID(), currentUser.getUserName(), content);
        message.markAsRead(currentUser.getUserID());
        messageRepository.save(message);

        Set<Integer> participants = chat.getParticipants();

        for (int participant : participants) {
            UserModel user = userRepository.findById(participant).orElseThrow(() ->
                    new RuntimeException("User not found with id: " + participant));

            if (socketIOController.isUserConnected(user.getEmail()) && participant != currentUser.getUserID()) {
                socketIOController.sendMessage(new SocketMessageModel(currentUsername, user.getEmail(), "message-" + chatID, content));
                message.markAsRead(participant);
                messageRepository.save(message);
            }
        }

        return new MessageDTO(message);
    }

    public List<MessageDTO> getMessages(int chatID) {

        ChatModel chat = chatRepository.findById(chatID).orElseThrow(() ->
                new RuntimeException("Chat not found with id: " + chatID));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = ((UserDetails) authentication.getPrincipal()).getUsername();

        List<MessageModel> messages = messageRepository.findByChat_ChatIDOrderByTimestamp(chatID);

        UserModel currentUser = userRepository.findByEmail(currentUsername).orElseThrow(() ->
                new UserNotFoundException("User not found with email {}" + currentUsername));

        // Check authorization before proceeding
        if (!chat.getParticipants().contains(currentUser.getUserID())) {
            throw new RuntimeException("User not authorized to view messages in chat: " + chatID);
        }

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
    }


}