package com.universityweb.lesson.mapper;

import com.universityweb.common.infrastructure.BaseMapper;
import com.universityweb.lesson.entity.Lesson;
import com.universityweb.lesson.response.LessonResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface LessonMapper extends BaseMapper<Lesson, LessonResponse> {
    @Mapping(source = "section.id", target = "sectionId")
    LessonResponse toDTO(Lesson entity);

    @Mapping(target = "section", ignore = true)
    Lesson toEntity(LessonResponse dto);

//    @Named("toLockedDTO")
//    @BeanMapping(ignoreByDefault = true)
//    @Mapping(source = "lesson.id", target = "id")
//    @Mapping(source = "lesson.title", target = "title")
//    @Mapping(source = "lesson.section.id", target = "sectionId")
//    @Mapping(target = "isLocked", constant = "true")
//    LessonResponse toLockedDTO(Lesson lesson);

    // Conditional mapping method
    default LessonResponse toDTOBasedOnIsLocked(boolean isLocked, Lesson lesson) {
//        if (isLocked) {
//            LessonResponse lockedDTO = toDTO(lesson);
//            lockedDTO.setLocked(true);
//            return lockedDTO;
//        } else {
//            LessonResponse fullDTO = toDTO(lesson);
//            fullDTO.setLocked(false);
//            return fullDTO;
//        }

        LessonResponse dto = toDTO(lesson);
        dto.setLocked(isLocked);
        return dto;
    }
}
