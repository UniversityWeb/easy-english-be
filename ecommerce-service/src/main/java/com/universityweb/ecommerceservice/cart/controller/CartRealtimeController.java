package com.universityweb.ecommerceservice.cart.controller;

import com.universityweb.cart.service.CartService;
import com.universityweb.common.websocket.WebSocketConstants;
import com.universityweb.notification.request.AddNotificationRequest;
import com.universityweb.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class CartRealtimeController {

    private final NotificationService notificationService;
    private final CartService cartService;

    @MessageMapping(WebSocketConstants.CART_ITEM_COUNT_DESTINATION)
    public void refreshCartItemCount(AddNotificationRequest request) {
        String username = request.getUsername();
        String topic = WebSocketConstants.getCartItemCountTopic(username);
        int count = cartService.countItems(username);
        notificationService.sendRealtimeNotification(topic, count);
    }
}
