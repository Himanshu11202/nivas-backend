package com.society.repository;

import com.society.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    // Get all messages for a product between two users
    List<Message> findByProductIdAndSenderIdAndReceiverIdOrProductIdAndSenderIdAndReceiverIdOrderByCreatedAtAsc(
            Long productId1, Long senderId1, Long receiverId1,
            Long productId2, Long senderId2, Long receiverId2);

    // Get all messages for a product (conversation between buyer and seller)
    List<Message> findByProductIdOrderByCreatedAtAsc(Long productId);

    // Get all messages where user is sender or receiver for a product
    List<Message> findByProductIdAndSenderIdOrProductIdAndReceiverIdOrderByCreatedAtAsc(
            Long productId1, Long senderId, Long productId2, Long receiverId);
}
