package com.universityweb.common.websocket;

public class WebSocketConstants {
    public static final String NOTIFICATION_DESTINATION = "/notifications";

    public static String getNotificationTopic(String username) {
        return String.format("/topic/notifications/%s", username);
    }
}
