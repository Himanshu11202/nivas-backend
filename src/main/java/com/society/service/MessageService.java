package com.society.service;

import com.society.entity.Message;
import com.society.entity.Product;
import com.society.entity.User;
import com.society.repository.MessageRepository;
import com.society.repository.ProductRepository;
import com.society.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    // Send message
    @Transactional
    public Message sendMessage(Long senderId, Long receiverId, Long productId, String messageText) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setProduct(product);
        message.setMessage(messageText);

        return messageRepository.save(message);
    }

    // Get chat history for a product between two users
    public List<Map<String, Object>> getChatHistory(Long productId, Long userId1, Long userId2) {
        // Get all messages for this product
        List<Message> allMessages = messageRepository.findByProductIdOrderByCreatedAtAsc(productId);
        
        // Filter messages where userId1 and userId2 are involved
        return allMessages.stream()
                .filter(msg -> 
                    (msg.getSender().getId().equals(userId1) && msg.getReceiver().getId().equals(userId2)) ||
                    (msg.getSender().getId().equals(userId2) && msg.getReceiver().getId().equals(userId1))
                )
                .map(this::convertToMap)
                .collect(Collectors.toList());
    }

    // Check if user can access chat (is either buyer or seller)
    public boolean canAccessChat(Long productId, Long userId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        Long sellerId = product.getUser().getId();
        
        // Get all messages for this product involving this user
        List<Message> messages = messageRepository.findByProductIdOrderByCreatedAtAsc(productId);
        
        // User is seller
        if (sellerId.equals(userId)) {
            return true;
        }
        
        // User has sent or received messages for this product
        boolean hasMessages = messages.stream()
                .anyMatch(msg -> msg.getSender().getId().equals(userId) || msg.getReceiver().getId().equals(userId));
        
        // User is the buyer (first one to message) or has messages
        return hasMessages;
    }

    private Map<String, Object> convertToMap(Message message) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", message.getId());
        map.put("message", message.getMessage());
        map.put("createdAt", message.getCreatedAt());
        
        Map<String, Object> sender = new HashMap<>();
        sender.put("id", message.getSender().getId());
        sender.put("name", message.getSender().getName());
        map.put("sender", sender);
        
        Map<String, Object> receiver = new HashMap<>();
        receiver.put("id", message.getReceiver().getId());
        receiver.put("name", message.getReceiver().getName());
        map.put("receiver", receiver);
        
        return map;
    }
}
