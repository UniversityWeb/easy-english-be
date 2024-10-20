package com.universityweb.course.faq.mapper;

import com.universityweb.course.faq.entity.FAQ;
import com.universityweb.course.faq.response.FAQResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FAQMapper {
    FAQMapper INSTANCE = Mappers.getMapper(FAQMapper.class);

    FAQResponse toDTO(FAQ entity);

    List<FAQResponse> toDTOs(List<FAQ> entities);

    @Mapping(target = "course", ignore = true)
    FAQ toEntity(FAQResponse dto);

    List<FAQ> toEntities(List<FAQResponse> dtos);
}