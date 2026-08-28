package com.universityweb.message;

import com.universityweb.common.auth.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MessageRepos extends JpaRepository<Message, UUID> {
    @Query("""
       SELECT m
       FROM Message m
       WHERE ((m.sender.username = :senderUsername AND m.recipient.username = :recipientUsername AND (m.deletedBySender = false OR m.deletedBySender IS NULL))
          OR  (m.sender.username = :recipientUsername AND m.recipient.username = :senderUsername AND (m.deletedByRecipient = false OR m.deletedByRecipient IS NULL)))
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
            WHERE m.recipient.username = :curUsername AND (m.deletedByRecipient = false OR m.deletedByRecipient IS NULL)
            UNION
            SELECT DISTINCT m.recipient
            FROM Message m
            WHERE m.sender.username = :curUsername AND (m.deletedBySender = false OR m.deletedBySender IS NULL)
        )
        ORDER BY (SELECT MAX(m.sendingTime) 
                  FROM Message m 
                  WHERE (m.sender = u AND m.recipient.username = :curUsername AND (m.deletedByRecipient = false OR m.deletedByRecipient IS NULL)) 
                     OR (m.recipient = u AND m.sender.username = :curUsername AND (m.deletedBySender = false OR m.deletedBySender IS NULL))
                 ) DESC
    """)
    Page<User> getRecentChats(
            String curUsername,
            Pageable pageable);

    @Query("""
        SELECT m FROM Message m
        WHERE m.sender.username = :senderUsername
          AND m.recipient.username = :recipientUsername
        ORDER BY m.sendingTime DESC
        LIMIT 1
    """)
    Optional<Message> findTopBySenderAndRecipientOrderBySendingTimeDesc(String senderUsername, String recipientUsername);

    @Query("""
       SELECT m FROM Message m
       WHERE ((m.sender.username = :sender1 AND m.recipient.username = :recipient1 AND (m.deletedBySender = false OR m.deletedBySender IS NULL))
          OR  (m.sender.username = :sender2 AND m.recipient.username = :recipient2 AND (m.deletedByRecipient = false OR m.deletedByRecipient IS NULL)))
       ORDER BY m.sendingTime DESC LIMIT 1
    """)
    Optional<Message> findTopBySenderUsernameAndRecipientUsernameOrSenderUsernameAndRecipientUsernameOrderBySendingTimeDesc(
        @Param("sender1") String sender1, 
        @Param("recipient1") String recipient1, 
        @Param("sender2") String sender2, 
        @Param("recipient2") String recipient2
    );

    @Query("SELECT COUNT(m) FROM Message m WHERE m.recipient.username = :recipient AND m.sender.username = :sender AND m.status != 'READ' AND (m.deletedByRecipient = false OR m.deletedByRecipient IS NULL)")
    int countUnreadMessages(@Param("recipient") String recipient, @Param("sender") String sender);
}
