package com.universityweb.level.mapper;

import com.universityweb.common.infrastructure.BaseMapper;
import com.universityweb.level.entity.Level;
import com.universityweb.level.request.LevelRequest;
import com.universityweb.level.response.LevelResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LevelMapper extends BaseMapper<Level, LevelResponse> {
    LevelMapper INSTANCE = Mappers.getMapper(LevelMapper.class);

    LevelResponse toDTO(Level entity);

    @Mapping(source = "topic.id", target = "orderId")
    List<LevelResponse> toDTOs(List<Level> entities);

    @Mapping(target = "topic", ignore = true)
    Level toEntity(LevelResponse dto);
    List<Level> toEntities(List<LevelResponse> dtos);

    @Mapping(target = "topic", ignore = true)
    Level toEntity(LevelRequest dto);
}
