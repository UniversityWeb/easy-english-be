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
    TopicMapper INSTANCE = Mappers.getMapper(TopicMapper.class);

    TopicResponse toDTO(Topic entity);
    List<TopicResponse> toDTOs(List<Topic> entities);

    @Mapping(target = "courses", ignore = true)
    @Mapping(target = "levels", ignore = true)
    Topic toEntity(TopicResponse dto);
    List<Topic> toEntities(List<TopicResponse> dtos);
}
