package com.universityweb.message.controller;

import com.universityweb.common.auth.dto.UserDTO;
import com.universityweb.common.auth.entity.User;
import com.universityweb.common.auth.exception.PermissionDenyException;
import com.universityweb.common.auth.service.auth.AuthService;
import com.universityweb.common.auth.service.user.UserService;
import com.universityweb.common.media.MediaUtils;
import com.universityweb.common.media.service.MediaService;
import com.universityweb.common.util.Utils;
import com.universityweb.message.Message;
import com.universityweb.message.MessageDTO;
import com.universityweb.message.service.MessageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/messages")
@Tag(name = "Messages")
@RequiredArgsConstructor
public class MessageController {

    private static final Logger log = LogManager.getLogger(MessageController.class);

    private final AuthService authService;
    private final MessageService messageService;
    private final MediaService mediaService;
    private final UserService userService;

    @GetMapping("/{senderUsername}/{recipientUsername}")
    public ResponseEntity<Page<MessageDTO>> getAllMessages(
            @PathVariable String senderUsername,
            @PathVariable String recipientUsername,
            @RequestParam int page,
            @RequestParam int size
    ) {
        String curUsername = authService.getCurrentUsername();
        if (!senderUsername.equals(curUsername) && !recipientUsername.equals(curUsername)) {
            throw new PermissionDenyException("You do not have permission to access this message");
        }

        Page<MessageDTO> messages = messageService.getAllMessages(senderUsername, recipientUsername, page, size);
        return ResponseEntity.ok(MediaUtils.addMessageMediaUrls(mediaService, messages));
    }

    @GetMapping("/get-recent-chats")
    public ResponseEntity<Page<UserDTO>> getRecentChats(
            @RequestParam int page,
            @RequestParam int size
    ) {
        String curUsername = authService.getCurrentUsername();
        Page<UserDTO> users = messageService.getRecentChats(curUsername, page, size);
        return ResponseEntity.ok(MediaUtils.addUserMediaUrlsForPage(mediaService, users));
    }

    @PostMapping("/send")
    public ResponseEntity<Void> handleMessage(@RequestBody MessageDTO message) {
        log.info("Received message request: {}", message);

        handleImageMessage(message);

        String senderUsername = authService.getCurrentUsername();
        String recipientUsername = message.getRecipientUsername();
        Message lastMsgBeforeSending = messageService.getLastMsg(senderUsername, recipientUsername);

        MessageDTO messageDTO = messageService.sendRealtimeMessage(message);
        log.info("Sent message: {}", messageDTO);

        sendAutoMessageIfNeeded(senderUsername, recipientUsername, lastMsgBeforeSending);

        return ResponseEntity.ok().build();
    }

    private void handleImageMessage(MessageDTO message) {
        if (message.getType() == Message.EType.IMAGE && message.getContent() != null && !message.getContent().isEmpty()) {
            try {
                String base64Str = message.getContent();
                String suffixUrl = mediaService.uploadFile(base64Str);
                message.setContent(suffixUrl);
            } catch (Exception e) {
                log.error("Failed to upload image", e);
            }
        }
    }

    private void sendAutoMessageIfNeeded(String senderUsername, String recipientUsername, Message lastMsgBeforeSending) {
        try {
            LocalDateTime now = LocalDateTime.now();

            if (lastMsgBeforeSending == null || !senderUsername.equals(lastMsgBeforeSending.getSender().getUsername())) {
                return;
            }

            long minutesSinceLastMsg = Duration.between(lastMsgBeforeSending.getSendingTime(), now).toMinutes();
            if (minutesSinceLastMsg <= Utils.AUTO_MESSAGE_TIMEOUT_MINUTES) {
                return;
            }

            User recipient = userService.loadUserByUsername(recipientUsername);

            if (recipient.getRole() != User.ERole.TEACHER) {
                return;
            }

            messageService.sendAutoMessage(senderUsername, recipientUsername, now);
        } catch (Exception e) {
            log.error("Failed to send auto-message", e);
        }
    }
}
