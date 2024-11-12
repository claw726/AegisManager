package com.aegis.project.service;

import com.aegis.project.dto.MessageDTO;
import com.aegis.project.model.ChatModel;
import com.aegis.project.model.MessageModel;
import com.aegis.project.model.UserModel;
import com.aegis.project.repository.ChatRepository;
import com.aegis.project.repository.MessageRepository;
import com.aegis.project.repository.UserRepository;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

  @Autowired
  private MessageRepository messageRepository;

  @Autowired
  private ChatRepository chatRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private SimpMessagingTemplate messagingTemplate;

  public MessageDTO addMessage(int chatId, int senderId, String content) {
    ChatModel chat = chatRepository
      .findById(chatId)
      .orElseThrow(() ->
        new RuntimeException("Chat not found with id: " + chatId)
      );

    MessageModel message = new MessageModel(
      chat,
      senderId,
      getUserName(senderId),
      content
    );
    messageRepository.save(message);

    // Update last message in chat
    chat.setLastMessage(content);
    chatRepository.save(chat);

    // Notify all participants about the new message
    MessageDTO messageDTO = new MessageDTO(message);
    for (Integer participantId : chat.getParticipants()) {
      if (participantId != senderId) {
        messagingTemplate.convertAndSend(
          "/queue/user." + participantId + ".messages",
          messageDTO
        );
      }
    }

    return messageDTO;
  }

  public List<MessageDTO> getMessages(String chatID) {
    int numericChatID;
    try {
      numericChatID = Integer.parseInt(chatID.replaceAll("\\D+", ""));
    } catch (NumberFormatException e) {
      throw new RuntimeException("Invalid chat id: " + chatID);
    }
    List<MessageModel> messages =
      messageRepository.findByChat_ChatIDOrderByTimestamp(numericChatID);
    if (messages.isEmpty()) {
      throw new RuntimeException("Messages not found with chat id: " + chatID);
    }

    Authentication authentication = SecurityContextHolder.getContext()
      .getAuthentication();
    String currentUsername =
      ((UserDetails) authentication.getPrincipal()).getUsername();

    ChatModel chat = chatRepository
      .findById(numericChatID)
      .orElseThrow(() ->
        new RuntimeException("Chat not found with id: " + chatID)
      );

    UserModel currentUser = userRepository
      .findByEmail(currentUsername)
      .orElseThrow(() ->
        new RuntimeException("User not found with email: " + currentUsername)
      );

    if (!chat.getParticipants().contains(currentUser.getUserID())) {
      throw new RuntimeException(
        "User not authorized to view messages in chat: " + chatID
      );
    }

    for (MessageModel message : messages) {
      if (message.getSenderID() != currentUser.getUserID()) {
        message.markAsRead(currentUser.getUserID());
        messageRepository.save(message);
      }
    }

    return messages.stream().map(MessageDTO::new).collect(Collectors.toList());
  }

  private String getUserName(int userId) {
    return userRepository
      .findById(userId)
      .map(UserModel::getUserName)
      .orElse("Unknown User");
  }
}
