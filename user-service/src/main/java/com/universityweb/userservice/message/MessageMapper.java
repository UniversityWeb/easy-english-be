package com.universityweb.userservice.message;

import com.universityweb.common.infrastructure.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MessageMapper extends BaseMapper<Message, MessageDTO> {
    @Mapping(source = "sender.username", target = "senderUsername")
    @Mapping(source = "recipient.username", target = "recipientUsername")
    @Override
    MessageDTO toDTO(Message entity);

    @Mapping(source = "senderUsername", target = "sender.username")
    @Mapping(source = "recipientUsername", target = "recipient.username")
    @Override
    Message toEntity(MessageDTO dto);
}
