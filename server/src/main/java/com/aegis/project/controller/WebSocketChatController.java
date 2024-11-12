package com.aegis.project.controller;

import com.aegis.project.dto.ChatDTO;
import com.aegis.project.dto.MessageDTO;
import com.aegis.project.service.ChatService;
import com.aegis.project.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketChatController {

  @Autowired
  private ChatService chatService;

  @Autowired
  private MessageService messageService;

  @Autowired
  private SimpMessagingTemplate messagingTemplate;

  @MessageMapping("/chat.message")
  @SendTo("/topic/messages")
  public MessageDTO handleMessage(MessageDTO message) {
    return messageService.addMessage(
      message.getChatId(),
      message.getSenderId(),
      message.getContent()
    );
  }

  @MessageMapping("/chat.{roomId}")
  @SendTo("/topic/chat.{roomId}")
  public MessageDTO handleChatMessage(
    @DestinationVariable String roomId,
    MessageDTO message
  ) {
    return messageService.addMessage(
      Integer.parseInt(roomId),
      message.getSenderId(),
      message.getContent()
    );
  }

  @MessageMapping("/chat.create")
  public void handleChatCreation(ChatDTO chat) {
    ChatDTO newChat = chatService.createChat(
      chat.getType(),
      chat.getParticipants(),
      chat.getTitle()
    );

    // Notify all participants about the new chat
    for (Integer participantId : chat.getParticipants()) {
      messagingTemplate.convertAndSend(
        "/queue/user." + participantId + ".chats",
        newChat
      );
    }
  }
}
