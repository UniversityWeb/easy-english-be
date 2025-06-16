package com.universityweb.section.mapper;

import com.universityweb.common.infrastructure.BaseMapper;
import com.universityweb.section.dto.SectionDTO;
import com.universityweb.section.entity.Section;
import com.universityweb.section.request.SectionRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SectionMapper extends BaseMapper<Section, SectionDTO> {
    @Mapping(source = "course.id", target = "courseId")
    @Override
    SectionDTO toDTO(Section entity);

    SectionDTO toDTO(SectionRequest request);

    @Mapping(target = "course", ignore = true)
    @Mapping(target = "lessons", ignore = true)
    @Override
    Section toEntity(SectionDTO dto);

    @Mapping(target = "course", ignore = true)
    @Mapping(target = "lessons", ignore = true)
    Section toEntity(SectionRequest request);
}
