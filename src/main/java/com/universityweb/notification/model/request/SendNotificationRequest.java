package com.universityweb.notification.model.request;

public record SendNotificationRequest(
        String message,
        String username
) {}
