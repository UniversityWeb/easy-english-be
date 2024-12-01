package com.universityweb.notification.request;

import java.time.LocalDateTime;

public record AddNotificationRequest(
        String message,
        String url,
        String username,
        LocalDateTime createdDate
) {}
