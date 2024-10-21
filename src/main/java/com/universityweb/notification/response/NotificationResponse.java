package com.universityweb.notification.response;

import lombok.AllArgsConstructor;
import lombok.*;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NotificationResponse {
    private Long id;

    private String message;

    private LocalDateTime createdDate;

    private boolean read;

    private String username;
}
