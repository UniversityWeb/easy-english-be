package com.universityweb.bundle;

import com.universityweb.common.infrastructure.BaseMapper;
import com.universityweb.course.entity.Course;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface BundleMapper extends BaseMapper<Bundle, BundleDTO> {

    @Mapping(source = "courses", target = "courseIds")
    @Override
    BundleDTO toDTO(Bundle entity);

    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "courses", ignore = true)
    @Override
    Bundle toEntity(BundleDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "courses", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE) // Ignore null values
    void updateEntityFromDTO(BundleDTO dto, @MappingTarget Bundle entity);

    default List<Long> mapCoursesToIds(Set<Course> courses) {
        return courses.stream().map(Course::getId).collect(Collectors.toList());
    }
}
