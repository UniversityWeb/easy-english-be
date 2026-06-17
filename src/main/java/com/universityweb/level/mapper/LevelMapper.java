package com.universityweb.level.mapper;

import com.universityweb.common.infrastructure.BaseMapper;
import com.universityweb.level.entity.Level;
import com.universityweb.level.request.LevelRequest;
import com.universityweb.level.response.LevelResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LevelMapper extends BaseMapper<Level, LevelResponse> {
    @Mapping(source = "topicId", target = "topicId")
    @Override
    LevelResponse toDTO(Level entity);

    @Override
    Level toEntity(LevelResponse dto);

    Level toEntity(LevelRequest dto);
}
