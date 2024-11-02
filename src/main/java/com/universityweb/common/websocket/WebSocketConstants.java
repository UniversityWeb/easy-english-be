package com.universityweb.common.websocket;

public class WebSocketConstants {
    public static final String NOTIFICATION_DESTINATION = "/notifications";
    public static final String CART_ITEM_COUNT_DESTINATION = "/cart-item-count";

    public static String getNotificationTopic(String username) {
        return String.format("/topic/notifications/%s", username);
    }

    public static String getCartItemCountTopic(String username) {
        return String.format("/topic/cart-item-count/%s", username);
    }
}
