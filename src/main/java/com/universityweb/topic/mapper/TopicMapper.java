package com.universityweb.topic.mapper;

import com.universityweb.common.infrastructure.BaseMapper;
import com.universityweb.topic.entity.Topic;
import com.universityweb.topic.response.TopicResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TopicMapper extends BaseMapper<Topic, TopicResponse> {
    @Override
    TopicResponse toDTO(Topic entity);

    @Mapping(target = "courses", ignore = true)
    @Mapping(target = "levels", ignore = true)
    @Override
    Topic toEntity(TopicResponse dto);
}
