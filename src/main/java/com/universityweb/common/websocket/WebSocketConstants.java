package com.universityweb.common.websocket;

public class WebSocketConstants {
    public static final String NOTIFICATION_DESTINATION = "/notifications";
    public static final String CART_ITEM_COUNT_DESTINATION = "/cart-item-count";
    public static final String MESSAGE_DESTINATION = "/messages";

    public static final String ONLINE_USERS_TOPIC = "/topic/online-users";

    public static String getNotificationTopic(String username) {
        return String.format("/topic/notifications/%s", username);
    }

    public static String getCartItemCountTopic(String username) {
        return String.format("/topic/cart-item-count/%s", username);
    }

    public static String testResultNotificationTopic(Long testId) {
        return String.format("/topic/test-result/%s", testId);
    }

    public static String getMessageTopic(String recipientUsername) {
        return String.format("/topic/messages/%s", recipientUsername);
    }

    public static String getRecentChatsTopic(String username) {
        return String.format("/topic/recent-chats/%s", username);
    }

    public static String getNotificationsCountTopic(String username) {
        return String.format("/topic/notifications-count/%s", username);
    }
}
