package com.aegis.project.controller;

import com.aegis.project.dto.ChatDTO;
import com.aegis.project.service.ChatService;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("api/chats")
public class ChatController {

  private static final Logger logger = LoggerFactory.getLogger(
    ChatController.class
  );

  @Autowired
  private ChatService chatService;

  @PostMapping("/create")
  public ResponseEntity<String> createChat(
    @RequestParam String type,
    @RequestParam Set<Integer> participants,
    @RequestParam String title
  ) {
    logger.info(
      "Attempting to create chat - Type: {}, Participants: {}, Title: {}",
      type,
      participants,
      title
    );

    try {
      chatService.createChat(type, participants, title);
      logger.info(
        "Chat created successfully - Type: {}, Title: {}",
        type,
        title
      );
      return ResponseEntity.ok("Chat created successfully");
    } catch (Exception e) {
      logger.error(
        "Error creating chat - Type: {}, Title: {}, Error: {}",
        type,
        title,
        e.getMessage(),
        e
      );

      if (e.getMessage().contains("User not found with email")) {
        logger.warn(
          "User not found while creating chat - Error: {}",
          e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
      }
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
        e.getMessage()
      );
    }
  }

  @GetMapping("{chatID}/get")
  public ResponseEntity<?> getChat(@PathVariable int chatID) {
    logger.info("Attempting to fetch chat with ID: {}", chatID);

    try {
      Object chat = chatService.getChat(chatID);
      logger.info("Successfully retrieved chat - ID: {}", chatID);
      return ResponseEntity.ok(chat);
    } catch (Exception e) {
      logger.error(
        "Error fetching chat - ID: {}, Error: {}",
        chatID,
        e.getMessage(),
        e
      );

      if (e.getMessage().contains("Chat not found with id")) {
        logger.warn("Chat not found - ID: {}", chatID);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
      } else if (e.getMessage().contains("User not found with email")) {
        logger.warn(
          "User not found while fetching chat - Error: {}",
          e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
      }
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
        e.getMessage()
      );
    }
  }

  @GetMapping("{userID}/chats")
  public ResponseEntity<?> findChatsByParticipant(@PathVariable int userID) {
    logger.info("Attempting to fetch chats for user ID: {}", userID);
    try {
      Set<ChatDTO> chats = chatService.findChatsByParticipant(userID);
      // Even if the list is empty, return OK with empty list
      return ResponseEntity.ok(chats);
    } catch (Exception e) {
        if (e.getMessage().contains("Chat not found with user id")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } else {
          logger.error(
            "Error fetching chats for user - ID: {}, Error: {}",
            userID,
            e.getMessage(),
            e
          );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            e.getMessage()
        );
        }
    }
  }

  @PostMapping("/{chatID}/addParticipant")
  public ResponseEntity<String> addParticipant(
    @PathVariable int chatID,
    @RequestParam int participantID
  ) {
    logger.info(
      "Attempting to add participant - Chat ID: {}, Participant ID: {}",
      chatID,
      participantID
    );

    try {
      chatService.addParticipant(chatID, participantID);
      logger.info(
        "Successfully added participant - Chat ID: {}, Participant ID: {}",
        chatID,
        participantID
      );
      return ResponseEntity.ok("Participant added successfully");
    } catch (Exception e) {
      logger.error(
        "Error adding participant - Chat ID: {}, Participant ID: {}, Error: {}",
        chatID,
        participantID,
        e.getMessage(),
        e
      );

      if (e.getMessage().contains("Chat not found with id")) {
        logger.warn("Chat not found while adding participant - ID: {}", chatID);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
      } else if (e.getMessage().contains("User not found with id")) {
        logger.warn(
          "User not found while adding participant - ID: {}",
          participantID
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
      }
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
        e.getMessage()
      );
    }
  }

  @DeleteMapping("/{chatID}/removeParticipant")
  public ResponseEntity<String> removeParticipant(
    @PathVariable int chatID,
    @RequestParam int participantID
  ) {
    logger.info(
      "Attempting to remove participant - Chat ID: {}, Participant ID: {}",
      chatID,
      participantID
    );

    try {
      chatService.removeParticipant(chatID, participantID);
      logger.info(
        "Successfully removed participant - Chat ID: {}, Participant ID: {}",
        chatID,
        participantID
      );
      return ResponseEntity.ok("Participant removed successfully");
    } catch (Exception e) {
      logger.error(
        "Error removing participant - Chat ID: {}, Participant ID: {}, Error: {}",
        chatID,
        participantID,
        e.getMessage(),
        e
      );

      if (e.getMessage().contains("Chat not found with id")) {
        logger.warn(
          "Chat not found while removing participant - ID: {}",
          chatID
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
      } else if (e.getMessage().contains("User not found with id")) {
        logger.warn(
          "User not found while removing participant - ID: {}",
          participantID
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
      } else if (e.getMessage().contains("User not found in chat")) {
        logger.warn(
          "User not found in chat - Chat ID: {}, User ID: {}",
          chatID,
          participantID
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
      }
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
        e.getMessage()
      );
    }
  }

    @GetMapping("/getMessageableUsers")
    public ResponseEntity<?> getMessageableUsers() {
        try {
            return ResponseEntity.ok(chatService.getMessageableUsers());
        } catch (Exception e) {
            if (e.getMessage().contains("User not found with email")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }
        }
    }
}
