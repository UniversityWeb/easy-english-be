package com.universityweb.message.controller;

import com.universityweb.common.auth.dto.UserDTO;
import com.universityweb.common.auth.exception.PermissionDenyException;
import com.universityweb.common.auth.service.auth.AuthService;
import com.universityweb.common.media.MediaUtils;
import com.universityweb.common.media.service.MediaService;
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

import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@RestController
@RequestMapping("/api/v1/messages")
@Tag(name = "Messages")
@RequiredArgsConstructor
public class MessageController {

    private static final Logger log = LogManager.getLogger(MessageController.class);

    private final AuthService authService;
    private final MessageService messageService;
    private final MediaService mediaService;
    private final ObjectMapper objectMapper = new ObjectMapper();

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

        String otherUsername = curUsername.equals(senderUsername) ? recipientUsername : senderUsername;
        Page<MessageDTO> messages = messageService.getAllMessages(curUsername, otherUsername, page, size);
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

        handleMediaMessage(message);

        String senderUsername = authService.getCurrentUsername();
        message.setSenderUsername(senderUsername);
        String recipientUsername = message.getRecipientUsername();
        
        Message lastMsgBeforeSending = null;
        try {
            lastMsgBeforeSending = messageService.getLastMsg(senderUsername, recipientUsername);
        } catch (Exception e) {
            log.warn("No previous messages found between {} and {}", senderUsername, recipientUsername);
        }

        MessageDTO messageDTO = messageService.sendRealtimeMessage(message);
        log.info("Sent message: {}", messageDTO);

        if (message.getType() == Message.EType.TEXT) {
            messageService.processAutoReplyIfNeeded(senderUsername, recipientUsername, lastMsgBeforeSending);
        }

        return ResponseEntity.ok().build();
    }

    private void handleMediaMessage(MessageDTO message) {
        if (message.getContent() == null || message.getContent().isEmpty()) return;

        try {
            if (message.getType() == Message.EType.IMAGE) {
                String suffixUrl = mediaService.uploadFile(message.getContent());
                message.setContent(suffixUrl);
            } else if (message.getType() == Message.EType.FILE) {
                JsonNode jsonNode = objectMapper.readTree(message.getContent());
                if (jsonNode.has("base64Data")) {
                    String base64Str = jsonNode.get("base64Data").asText();
                    String suffixUrl = mediaService.uploadFile(base64Str);
                    ((ObjectNode) jsonNode).put("url", suffixUrl);
                    ((ObjectNode) jsonNode).remove("base64Data");
                    message.setContent(objectMapper.writeValueAsString(jsonNode));
                }
            }
        } catch (Exception e) {
            log.error("Failed to upload media message", e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageDTO> deleteMessage(
            @PathVariable java.util.UUID id,
            @RequestParam String type
    ) {
        String curUsername = authService.getCurrentUsername();
        MessageDTO updatedMsg = messageService.deleteMessage(id, curUsername, type);
        return ResponseEntity.ok(updatedMsg);
    }
}
