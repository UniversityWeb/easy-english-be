package com.universityweb.courseservice.drip;

import com.universityweb.common.infrastructure.BaseMapper;
import com.universityweb.drip.dto.DripDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DripMapper extends BaseMapper<Drip, DripDTO> {
    @Mapping(source = "course.id", target = "courseId")
    DripDTO toDTO(Drip entity);

    @Mapping(target = "course", ignore = true)
    Drip toEntity(DripDTO dto);
}
