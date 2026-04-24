package com.society.controller;

import com.society.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "http://localhost:3000")
public class MessageController {

    @Autowired
    private MessageService messageService;

    // Send message
    @PostMapping
    @PreAuthorize("hasRole('RESIDENT') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> sendMessage(@RequestBody Map<String, Object> request) {
        try {
            Long senderId = Long.valueOf(request.get("senderId").toString());
            Long receiverId = Long.valueOf(request.get("receiverId").toString());
            Long productId = Long.valueOf(request.get("productId").toString());
            String message = (String) request.get("message");

            messageService.sendMessage(senderId, receiverId, productId, message);
            return ResponseEntity.ok(Map.of("success", true, "message", "Message sent successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Get chat history for a product
    @GetMapping("/{productId}")
    @PreAuthorize("hasRole('RESIDENT') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> getChatHistory(
            @PathVariable Long productId,
            @RequestParam Long userId1,
            @RequestParam Long userId2) {
        try {
            // Verify user can access this chat
            boolean canAccess = messageService.canAccessChat(productId, userId1);
            if (!canAccess) {
                return ResponseEntity.status(403).body(Map.of("error", "You don't have access to this chat"));
            }

            List<Map<String, Object>> messages = messageService.getChatHistory(productId, userId1, userId2);
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
