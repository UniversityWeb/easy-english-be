package com.universityweb.notification.service;

import com.universityweb.common.auth.entity.User;
import com.universityweb.notification.response.NotificationResponse;
import com.universityweb.notification.request.GetNotificationsRequest;
import com.universityweb.notification.request.SendNotificationRequest;
import org.springframework.data.domain.Page;

public interface NotificationService {
    Page<NotificationResponse> getNotificationsByUsername(GetNotificationsRequest request);
    NotificationResponse send(SendNotificationRequest request);
    NotificationResponse markAsRead(Long notificationId);
    User getUserByNotificationId(Long notificationId);
}
