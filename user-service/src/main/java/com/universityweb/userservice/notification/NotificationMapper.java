package com.universityweb.userservice.notification;

import com.universityweb.common.infrastructure.BaseMapper;
import com.universityweb.notification.entity.Notification;
import com.universityweb.notification.response.NotificationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper extends BaseMapper<Notification, NotificationResponse> {
    @Mapping(source = "user.username", target = "username")
    @Override
    NotificationResponse toDTO(Notification notification);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Override
    Notification toEntity(NotificationResponse notificationDTO);
}
