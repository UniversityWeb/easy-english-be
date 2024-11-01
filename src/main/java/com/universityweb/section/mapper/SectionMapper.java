package com.universityweb.section.mapper;

import com.universityweb.common.infrastructure.BaseMapper;
import com.universityweb.section.entity.Section;
import com.universityweb.section.dto.SectionDTO;
import com.universityweb.section.request.SectionRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SectionMapper extends BaseMapper<Section, SectionDTO> {
    SectionMapper INSTANCE = Mappers.getMapper(SectionMapper.class);

    @Mapping(source = "course.id", target = "courseId")
    SectionDTO toDTO(Section entity);
    List<SectionDTO> toDTOs(List<Section> entities);

    SectionDTO toDTO(SectionRequest request);

    @Mapping(target = "course", ignore = true)
    @Mapping(target = "lessons", ignore = true)
    Section toEntity(SectionDTO dto);
    List<Section> toEntities(List<SectionDTO> dtos);

    @Mapping(target = "course", ignore = true)
    @Mapping(target = "lessons", ignore = true)
    Section toEntity(SectionRequest request);
}
