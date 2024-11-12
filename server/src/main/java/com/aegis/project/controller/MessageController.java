package com.aegis.project.controller;

import com.aegis.project.dto.MessageDTO;
import com.aegis.project.service.MessageService;
import java.util.List;
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
    public ResponseEntity<?> addMessage(@RequestParam int chatID, @RequestParam String content) {
        try {
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

  @GetMapping("{chatID}/getMessages")
  public ResponseEntity<?> getMessages(@PathVariable int chatId) {
    logger.info("Attempting to fetch messages for chat ID: {}", chatId);
    try {
      Set<MessageDTO> messages = messageService.getMessages(chatId);
      logger.info(
        "Successfully retrieved {} messages for chat ID: {}",
        messages.size(),
        chatId
      );
      return ResponseEntity.ok(messages);
    } catch (Exception e) {
      if (e.getMessage().contains("not found")) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
      } else if (e.getMessage().contains("User not authorized")) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
          e.getMessage()
        );
      } else {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
          e.getMessage()
        );
      }
    }
  }
}
