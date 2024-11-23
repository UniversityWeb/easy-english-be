package com.universityweb.course.mapper;

import com.universityweb.common.infrastructure.BaseMapper;
import com.universityweb.course.entity.Course;
import com.universityweb.course.request.CourseRequest;
import com.universityweb.course.response.CourseResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CourseMapper extends BaseMapper<Course, CourseResponse> {
    @Mapping(source = "owner.username", target = "ownerUsername")
    @Mapping(target = "progress", ignore = true)
    @Mapping(target = "rating", ignore = true)
    @Mapping(target = "ratingCount", ignore = true)
    @Override
    CourseResponse toDTO(Course entity);

    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "sections", ignore = true)
    @Override
    Course toEntity(CourseResponse dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "price", ignore = true)
    void updateEntityFromDTO(CourseRequest dto, @MappingTarget Course entity);
}
