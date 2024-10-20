package com.universityweb.course.section.mapper;

import com.universityweb.course.section.entity.Section;
import com.universityweb.course.section.response.SectionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SectionMapper {
    SectionMapper INSTANCE = Mappers.getMapper(SectionMapper.class);

    SectionResponse toDTO(Section entity);
    List<SectionResponse> toDTOs(List<Section> entities);

    @Mapping(target = "lessons", ignore = true)
    @Mapping(target = "test", ignore = true)
    @Mapping(target = "course", ignore = true)
    @Mapping(target = "ordinalNumber", ignore = true)
    Section toEntity(SectionResponse dto);
    List<Section> toEntities(List<SectionResponse> dtos);
}
