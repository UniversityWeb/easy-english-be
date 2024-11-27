package com.universityweb.message.service;

import com.universityweb.common.auth.dto.UserDTO;
import com.universityweb.common.auth.entity.User;
import com.universityweb.common.auth.mapper.UserMapper;
import com.universityweb.common.auth.service.user.UserService;
import com.universityweb.common.exception.CustomException;
import com.universityweb.common.infrastructure.service.BaseServiceImpl;
import com.universityweb.common.websocket.WebSocketConstants;
import com.universityweb.message.Message;
import com.universityweb.message.MessageDTO;
import com.universityweb.message.MessageMapper;
import com.universityweb.message.MessageRepos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class MessageServiceImpl
        extends BaseServiceImpl<Message, MessageDTO, UUID, MessageRepos, MessageMapper>
        implements MessageService{

    private final UserService userService;
    private final UserMapper userMapper;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    public MessageServiceImpl(
            MessageRepos repository,
            MessageMapper mapper,
            UserService userService,
            UserMapper userMapper,
            SimpMessagingTemplate simpMessagingTemplate
    ) {
        super(repository, mapper);
        this.userService = userService;
        this.userMapper = userMapper;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Override
    protected void throwNotFoundException(UUID id) {
        throw new CustomException("Couldn't find message with id: " + id);
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
    public void softDelete(UUID id) {
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
        String chatBoxOfRecipientTopic = WebSocketConstants.getMessageTopic(dto.getRecipientUsername());
        sendToTopic(chatBoxOfRecipientTopic, messageDTO);

        String recentChatsOfRecipientTopic = WebSocketConstants.getRecentChatsTopic(dto.getRecipientUsername());
        sendToTopic(recentChatsOfRecipientTopic, messageDTO);

        return messageDTO;
    }

    private void sendToTopic(String topic, MessageDTO message) {
        simpMessagingTemplate.convertAndSend(topic, message);
    }
}
