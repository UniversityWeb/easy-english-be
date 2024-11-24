package com.universityweb.lessontracker;

import com.universityweb.common.infrastructure.BaseMapper;
import com.universityweb.lessontracker.dto.LessonTrackerDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LessonTrackerMapper extends BaseMapper<LessonTracker, LessonTrackerDTO> {
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "lesson.id", target = "lessonId")
    @Override
    LessonTrackerDTO toDTO(LessonTracker entity);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "lesson", ignore = true)
    @Override
    LessonTracker toEntity(LessonTrackerDTO dto);
}
