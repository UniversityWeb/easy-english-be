package com.universityweb.lesson.mapper;

import com.universityweb.common.infrastructure.BaseMapper;
import com.universityweb.lesson.entity.Lesson;
import com.universityweb.lesson.response.LessonResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LessonMapper extends BaseMapper<Lesson, LessonResponse> {
    @Mapping(source = "section.id", target = "sectionId")
    LessonResponse toDTO(Lesson entity);

    @Mapping(target = "section", ignore = true)
    Lesson toEntity(LessonResponse dto);
}
