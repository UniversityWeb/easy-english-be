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

@Controller
@RequiredArgsConstructor
public class MessageRealtimeController {
    
    private static final Logger log = LogManager.getLogger(MessageRealtimeController.class);
    
    private final MessageService messageService;
    private final MediaService mediaService;

    @MessageMapping(WebSocketConstants.MESSAGE_DESTINATION)
    public void handleMessage(MessageDTO message) {
        log.info("Received message request via WebSocket: {}", message);

        handleImageMessage(message);

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

    private void handleImageMessage(MessageDTO message) {
        if (message.getType() == Message.EType.IMAGE && message.getContent() != null && !message.getContent().isEmpty()) {
            try {
                String base64Str = message.getContent();
                String suffixUrl = mediaService.uploadFile(base64Str);
                message.setContent(suffixUrl);
            } catch (Exception e) {
                log.error("Failed to upload image via WebSocket", e);
            }
        }
    }
}
