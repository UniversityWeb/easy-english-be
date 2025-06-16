package com.universityweb.message.service;

import com.universityweb.common.auth.dto.UserDTO;
import com.universityweb.common.infrastructure.service.BaseService;
import com.universityweb.message.Message;
import com.universityweb.message.MessageDTO;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.UUID;

public interface MessageService extends BaseService<Message, MessageDTO, UUID> {
    Page<MessageDTO> getAllMessages(String senderUsername, String recipientUsername, int page, int size);

    Page<UserDTO> getRecentChats(String curUsername, int page, int size);

    MessageDTO sendRealtimeMessage(MessageDTO dto);

    MessageDTO sendAutoMessage(String senderUsername, String recipientUsername, LocalDateTime sendingTime);

    Message getLastMsg(String senderUsername, String recipientUsername);
}
