package com.universityweb.notification;

import com.universityweb.notification.model.Notification;
import com.universityweb.notification.model.NotificationDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    NotificationMapper INSTANCE = Mappers.getMapper(NotificationMapper.class);

    @Mapping(source = "user.username", target = "username")
    NotificationDTO toDTO(Notification notification);

    List<NotificationDTO> toDTOs(List<Notification> notifications);

    @Mapping(target = "user", ignore = true)
    Notification toEntity(NotificationDTO notificationDTO);

    List<Notification> toEntities(List<NotificationDTO> notificationDTOs);
}
