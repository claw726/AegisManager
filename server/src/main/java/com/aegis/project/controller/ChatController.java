package com.aegis.project.controller;

import com.aegis.project.dto.ChatDTO;
import com.aegis.project.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("api/chats")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @PostMapping("/create")
    public ResponseEntity<String> createChat(@RequestParam String type, @RequestParam Set<Integer> participants,
                                             @RequestParam String title) {
        try {
            chatService.createChat(type, participants, title);
            return ResponseEntity.ok("Chat created successfully");
        } catch (Exception e) {
            if (e.getMessage().contains("User not found with email")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("{chatID}/get")
    public ResponseEntity<?> getChat(@PathVariable int chatID) {
        try {
            return ResponseEntity.ok(chatService.getChat(chatID));
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

    @GetMapping("{userID}/chats")
    public ResponseEntity<?> findChatsByParticipant(@PathVariable int userID) {
        try {
            return ResponseEntity.ok(chatService.findChatsByParticipant(userID));
        } catch (Exception e) {
            if (e.getMessage().contains("Chat not found with user id")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }
        }
    }

    @PostMapping("/{chatID}/addParticipant")
    public ResponseEntity<String> addParticipant(@PathVariable int chatID, @RequestParam int participantID) {
        try {
            chatService.addParticipant(chatID, participantID);
            return ResponseEntity.ok("Participant added successfully");
        } catch (Exception e) {
            if (e.getMessage().contains("Chat not found with id")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else if (e.getMessage().contains("User not found with id")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }
        }
    }

    @DeleteMapping("/{chatID}/removeParticipant")
    public ResponseEntity<String> removeParticipant(@PathVariable int chatID, @RequestParam int participantID) {
        try {
            chatService.removeParticipant(chatID, participantID);
            return ResponseEntity.ok("Participant removed successfully");
        } catch (Exception e) {
            if (e.getMessage().contains("Chat not found with id")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else if (e.getMessage().contains("User not found with id")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else if (e.getMessage().contains("User not found in chat")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }
        }
    }
}
