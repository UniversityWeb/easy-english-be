package com.universityweb.notification.controller;

import com.universityweb.common.websocket.WebSocketConstants;
import com.universityweb.notification.request.AddNotificationRequest;
import com.universityweb.notification.response.NotificationResponse;
import com.universityweb.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class NotificationRealtimeController {

    private static final Logger log = LogManager.getLogger(NotificationController.class);
    private final NotificationService notificationService;

    @MessageMapping(WebSocketConstants.NOTIFICATION_DESTINATION)
    public void handleNotification(AddNotificationRequest request) {
        log.info("Received notification request: {}", request);
        NotificationResponse notificationResponse = notificationService.sendRealtimeNotification(request);
        log.info("Sent notification: {}", notificationResponse);
    }
}
