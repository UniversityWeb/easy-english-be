package com.universityweb.category.mapper;

import com.universityweb.category.entity.Category;
import com.universityweb.category.response.CategoryResponse;
import com.universityweb.common.infrastructure.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper extends BaseMapper<Category, CategoryResponse> {
    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    CategoryResponse toDTO(Category entity);

    List<CategoryResponse> toDTOs(List<Category> entities);

    @Mapping(target = "courses", ignore = true)
    Category toEntity(CategoryResponse dto);

    List<Category> toEntities(List<CategoryResponse> dtos);
}
