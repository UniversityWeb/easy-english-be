package com.universityweb.category.service;

import com.universityweb.category.CategoryRepository;
import com.universityweb.category.entity.Category;
import com.universityweb.category.mapper.CategoryMapper;
import com.universityweb.category.request.CategoryRequest;
import com.universityweb.category.response.CategoryResponse;
import com.universityweb.common.exception.ResourceNotFoundException;
import com.universityweb.common.infrastructure.service.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl
        extends BaseServiceImpl<Category, CategoryResponse, Long, CategoryRepository, CategoryMapper>
        implements CategoryService {

    @Autowired
    public CategoryServiceImpl(
            CategoryRepository repository,
            CategoryMapper categoryMapper
    ) {
        super(repository, categoryMapper);
    }

    public void createCategory(CategoryRequest categoryRequest) {
        Category category = new Category();
        category.setName(categoryRequest.getName());
        repository.save(category);
    }
    public void updateCategory(CategoryRequest categoryRequest) {
        Category currentCategory = getEntityById(categoryRequest.getId());
        currentCategory.setName(categoryRequest.getName());
        repository.save(currentCategory);
    }

    public List<CategoryResponse> getAllCategory() {
        List<Category> categories = repository.findAll();
        List<CategoryResponse> categoryResponses = new ArrayList<>();
        categories.forEach(category -> {
            CategoryResponse categoryResponse = new CategoryResponse();
            categoryResponse.setId(category.getId());
            categoryResponse.setName(category.getName());
            categoryResponses.add(categoryResponse);
        });
        return categoryResponses;
    }

    @Override
    protected void throwNotFoundException(Long id) {
        throw new ResourceNotFoundException("Category not found");
    }

    @Override
    public CategoryResponse update(Long id, CategoryResponse dto) {
        Category currentCategory = getEntityById(dto.getId());
        currentCategory.setName(dto.getName());
        return savedAndConvertToDTO(currentCategory);
    }

    @Override
    public void delete(Long id) {
        Category cate = getEntityById(id);
        cate.setIsDeleted(true);
        repository.save(cate);
    }
}
