package com.universityweb.common.auth.mapper;

import com.universityweb.common.auth.dto.UserDTO;
import com.universityweb.common.auth.entity.User;
import com.universityweb.common.infrastructure.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper extends BaseMapper<User, UserDTO> {
    UserDTO toDTO(User user);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "status", ignore = true)
    User toEntity(UserDTO userDTO);
}
