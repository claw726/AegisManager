package com.aegis.project.service;

import com.aegis.project.dto.ChatDTO;
import com.aegis.project.model.ChatModel;
import com.aegis.project.model.UserModel;
import com.aegis.project.repository.ChatRepository;
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
public class ChatService {

    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private UserRepository userRepository;

    public ChatDTO createChat(String type, Set<Integer> participants, String title) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = ((UserDetails) authentication.getPrincipal()).getUsername();

        UserModel currentUser = userRepository.findByEmail(currentUsername).orElseThrow(() ->
                new RuntimeException("User not found with email: " + currentUsername));

        ChatModel chat = new ChatModel(type, title, participants);
        chat.addParticipant(currentUser.getUserID());
        chatRepository.save(chat);
        return new ChatDTO(chat);
    }

    public ChatDTO getChat(int chatID) {
        ChatModel chat = chatRepository.findById(chatID).orElseThrow(() ->
                new RuntimeException("Chat not found with id: " + chatID));
        return new ChatDTO(chat);
    }

    public Set<ChatDTO> findChatsByParticipant(int userID) {
        List<ChatModel> chats = chatRepository.findByParticipantsContaining(userID);
        if (chats.isEmpty()) {
            throw new RuntimeException("Chat not found with user id: " + userID);
        }
        return chats.stream().map(ChatDTO::new).collect(Collectors.toSet());
    }

    public void addParticipant(int chatID, int userID) {
        ChatModel chat = chatRepository.findById(chatID).orElseThrow(() ->
                new RuntimeException("Chat not found with id: " + chatID));
        chat.addParticipant(userID);
        chatRepository.save(chat);
    }

    public void removeParticipant(int chatID, int userID) {
        ChatModel chat = chatRepository.findById(chatID).orElseThrow(() ->
                new RuntimeException("Chat not found with id: " + chatID));
        chat.removeParticipant(userID);
        chatRepository.save(chat);
    }
}
