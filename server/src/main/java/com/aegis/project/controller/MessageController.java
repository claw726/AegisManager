package com.aegis.project.controller;

import com.aegis.project.dto.ChatMessagesDTO;
import com.aegis.project.dto.MessageDTO;
import com.aegis.project.service.MessageService;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/messages")
public class MessageController {

  private static final Logger logger = LoggerFactory.getLogger(
    MessageController.class
  );

  @Autowired
  private MessageService messageService;

    @PostMapping("/add")
    public ResponseEntity<?> addMessage(@RequestBody Map<String, Object> payload) {
        logger.info("Received message request with payload: {}", payload);
        try {
            int chatID = (Integer) payload.get("chatId");
            String content = (String) payload.get("content");
            logger.info("Processing message for chat {} with content: {}", chatID, content);
            messageService.addMessage(chatID, content);
            return ResponseEntity.ok("Message added successfully");
        } catch (Exception e) {
            if (e.getMessage().contains("Chat not found with id")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else if (e.getMessage().contains("User not found with email")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }
        }
    }

  @GetMapping("{chatId}/getMessages")
  public ResponseEntity<?> getMessages(@PathVariable int chatId) {
    logger.info("Attempting to fetch messages for chat ID: {}", chatId);
    try {
      List<MessageDTO> messages = messageService.getMessages(chatId);
      logger.info(
        "Successfully retrieved {} messages for chat ID: {}",
        messages.size(),
        chatId
      );
      return ResponseEntity.ok(messages);
    } catch (Exception e) {
      String errorMessage = e.getMessage() != null ? e.getMessage() : "An error occurred while fetching messages";
      if (errorMessage.contains("not found")) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
      } else if (errorMessage.contains("User not authorized")) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
          e.getMessage()
        );
      } else {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
          e.getMessage()
        );
      }
    }
  }

    @DeleteMapping("/{messageID}/delete")
    public ResponseEntity<?> deleteMessage(@PathVariable int messageID) {
        logger.info("Attempting to delete message with ID: {}", messageID);
        try {
            messageService.markDeleted(messageID);
            return ResponseEntity.ok("Message deleted successfully");
        } catch (Exception e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else if (e.getMessage().contains("User not authorized")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }
        }
    }

    @GetMapping("/history/{orgID}")
    public ResponseEntity<?> getOrgMessages(@PathVariable int orgID) {
        logger.info("Attempting to fetch messages for org ID: {}", orgID);
        try {
            List<ChatMessagesDTO> chatMessagesDTOs = messageService.getOrgMessages(orgID);
            logger.info("Successfully retrieved {} chats for org ID: {}", chatMessagesDTOs.size(), orgID);
            return ResponseEntity.ok(chatMessagesDTOs);
        } catch (Exception e) {
            String errorMessage = e.getMessage() != null ? e.getMessage() : "An error occurred while fetching messages";
            if (errorMessage.contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else if (errorMessage.contains("User not authorized")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }
        }
    }
}