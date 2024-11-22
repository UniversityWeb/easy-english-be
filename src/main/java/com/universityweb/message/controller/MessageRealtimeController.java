package com.universityweb.message.controller;

import com.universityweb.common.websocket.WebSocketConstants;
import com.universityweb.message.MessageDTO;
import com.universityweb.message.service.MessageService;
import com.universityweb.notification.controller.NotificationController;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MessageRealtimeController {
    private static final Logger log = LogManager.getLogger(NotificationController.class);
    private final MessageService messageService;

    @MessageMapping(WebSocketConstants.MESSAGE_DESTINATION)
    public void handleMessage(MessageDTO message) {
        log.info("Received message request: {}", message);
        MessageDTO messageDTO = messageService.sendRealtimeMessage(message);
        log.info("Sent message: {}", messageDTO);
    }
}
