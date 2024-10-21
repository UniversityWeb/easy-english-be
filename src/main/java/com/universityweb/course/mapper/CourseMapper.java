package com.universityweb.course.mapper;

import com.universityweb.course.entity.Course;
import com.universityweb.course.response.CourseResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CourseMapper {
    CourseMapper INSTANCE = Mappers.getMapper(CourseMapper.class);

    @Mapping(source = "owner.username", target = "ownerUsername")
    @Mapping(target = "progress", ignore = true)
    @Mapping(target = "rating", ignore = true)
    @Mapping(target = "ratingCount", ignore = true)
    CourseResponse toDTO(Course entity);

    List<CourseResponse> toDTOs(List<Course> entities);

    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "sections", ignore = true)
    Course toEntity(CourseResponse dto);

    List<Course> toEntities(List<CourseResponse> dtos);
}
