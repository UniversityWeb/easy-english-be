package com.universityweb.notification.service;

import com.universityweb.common.auth.entity.User;
import com.universityweb.common.infrastructure.service.BaseService;
import com.universityweb.common.request.GetByUsernameRequest;
import com.universityweb.notification.entity.Notification;
import com.universityweb.notification.request.AddNotificationRequest;
import com.universityweb.notification.response.NotificationResponse;
import org.springframework.data.domain.Page;

public interface NotificationService extends BaseService<Notification, NotificationResponse, Long> {
    Page<NotificationResponse> getNotificationsByUsername(GetByUsernameRequest request);
    NotificationResponse addNewNotification(AddNotificationRequest request);
    NotificationResponse markAsRead(Long notificationId);
    User getUserByNotificationId(Long notificationId);
    NotificationResponse sendRealtimeNotification(AddNotificationRequest request);
    int countUnreadNotifications(String username);
    void sendRealtimeNotification(String topic, Object payload);
}
