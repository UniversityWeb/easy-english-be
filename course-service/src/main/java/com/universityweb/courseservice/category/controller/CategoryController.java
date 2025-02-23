package com.universityweb.courseservice.category.controller;

import com.universityweb.category.request.CategoryRequest;
import com.universityweb.category.service.CategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RequestMapping("/api/v1/category")
@RestController
@Tag(name = "Categories")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    @PostMapping("/create-category")
    public ResponseEntity<String> createCategory(@RequestBody CategoryRequest categoryRequest) {
        categoryService.createCategory(categoryRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("Category added successfully");
    }
    @PostMapping("/update-category")
    public ResponseEntity<String> updateCategory(@RequestBody CategoryRequest categoryRequest) {
        categoryService.updateCategory(categoryRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("Category updated successfully");
    }
    @PostMapping("/get-all-category")
    public ResponseEntity<?> getAllCategory() {
        return ResponseEntity.ok(categoryService.getAllCategory());
    }
}
