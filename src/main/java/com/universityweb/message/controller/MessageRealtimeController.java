package com.universityweb.message.controller;

import com.universityweb.common.media.service.MediaService;
import com.universityweb.common.websocket.WebSocketConstants;
import com.universityweb.message.Message;
import com.universityweb.message.MessageDTO;
import com.universityweb.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Controller
@RequiredArgsConstructor
public class MessageRealtimeController {
    
    private static final Logger log = LogManager.getLogger(MessageRealtimeController.class);
    
    private final MessageService messageService;
    private final MediaService mediaService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @MessageMapping(WebSocketConstants.MESSAGE_DESTINATION)
    public void handleMessage(MessageDTO message) {
        log.info("Received message request via WebSocket: {}", message);

        handleMediaMessage(message);

        String senderUsername = message.getSenderUsername();
        String recipientUsername = message.getRecipientUsername();
        
        Message lastMsgBeforeSending = null;
        try {
            lastMsgBeforeSending = messageService.getLastMsg(senderUsername, recipientUsername);
        } catch (Exception e) {
            log.warn("No previous messages found between {} and {}", senderUsername, recipientUsername);
        }

        MessageDTO messageDTO = messageService.sendRealtimeMessage(message);
        log.info("Sent message via WebSocket: {}", messageDTO);

        if (message.getType() == Message.EType.TEXT) {
            messageService.processAutoReplyIfNeeded(senderUsername, recipientUsername, lastMsgBeforeSending);
        }
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
            log.error("Failed to upload media via WebSocket", e);
        }
    }
}
