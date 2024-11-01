package com.universityweb.enrollment.mapper;

import com.universityweb.common.infrastructure.BaseMapper;
import com.universityweb.enrollment.dto.EnrollmentDTO;
import com.universityweb.enrollment.entity.Enrollment;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EnrollmentMapper extends BaseMapper<Enrollment, EnrollmentDTO> {
    EnrollmentMapper INSTANCE = Mappers.getMapper(EnrollmentMapper.class);

    EnrollmentDTO toDTO(Enrollment entity);

    List<EnrollmentDTO> toDTOs(List<Enrollment> entities);

    Enrollment toEntity(EnrollmentDTO dto);

    List<Enrollment> toEntities(List<EnrollmentDTO> dtos);
}
