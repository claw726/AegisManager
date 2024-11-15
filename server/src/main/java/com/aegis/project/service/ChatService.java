package com.aegis.project.service;

import com.aegis.project.dto.ChatDTO;
import com.aegis.project.dto.UserDTO;
import com.aegis.project.model.ChatModel;
import com.aegis.project.model.OrgModel;
import com.aegis.project.model.UserModel;
import com.aegis.project.repository.ChatRepository;
import com.aegis.project.repository.UserRepository;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

  private static final Logger logger = LoggerFactory.getLogger(
    ChatService.class
  );

  @Autowired
  private ChatRepository chatRepository;

  @Autowired
  private UserRepository userRepository;

    public void createChat(String type, Set<Integer> participants, String title) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = ((UserDetails) authentication.getPrincipal()).getUsername();

        UserModel currentUser = userRepository.findByEmail(currentUsername).orElseThrow(() ->
                new RuntimeException("User not found with email: " + currentUsername));

        ChatModel chat = new ChatModel(type, title, participants);
        chatRepository.save(chat);
    }

  public ChatDTO getChat(int chatID) {
    Authentication authentication = SecurityContextHolder.getContext()
      .getAuthentication();
    String currentUsername =
      ((UserDetails) authentication.getPrincipal()).getUsername();

    UserModel currentUser = userRepository
      .findByEmail(currentUsername)
      .orElseThrow(() ->
        new RuntimeException("User not found with email: " + currentUsername)
      );

    ChatModel chat = chatRepository
      .findById(chatID)
      .orElseThrow(() ->
        new RuntimeException("Chat not found with id: " + chatID)
      );

    if (!chat.getParticipants().contains(currentUser.getUserID())) {
        throw new RuntimeException("User not found in chat");
    }
    return new ChatDTO(chat);
  }

  public List<ChatDTO> findChatsByParticipant(int userID) {
    List<ChatModel> chats = chatRepository.findByParticipantsContaining(userID);
    if (chats.isEmpty()) {
      logger.info("No chats found for user with id: " + userID);
      return Collections.emptyList();
    }

    // Sort chats, handling the case where there might be no messages
    chats.sort((c1, c2) -> {
        List<?> messages1 = c1.getMessages();
        List<?> messages2 = c2.getMessages();

        // If either chat has no messages, handle accordingly
        if (messages1.isEmpty() && messages2.isEmpty()) return 0;
        if (messages1.isEmpty()) return 1;  // Put chats with no messages last
        if (messages2.isEmpty()) return -1;

        // Both chats have messages, compare their timestamps
        return c2.getMessages().getLast().getTimestamp()
            .compareTo(c1.getMessages().getLast().getTimestamp());
    });
    return chats.stream().map(ChatDTO::new).collect(Collectors.toList());
  }

  public void addParticipant(int chatID, int userID) {
    ChatModel chat = chatRepository
      .findById(chatID)
      .orElseThrow(() ->
        new RuntimeException("Chat not found with id: " + chatID)
      );

    userRepository
      .findById(userID)
      .orElseThrow(() ->
        new RuntimeException("User not found with id: " + userID)
      );
    chat.addParticipant(userID);
    chatRepository.save(chat);
  }

  public void removeParticipant(int chatID, int userID) {
    ChatModel chat = chatRepository
      .findById(chatID)
      .orElseThrow(() ->
        new RuntimeException("Chat not found with id: " + chatID)
      );

    userRepository
      .findById(userID)
      .orElseThrow(() ->
        new RuntimeException("User not found with id: " + userID)
      );

        if (!chat.getParticipants().contains(userID)) {
            throw new RuntimeException("User not found in chat");
        }

        chat.removeParticipant(userID);
        chatRepository.save(chat);
    }

    public Set<UserDTO> getMessageableUsers() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = ((UserDetails) authentication.getPrincipal()).getUsername();

        UserModel currentUser = userRepository.findByEmail(currentUsername).orElseThrow(() ->
                new RuntimeException("User not found with email: " + currentUsername));

        List<UserModel> users = new java.util.ArrayList<>(List.of());

        Set<OrgModel> orgs = currentUser.getOrgs();

        for (OrgModel org : orgs) {
            Set<UserModel> orgUsers = org.getUsers();
            for (UserModel user : orgUsers) {
                if (user.getUserID() != currentUser.getUserID() && !users.contains(user)) {
                    users.add(user);
                }
            }
        }

        return users.stream().map(UserDTO::new).collect(Collectors.toSet());
    }
}