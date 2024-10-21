package com.universityweb.enrollment.mapper;

import com.universityweb.enrollment.entity.Enrollment;
import com.universityweb.enrollment.response.EnrollmentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EnrollmentMapper {
    EnrollmentMapper INSTANCE = Mappers.getMapper(EnrollmentMapper.class);

    EnrollmentResponse toDTO(Enrollment entity);

    List<EnrollmentResponse> toDTOs(List<Enrollment> entities);

    Enrollment toEntity(EnrollmentResponse dto);

    List<Enrollment> toEntities(List<EnrollmentResponse> dtos);
}
