package com.universityweb.category.controller;

import com.universityweb.category.entity.Category;
import com.universityweb.category.response.CategoryResponse;
import com.universityweb.category.service.CategoryService;
import com.universityweb.common.infrastructure.BaseController;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RequestMapping("/api/v2/categories")
@RestController
@Tag(name = "Categories v2")
public class CategoryControllerV2
        extends BaseController<Category, CategoryResponse, Long, CategoryService> {

    @Autowired
    public CategoryControllerV2(CategoryService service) {
        super(service);
    }
}
