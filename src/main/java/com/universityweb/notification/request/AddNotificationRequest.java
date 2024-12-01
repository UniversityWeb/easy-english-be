package com.universityweb.notification.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddNotificationRequest {
    String previewImage;
    String message;
    String url;
    String username;
    LocalDateTime createdDate;
}
