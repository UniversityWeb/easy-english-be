package com.universityweb.message.service;

import com.universityweb.common.auth.dto.SettingsDTO;
import com.universityweb.common.auth.dto.UserDTO;
import com.universityweb.common.auth.entity.User;
import com.universityweb.common.auth.mapper.UserMapper;
import com.universityweb.common.auth.service.auth.AuthService;
import com.universityweb.common.auth.service.user.UserService;
import com.universityweb.common.exception.ResourceNotFoundException;
import com.universityweb.common.infrastructure.service.BaseServiceImpl;
import com.universityweb.common.util.Utils;
import com.universityweb.common.websocket.WebSocketConstants;
import com.universityweb.message.Message;
import com.universityweb.message.MessageDTO;
import com.universityweb.message.MessageMapper;
import com.universityweb.message.MessageRepos;
import com.universityweb.notification.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class MessageServiceImpl
        extends BaseServiceImpl<Message, MessageDTO, UUID, MessageRepos, MessageMapper>
        implements MessageService{

    private final UserService userService;
    private final UserMapper userMapper;
    private final NotificationService notificationService;
    private final AuthService authService;

    @Autowired
    public MessageServiceImpl(
            MessageRepos repository,
            MessageMapper mapper,
            UserService userService,
            UserMapper userMapper,
            NotificationService notificationService,
            AuthService authService) {
        super(repository, mapper);
        this.userService = userService;
        this.userMapper = userMapper;
        this.notificationService = notificationService;
        this.authService = authService;
    }

    @Override
    protected void throwNotFoundException(UUID id) {
        throw new ResourceNotFoundException("Couldn't find message with id: " + id);
    }

    @Override
    public MessageDTO create(MessageDTO dto) {
        MessageDTO messageDTO = super.create(dto);
        sendRealtimeMessage(messageDTO);
        return messageDTO;
    }

    @Override
    protected void setEntityRelationshipsBeforeAdd(Message entity, MessageDTO dto) {
        entity.setSendingTime(LocalDateTime.now());

        String senderUsername = dto.getSenderUsername();
        User sender = userService.loadUserByUsername(senderUsername);

        String recipientUsername = dto.getRecipientUsername();
        User recipient = userService.loadUserByUsername(recipientUsername);

        entity.setSender(sender);
        entity.setRecipient(recipient);
    }

    @Override
    public void delete(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public Page<MessageDTO> getAllMessages(
            String senderUsername,
            String recipientUsername,
            int page, int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Message> messages = repository.getAllMessages(senderUsername, recipientUsername, pageable);
        return mapper.mapPageToPageDTO(messages);
    }

    @Override
    public Page<UserDTO> getRecentChats(String curUsername, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = repository.getRecentChats(curUsername, pageable);
        return userMapper.mapPageToPageDTO(users);
    }

    @Override
    public MessageDTO sendRealtimeMessage(MessageDTO dto) {
        MessageDTO messageDTO = super.create(dto);

        sendNotifications(dto.getRecipientUsername(), messageDTO);
        sendNotifications(dto.getSenderUsername(), messageDTO);

        return messageDTO;
    }

    private void sendNotifications(String username, MessageDTO messageDTO) {
        notificationService.sendRealtimeNotification(WebSocketConstants.getMessageTopic(username), messageDTO);
        notificationService.sendRealtimeNotification(WebSocketConstants.getRecentChatsTopic(username), messageDTO);
    }

    @Override
    public MessageDTO sendAutoMessage(String senderUsername, String recipientUsername, LocalDateTime sendingTime) {
        User recipient = userService.loadUserByUsername(recipientUsername);
        SettingsDTO existingSettingsDTO = Utils.convertFromJson(recipient.getSettings(), SettingsDTO.class);
        assert existingSettingsDTO != null;
        String autoMsg = existingSettingsDTO.getAutoReplyMessage();

        MessageDTO messageDTO = MessageDTO.builder()
                .type(Message.EType.TEXT)
                .content(autoMsg)
                .sendingTime(sendingTime)
                .senderUsername(recipientUsername)
                .recipientUsername(senderUsername)
                .build();

        return sendRealtimeMessage(messageDTO);
    }

    @Override
    public Message getLastMsg(String senderUsername, String recipientUsername) {
        return repository
                .findTopBySenderAndRecipientOrderBySendingTimeDesc(senderUsername, recipientUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Couldn't find the last message between " + recipientUsername + " and " + senderUsername));
    }
}
