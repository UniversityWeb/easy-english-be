package com.universityweb.drip;

import com.universityweb.common.infrastructure.BaseMapper;
import com.universityweb.drip.dto.DripDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DripMapper extends BaseMapper<Drip, DripDTO> {
    @Mapping(source = "courseId", target = "courseId")
    DripDTO toDTO(Drip entity);

    Drip toEntity(DripDTO dto);
}
