package com.universityweb.section.mapper;

import com.universityweb.common.infrastructure.BaseMapper;
import com.universityweb.section.entity.Section;
import com.universityweb.section.response.SectionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SectionMapper extends BaseMapper<Section, SectionResponse> {
    SectionMapper INSTANCE = Mappers.getMapper(SectionMapper.class);

    @Mapping(source = "course.id", target = "courseId")
    SectionResponse toDTO(Section entity);
    List<SectionResponse> toDTOs(List<Section> entities);

    @Mapping(target = "course", ignore = true)
    @Mapping(target = "lessons", ignore = true)
    Section toEntity(SectionResponse dto);
    List<Section> toEntities(List<SectionResponse> dtos);
}
