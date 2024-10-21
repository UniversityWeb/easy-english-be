package com.universityweb.lesson.mapper;

import com.universityweb.lesson.entity.Lesson;
import com.universityweb.lesson.response.LessonResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LessonMapper {
    LessonMapper INSTANCE = Mappers.getMapper(LessonMapper.class);

    LessonResponse toDTO(Lesson entity);

    List<LessonResponse> toDTOs(List<Lesson> entities);

    Lesson toEntity(LessonResponse dto);

    List<Lesson> toEntities(List<LessonResponse> dtos);
}
