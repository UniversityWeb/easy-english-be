package com.universityweb.courseservice.category.service;

import com.universityweb.category.entity.Category;
import com.universityweb.category.request.CategoryRequest;
import com.universityweb.category.response.CategoryResponse;
import com.universityweb.common.infrastructure.service.BaseService;

import java.util.List;

public interface CategoryService extends BaseService<Category, CategoryResponse, Long> {
    void createCategory(CategoryRequest categoryRequest);
    void updateCategory(CategoryRequest categoryRequest);
    List<CategoryResponse> getAllCategory();
}
