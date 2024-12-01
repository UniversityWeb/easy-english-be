package com.universityweb.notification.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationResponse {
    Long id;
    String previewImage;
    String message;
    String url;
    LocalDateTime createdDate;
    boolean read;
    String username;
}
