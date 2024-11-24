package com.universityweb.notification.request;

import java.time.LocalDateTime;

public record AddNotificationRequest(
        String message,
        String username,
        LocalDateTime createdDate
) {}
