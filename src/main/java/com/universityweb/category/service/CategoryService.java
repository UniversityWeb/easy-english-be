package com.universityweb.category.service;

import com.universityweb.category.entity.Category;
import com.universityweb.category.request.CategoryRequest;
import com.universityweb.category.response.CategoryResponse;
import com.universityweb.category.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public void createCategory(CategoryRequest categoryRequest) {
        Category category = new Category();
        category.setName(categoryRequest.getName());
        categoryRepository.save(category);
    }
    public void updateCategory(CategoryRequest categoryRequest) {
        Category currentCategory = categoryRepository
                .findById(categoryRequest.getId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        currentCategory.setName(categoryRequest.getName());
        categoryRepository.save(currentCategory);
    }

    public List<CategoryResponse> getAllCategory() {
        List<Category> categories = categoryRepository.findAll();
        List<CategoryResponse> categoryResponses = new ArrayList<>();
        categories.forEach(category -> {
            CategoryResponse categoryResponse = new CategoryResponse();
            categoryResponse.setId(category.getId());
            categoryResponse.setName(category.getName());
            categoryResponses.add(categoryResponse);
        });
        return categoryResponses;
    }
}
