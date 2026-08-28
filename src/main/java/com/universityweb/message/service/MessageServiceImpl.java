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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
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
    public MessageDTO deleteMessage(UUID messageId, String curUsername, String deleteType) {
        Message message = repository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Couldn't find message with id: " + messageId));

        boolean isSender = curUsername.equals(message.getSender().getUsername());
        boolean isRecipient = curUsername.equals(message.getRecipient().getUsername());

        if (!isSender && !isRecipient) {
            throw new IllegalArgumentException("User does not have permission to delete this message.");
        }

        if ("FOR_ALL".equalsIgnoreCase(deleteType)) {
            if (!isSender) {
                throw new IllegalArgumentException("Only the sender can recall a message.");
            }
            message.setIsRecalled(true);
            message.setContent(""); // Optional: clear content to save space and ensure privacy
            repository.save(message);

            MessageDTO messageDTO = mapper.toDTO(message);
            // Broadcast the recall event to both users
            sendNotifications(message.getRecipient().getUsername(), messageDTO);
            sendNotifications(message.getSender().getUsername(), messageDTO);

            return messageDTO;
        } else if ("FOR_ME".equalsIgnoreCase(deleteType)) {
            if (isSender) {
                message.setDeletedBySender(true);
            } else {
                message.setDeletedByRecipient(true);
            }
            repository.save(message);
            return mapper.toDTO(message);
        } else {
            throw new IllegalArgumentException("Invalid delete type.");
        }
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
        return users.map(user -> {
            UserDTO dto = userMapper.toDTO(user);
            repository.findTopBySenderUsernameAndRecipientUsernameOrSenderUsernameAndRecipientUsernameOrderBySendingTimeDesc(
                    curUsername, user.getUsername(), user.getUsername(), curUsername
            ).ifPresent(lastMsg -> {
                dto.setLastMessage(lastMsg.getContent());
                dto.setLastMessageType(lastMsg.getType() != null ? lastMsg.getType().name() : null);
                dto.setLastMessageIsRecalled(lastMsg.getIsRecalled());
                dto.setLastMessageTime(lastMsg.getSendingTime());
            });
            int unreadCount = repository.countUnreadMessages(curUsername, user.getUsername());
            dto.setUnreadCount(unreadCount);
            dto.setLastLogin(user.getLastLogin());
            return dto;
        });
    }

    @Override
    public MessageDTO sendRealtimeMessage(MessageDTO dto) {
        String tempId = dto.getTempId();
        MessageDTO messageDTO = super.create(dto);
        messageDTO.setTempId(tempId);

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
    @Async("taskExecutor")
    public void processAutoReplyIfNeeded(String senderUsername, String recipientUsername, Message lastMsgBeforeSending) {
        try {
            LocalDateTime now = LocalDateTime.now();

            if (lastMsgBeforeSending == null || !senderUsername.equals(lastMsgBeforeSending.getSender().getUsername())) {
                return;
            }

            long minutesSinceLastMsg = Duration.between(lastMsgBeforeSending.getSendingTime(), now).toMinutes();
            if (minutesSinceLastMsg <= Utils.AUTO_MESSAGE_TIMEOUT_MINUTES) {
                return;
            }

            User recipient = userService.loadUserByUsername(recipientUsername);

            if (recipient.getRole() != User.ERole.TEACHER) {
                return;
            }

            sendAutoMessage(senderUsername, recipientUsername, now);
        } catch (Exception e) {
            log.error("Failed to send auto-message via @Async", e);
        }
    }

    @Override
    public Message getLastMsg(String senderUsername, String recipientUsername) {
        return repository
                .findTopBySenderAndRecipientOrderBySendingTimeDesc(senderUsername, recipientUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Couldn't find the last message between " + recipientUsername + " and " + senderUsername));
    }
}
