package com.universityweb.course.level.mapper;

import com.universityweb.course.level.entity.Level;
import com.universityweb.course.level.response.LevelResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LevelMapper {
    LevelMapper INSTANCE = Mappers.getMapper(LevelMapper.class);

    LevelResponse toDTO(Level entity);

    @Mapping(source = "topic.id", target = "orderId")
    List<LevelResponse> toDTOs(List<Level> entities);

    @Mapping(target = "topic", ignore = true)
    Level toEntity(LevelResponse dto);
    List<Level> toEntities(List<LevelResponse> dtos);
}
