package com.universityweb.userservice.user;

import com.universityweb.userservice.user.dto.UserForAdminDTO;
import com.universityweb.userservice.user.entity.User;
import com.universityweb.userservice.user.dto.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.bind.annotation.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper extends BaseMapper<User, UserDTO> {
    UserDTO toDTO(User user);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "username", ignore = true)
    User toEntity(UserDTO userDTO);

    User toEntity(UserForAdminDTO userForAdminDTO);

    @Mapping(target = "username", ignore = true)
    @Mapping(target = "password", ignore = true)
    void updateEntityFromDTO(UserForAdminDTO dto, @MappingTarget User entity);

    @Mapping(target = "password", ignore = true)
    UserForAdminDTO toUserForAdminDTO(User user);

    List<UserForAdminDTO> toForAdminDTOs(List<User> entities);

    default Page<UserForAdminDTO> mapPageToPageForAdminDTO(Page<User> page) {
        List<UserForAdminDTO> dtos = toForAdminDTOs(page.getContent());
        return new PageImpl<>(dtos, page.getPageable(), page.getTotalElements());
    }
}
