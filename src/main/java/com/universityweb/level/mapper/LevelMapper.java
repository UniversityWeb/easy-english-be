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
    @Mapping(source = "topic.id", target = "topicId")
    @Override
    LevelResponse toDTO(Level entity);

    @Mapping(target = "topic", ignore = true)
    @Override
    Level toEntity(LevelResponse dto);

    @Mapping(target = "topic", ignore = true)
    Level toEntity(LevelRequest dto);
}
