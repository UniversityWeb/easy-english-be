package com.universityweb.notification.service;

import com.universityweb.common.auth.entity.User;
import com.universityweb.notification.model.NotificationDTO;
import com.universityweb.notification.model.request.GetNotificationsRequest;
import com.universityweb.notification.model.request.SendNotificationRequest;
import org.springframework.data.domain.Page;

public interface NotificationService {
    Page<NotificationDTO> getNotificationsByUsername(GetNotificationsRequest request);
    NotificationDTO send(SendNotificationRequest request);
    NotificationDTO markAsRead(Long notificationId);
    User getUserByNotificationId(Long notificationId);
}
