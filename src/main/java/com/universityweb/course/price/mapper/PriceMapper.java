package com.universityweb.course.price.mapper;

import com.universityweb.course.price.entity.Price;
import com.universityweb.course.price.response.PriceResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PriceMapper {
    PriceMapper INSTANCE = Mappers.getMapper(PriceMapper.class);

    PriceResponse toDTO(Price entity);
    List<PriceResponse> toDTOs(List<Price> entities);

    @Mapping(target = "course", ignore = true)
    Price toEntity(PriceResponse dto);
    List<Price> toEntities(List<PriceResponse> dtos);
}
