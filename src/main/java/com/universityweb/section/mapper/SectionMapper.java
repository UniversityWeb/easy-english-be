package com.universityweb.section.mapper;

import com.universityweb.common.infrastructure.BaseMapper;
import com.universityweb.section.dto.SectionDTO;
import com.universityweb.section.entity.Section;
import com.universityweb.section.request.SectionRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SectionMapper extends BaseMapper<Section, SectionDTO> {
    @Mapping(source = "courseId", target = "courseId")
    @Override
    SectionDTO toDTO(Section entity);

    SectionDTO toDTO(SectionRequest request);

    @Override
    Section toEntity(SectionDTO dto);

    Section toEntity(SectionRequest request);
}
