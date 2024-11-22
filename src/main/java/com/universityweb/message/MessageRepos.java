package com.universityweb.message;

import com.universityweb.common.auth.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MessageRepos extends JpaRepository<Message, UUID> {
    @Query("""
       SELECT m
       FROM Message m
       WHERE (m.sender.username = :senderUsername AND m.recipient.username = :recipientUsername)
          OR (m.sender.username = :recipientUsername AND m.recipient.username = :senderUsername)
       ORDER BY m.sendingTime ASC
   """)
    Page<Message> getAllMessages(
            String senderUsername,
            String recipientUsername,
            Pageable pageable);

    @Query("""
        SELECT u
        FROM User u
        WHERE u IN (
            SELECT DISTINCT m.sender
            FROM Message m
            WHERE m.recipient.username = :curUsername
            UNION
            SELECT DISTINCT m.recipient
            FROM Message m
            WHERE m.sender.username = :curUsername
        )
        ORDER BY (SELECT MAX(m.sendingTime) 
                  FROM Message m 
                  WHERE m.sender = u OR m.recipient = u) DESC
    """)
    Page<User> getRecentChats(
            String curUsername,
            Pageable pageable);
}
