package com.universityweb.common.uc;

import com.universityweb.common.auth.dto.UserDTO;
import com.universityweb.common.auth.entity.User;
import com.universityweb.common.auth.mapper.UserMapper;
import com.universityweb.common.auth.service.user.UserService;
import com.universityweb.message.Message;
import com.universityweb.message.MessageDTO;
import com.universityweb.message.MessageMapper;
import com.universityweb.message.MessageRepos;
import com.universityweb.message.service.MessageServiceImpl;
import com.universityweb.notification.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UC_023_InteractToStudent_Tests {

    @InjectMocks
    private MessageServiceImpl messageService;

    @Mock
    private MessageRepos messageRepository;

    @Mock
    private MessageMapper messageMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserService userService;
    @Mock
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendRealtimeMessage_Success() {
        // Arrange
        MessageDTO request = MessageDTO.builder()
                .id(UUID.randomUUID())
                .type(Message.EType.TEXT)
                .content("Hello, Student!")
                .sendingTime(LocalDateTime.now())
                .senderUsername("teacher1")
                .recipientUsername("student1")
                .build();

        Message messageEntity = Message.builder()
                .id(request.getId())
                .type(Message.EType.TEXT)
                .content("Hello, Student!")
                .sendingTime(LocalDateTime.now())
                .build();

        MessageDTO expectedResponse = request;

        when(messageMapper.toEntity(any(MessageDTO.class))).thenReturn(messageEntity);
        when(messageRepository.save(any(Message.class))).thenReturn(messageEntity);
        when(messageMapper.toDTO(any(Message.class))).thenReturn(expectedResponse);

        // Act
        MessageDTO result = messageService.sendRealtimeMessage(request);

        // Assert
        assertNotNull(result);
        assertEquals("Hello, Student!", result.getContent());
        verify(notificationService, times(2)).sendRealtimeNotification(anyString(), any(MessageDTO.class));
    }

    @Test
    void testGetAllMessages_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Message messageEntity = Message.builder()
                .id(UUID.randomUUID())
                .type(Message.EType.TEXT)
                .content("Hello, Student!")
                .sendingTime(LocalDateTime.now())
                .build();

        MessageDTO dto = MessageDTO.builder()
                .id(messageEntity.getId())
                .type(Message.EType.TEXT)
                .content("Hello, Student!")
                .sendingTime(messageEntity.getSendingTime())
                .build();

        Page<Message> messagePage = new PageImpl<>(Collections.singletonList(messageEntity));

        when(messageRepository.getAllMessages("teacher1", "student1", pageable)).thenReturn(messagePage);
        when(messageMapper.mapPageToPageDTO(any(Page.class))).thenReturn(new PageImpl<>(Collections.singletonList(dto)));

        // Act
        Page<MessageDTO> result = messageService.getAllMessages("teacher1", "student1", 0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Hello, Student!", result.getContent().get(0).getContent());
        verify(messageRepository, times(1)).getAllMessages("teacher1", "student1", pageable);
    }

    @Test
    void testGetRecentChats_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        User user = User.builder()
                .username("student1")
                .build();

        UserDTO userDTO = UserDTO.builder()
                .username("student1")
                .build();

        Page<User> userPage = new PageImpl<>(Collections.singletonList(user));

        when(messageRepository.getRecentChats("teacher1", pageable)).thenReturn(userPage);
        when(userMapper.mapPageToPageDTO(any(Page.class))).thenReturn(new PageImpl<>(Collections.singletonList(userDTO)));

        // Act
        Page<UserDTO> result = messageService.getRecentChats("teacher1", 0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("student1", result.getContent().get(0).getUsername());
        verify(messageRepository, times(1)).getRecentChats("teacher1", pageable);
    }
}
