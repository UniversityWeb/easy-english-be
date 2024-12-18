package com.universityweb.enrollment.mapper;

import com.universityweb.common.infrastructure.BaseMapper;
import com.universityweb.enrollment.dto.EnrollmentDTO;
import com.universityweb.enrollment.entity.Enrollment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EnrollmentMapper extends BaseMapper<Enrollment, EnrollmentDTO> {
}
