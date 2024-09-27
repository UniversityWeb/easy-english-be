package com.universityweb.notification.service;

import com.universityweb.common.auth.entity.User;
import com.universityweb.common.auth.exception.UserNotFoundException;
import com.universityweb.common.auth.service.user.UserService;
import com.universityweb.notification.NotificationNotFoundException;
import com.universityweb.notification.model.Notification;
import com.universityweb.notification.model.NotificationDTO;
import com.universityweb.notification.NotificationMapper;
import com.universityweb.notification.model.request.GetNotificationsRequest;
import com.universityweb.notification.model.request.SendNotificationRequest;
import com.universityweb.notification.NotificationRepos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationMapper notificationMapper = NotificationMapper.INSTANCE;

    @Autowired
    private NotificationRepos notificationRepos;

    @Autowired
    private UserService userService;

    @Override
    public Page<NotificationDTO> getNotificationsByUsername(GetNotificationsRequest request) {
        String username = request.getUsername();
        int pageNumber = request.getPage();
        int size = request.getSize();

        Pageable pageable = PageRequest.of(pageNumber, size);

        Page<Notification> notificationsPage = notificationRepos.findByUserUsername(username, pageable);
        return notificationsPage.map(notificationMapper::toDTO);
    }

    @Override
    public NotificationDTO send(SendNotificationRequest request) {
        User user = userService.loadUserByUsername(request.username());

        Notification notification = Notification.builder()
                .message(request.message())
                .createdDate(LocalDateTime.now())
                .read(false)
                .user(user)
                .build();

        Notification saved = notificationRepos.save(notification);
        return notificationMapper.toDTO(saved);
    }

    @Override
    public NotificationDTO markAsRead(Long notificationId) {
        Notification notification = getNotificationById(notificationId);

        notification.setRead(true);
        Notification saved = notificationRepos.save(notification);
        return notificationMapper.toDTO(saved);
    }

    @Override
    public User getUserByNotificationId(Long notificationId) {
        Notification notification = getNotificationById(notificationId);
        User user = notification.getUser();
        if (user == null) {
            throw new UserNotFoundException("Could not find user with notificationId=" + notificationId);
        }

        return user;
    }

    private Notification getNotificationById(Long id) {
        String notFoundMsg = "Could not find any notifications with id=" + id;
        return notificationRepos.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException(notFoundMsg));
    }
}
