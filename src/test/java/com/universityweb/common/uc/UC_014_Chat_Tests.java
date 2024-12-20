package com.universityweb.common.uc;

import com.universityweb.message.Message;
import com.universityweb.message.MessageDTO;
import com.universityweb.message.MessageMapper;
import com.universityweb.message.service.MessageServiceImpl;
import com.universityweb.notification.request.AddNotificationRequest;
import com.universityweb.notification.response.NotificationResponse;
import com.universityweb.notification.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UC_014_Chat_Tests {

    @InjectMocks
    private MessageServiceImpl messageService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private MessageMapper mapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendTextMessage_Success_CHAT_POS_001() {
        // Arrange
        UUID messageId = UUID.randomUUID();
        String senderUsername = "john_doe";
        String recipientUsername = "jane_doe";
        String content = "Hello, Jane!";

        MessageDTO messageDTO = MessageDTO.builder()
                .id(messageId)
                .type(Message.EType.TEXT)
                .content(content)
                .sendingTime(LocalDateTime.now())
                .senderUsername(senderUsername)
                .recipientUsername(recipientUsername)
                .build();

        AddNotificationRequest notificationRequest = AddNotificationRequest.builder()
                .username(recipientUsername)
                .message("New message from " + senderUsername)
                .url("/chat")
                .createdDate(LocalDateTime.now())
                .build();

        NotificationResponse notificationResponse = NotificationResponse.builder()
                .id(1L)
                .message("New message from " + senderUsername)
                .username(recipientUsername)
                .read(false)
                .createdDate(LocalDateTime.now())
                .build();

        when(notificationService.sendRealtimeNotification(any(AddNotificationRequest.class)))
                .thenReturn(notificationResponse);

        // Act
        MessageDTO result = messageService.sendRealtimeMessage(messageDTO);

        // Assert
        assertNotNull(result);
        assertEquals(content, result.getContent());
        assertEquals(senderUsername, result.getSenderUsername());
        assertEquals(recipientUsername, result.getRecipientUsername());

        verify(notificationService, times(1)).sendRealtimeNotification(
                eq("/topic/messages/jane_doe"),
                any(MessageDTO.class)
        );
        verify(notificationService, times(1)).sendRealtimeNotification(
                eq("/topic/recent-chats/jane_doe"),
                any(MessageDTO.class)
        );
    }
}
