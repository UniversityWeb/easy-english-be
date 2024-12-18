package com.universityweb.category.mapper;

import com.universityweb.category.entity.Category;
import com.universityweb.category.response.CategoryResponse;
import com.universityweb.common.infrastructure.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper extends BaseMapper<Category, CategoryResponse> {
    @Override
    CategoryResponse toDTO(Category entity);

    @Mapping(target = "courses", ignore = true)
    @Override
    Category toEntity(CategoryResponse dto);
}
