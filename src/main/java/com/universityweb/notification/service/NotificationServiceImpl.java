package com.universityweb.notification.service;

import com.universityweb.common.auth.entity.User;
import com.universityweb.common.auth.exception.UserNotFoundException;
import com.universityweb.common.auth.service.user.UserService;
import com.universityweb.common.infrastructure.service.BaseServiceImpl;
import com.universityweb.common.request.GetByUsernameRequest;
import com.universityweb.common.websocket.WebSocketConstants;
import com.universityweb.notification.NotificationMapper;
import com.universityweb.notification.NotificationRepos;
import com.universityweb.notification.entity.Notification;
import com.universityweb.notification.exception.NotificationNotFoundException;
import com.universityweb.notification.request.AddNotificationRequest;
import com.universityweb.notification.response.NotificationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl
        extends BaseServiceImpl<Notification, NotificationResponse, Long, NotificationRepos, NotificationMapper>
        implements NotificationService {

    private final UserService userService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public NotificationServiceImpl(
            NotificationRepos repository,
            NotificationMapper mapper,
            UserService userService,
            SimpMessagingTemplate simpMessagingTemplate
    ) {
        super(repository, mapper);
        this.userService  = userService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Override
    public Page<NotificationResponse> getNotificationsByUsername(GetByUsernameRequest request) {
        String username = request.getUsername();
        int pageNumber = request.getPage();
        int size = request.getSize();

        Sort sort = Sort.by("createdDate");
        Pageable pageable = PageRequest.of(pageNumber, size, sort.descending());

        Page<Notification> notificationsPage = repository.findByUserUsername(username, pageable);
        return notificationsPage.map(mapper::toDTO);
    }

    @Override
    public NotificationResponse addNewNotification(AddNotificationRequest request) {
        String username = request.username();
        User user = userService.loadUserByUsername(username);

        Notification notification = Notification.builder()
                .message(request.message())
                .createdDate(request.createdDate())
                .read(false)
                .user(user)
                .build();

        return savedAndConvertToDTO(notification);
    }

    @Override
    public NotificationResponse markAsRead(Long notificationId) {
        Notification notification = getEntityById(notificationId);
        notification.setRead(true);
        return savedAndConvertToDTO(notification);
    }

    @Override
    public User getUserByNotificationId(Long notificationId) {
        Notification notification = getEntityById(notificationId);
        User user = notification.getUser();
        if (user == null) {
            throw new UserNotFoundException("Could not find user with notificationId=" + notificationId);
        }

        return user;
    }

    @Override
    public NotificationResponse sendRealtimeNotification(AddNotificationRequest request) {
        String username = request.username();
        NotificationResponse notificationResponse = addNewNotification(request);

        try {
            String sendNotificationDestination = WebSocketConstants.getNotificationTopic(username);
            simpMessagingTemplate.convertAndSend(sendNotificationDestination, notificationResponse);

            int numberOfUnreadNotifications = countUnreadNotifications(username);
            String refreshUnreadNotiDestination = WebSocketConstants.getNotificationsCountTopic(username);
            simpMessagingTemplate.convertAndSend(refreshUnreadNotiDestination, numberOfUnreadNotifications);
        } catch (Exception e) {
            log.error(e);
        }

        return notificationResponse;
    }

    @Override
    public int countUnreadNotifications(String username) {
        return repository.countUnreadNotificationsByUser(username);
    }

    @Override
    protected void throwNotFoundException(Long id) {
        String notFoundMsg = "Could not find any notifications with id=" + id;
        throw new NotificationNotFoundException(notFoundMsg);
    }

    @Override
    public NotificationResponse update(Long id, NotificationResponse dto) {
        return null;
    }

    @Override
    public void softDelete(Long id) {
        Notification notification = getEntityById(id);
        notification.setIsDeleted(true);
        repository.save(notification);
    }
}
