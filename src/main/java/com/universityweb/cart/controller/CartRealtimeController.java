package com.universityweb.cart.controller;

import com.universityweb.common.websocket.WebSocketConstants;
import com.universityweb.notification.request.AddNotificationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class CartRealtimeController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping(WebSocketConstants.CART_ITEM_COUNT_DESTINATION)
    public void refreshCartItemCount(AddNotificationRequest request) {
        String destination = WebSocketConstants.getCartItemCountTopic(request.username());
        simpMessagingTemplate.convertAndSend(destination, true);
    }
}
