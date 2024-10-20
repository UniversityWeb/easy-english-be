package com.universityweb.course.category.mapper;

import com.universityweb.course.category.entity.Category;
import com.universityweb.course.category.response.CategoryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    CategoryResponse toDTO(Category entity);

    List<CategoryResponse> toDTOs(List<Category> entities);

    @Mapping(target = "courses", ignore = true)
    Category toEntity(CategoryResponse dto);

    List<Category> toEntities(List<CategoryResponse> dtos);
}
