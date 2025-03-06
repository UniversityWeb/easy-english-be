package com.universityweb.common.auth.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.universityweb.common.auth.dto.SettingsDTO;
import com.universityweb.common.auth.dto.UserDTO;
import com.universityweb.common.auth.dto.UserForAdminDTO;
import com.universityweb.common.auth.entity.User;
import com.universityweb.common.infrastructure.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper extends BaseMapper<User, UserDTO> {
    @Mapping(target = "settings", source = "settings", qualifiedByName = "jsonToSettingsDTO")
    UserDTO toDTO(User user);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "settings", source = "settings", qualifiedByName = "settingsDTOToJson")
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

    ObjectMapper objectMapper = new ObjectMapper();

    @Named("jsonToSettingsDTO")
    default SettingsDTO jsonToSettingsDTO(String settings) {
        try {
            return objectMapper.readValue(settings, SettingsDTO.class);
        } catch (Exception e) {
            return new SettingsDTO();
        }
    }

    @Named("settingsDTOToJson")
    default String settingsDTOToJson(SettingsDTO settingsDTO) {
        try {
            return objectMapper.writeValueAsString(settingsDTO);
        } catch (Exception e) {
            return "{}";
        }
    }
}
