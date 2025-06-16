package com.universityweb.common.uc;

import com.universityweb.common.request.GetByUsernameRequest;
import com.universityweb.notification.NotificationMapper;
import com.universityweb.notification.NotificationRepos;
import com.universityweb.notification.entity.Notification;
import com.universityweb.notification.response.NotificationResponse;
import com.universityweb.notification.service.NotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UC_004_SeeNotification_Tests {

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Mock
    private NotificationRepos repository;

    @Mock
    private NotificationMapper mapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetNotificationsByUsername_Success_AUTH_SEE_NOTIFICATIONS_POS_001() {
        // Arrange
        String username = "john_doe";
        int pageNumber = 0;
        int size = 5;
        GetByUsernameRequest request = new GetByUsernameRequest(pageNumber, size, username);

        Pageable pageable = PageRequest.of(pageNumber, size, Sort.by("createdDate").descending());

        Notification notification1 = Notification.builder()
                .id(1L)
                .previewImage("image1.jpg")
                .message("Message 1")
                .url("url1")
                .createdDate(LocalDateTime.now())
                .read(false)
                .isDeleted(false)
                .user(null)
                .build();

        Notification notification2 = Notification.builder()
                .id(2L)
                .previewImage("image2.jpg")
                .message("Message 2")
                .url("url2")
                .createdDate(LocalDateTime.now())
                .read(true)
                .isDeleted(false)
                .user(null)
                .build();

        Page<Notification> notificationsPage = new PageImpl<>(List.of(notification1, notification2));

        NotificationResponse response1 = new NotificationResponse(1L, "image1.jpg", "Message 1", "url1", LocalDateTime.now(), false, "john_doe");
        NotificationResponse response2 = new NotificationResponse(2L, "image2.jpg", "Message 2", "url2", LocalDateTime.now(), true, "john_doe");

        when(repository.findByUserUsername(username, pageable)).thenReturn(notificationsPage);
        when(mapper.mapPageToPageDTO(notificationsPage)).thenReturn(new PageImpl<>(List.of(response1, response2)));

        // Act
        Page<NotificationResponse> result = notificationService.getNotificationsByUsername(request);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals("Message 1", result.getContent().get(0).getMessage());
        assertEquals("Message 2", result.getContent().get(1).getMessage());

        verify(repository, times(1)).findByUserUsername(username, pageable);
        verify(mapper, times(1)).mapPageToPageDTO(notificationsPage);
    }

    @Test
    void testGetNotificationsByUsername_NoNotifications_AUTH_SEE_NOTIFICATIONS_NEG_001() {
        // Arrange
        String username = "john_doe";
        int pageNumber = 0;
        int size = 5;
        GetByUsernameRequest request = new GetByUsernameRequest(pageNumber, size, username);

        Pageable pageable = PageRequest.of(pageNumber, size, Sort.by("createdDate").descending());

        Page<Notification> notificationsPage = new PageImpl<>(List.of());

        when(repository.findByUserUsername(username, pageable)).thenReturn(notificationsPage);
        when(mapper.mapPageToPageDTO(notificationsPage)).thenReturn(new PageImpl<>(List.of()));

        // Act
        Page<NotificationResponse> result = notificationService.getNotificationsByUsername(request);

        // Assert
        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());

        verify(repository, times(1)).findByUserUsername(username, pageable);
        verify(mapper, times(1)).mapPageToPageDTO(notificationsPage);
    }
}
