package com.universityweb.price.mapper;

import com.universityweb.common.infrastructure.BaseMapper;
import com.universityweb.price.entity.Price;
import com.universityweb.price.response.PriceResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PriceMapper extends BaseMapper<Price, PriceResponse> {
    @Override
    PriceResponse toDTO(Price entity);

    @Mapping(target = "course", ignore = true)
    @Override
    Price toEntity(PriceResponse dto);
}
