package com.aegis.project.service;

import com.aegis.project.dto.ChatDTO;
import com.aegis.project.model.ChatModel;
import com.aegis.project.model.UserModel;
import com.aegis.project.repository.ChatRepository;
import com.aegis.project.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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

  @Autowired
  private SimpMessagingTemplate messagingTemplate;

  public ChatDTO createChat(
    String type,
    Set<Integer> participants,
    String title
  ) {
    ChatModel chat = new ChatModel(type, title, participants);
    chat = chatRepository.save(chat);

    ChatDTO chatDTO = new ChatDTO(chat);

    // Notify all participants about the new chat
    for (Integer participantId : participants) {
      messagingTemplate.convertAndSend(
        "/queue/user." + participantId + ".chats",
        chatDTO
      );
    }

    return chatDTO;
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

    for (int participant : chat.getParticipants()) {
      if (participant != currentUser.getUserID()) {
        chat.addParticipant(participant);
      }
    }
    return new ChatDTO(chat);
  }

  public List<ChatDTO> findChatsByParticipant(int userId) {
    logger.debug("Finding chats for userID: {}", userId);
    List<ChatModel> userChats = chatRepository.findByParticipantsContaining(
      userId
    );

    // Add more detailed logging
    logger.debug("Found {} chats for user ID: {}", userChats.size(), userId);
    if (userChats.isEmpty()) {
      logger.info(
        "No chats found for user ID: {}. Returning empty list.",
        userId
      );
      return new ArrayList<>();
    }
    return userChats.stream().map(ChatDTO::new).collect(Collectors.toList());
  }

  public void addParticipant(int chatID, int userID) {
    ChatModel chat = chatRepository
      .findById(chatID)
      .orElseThrow(() ->
        new RuntimeException("Chat not found with id: " + chatID)
      );

    UserModel user = userRepository
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

    UserModel user = userRepository
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
}
