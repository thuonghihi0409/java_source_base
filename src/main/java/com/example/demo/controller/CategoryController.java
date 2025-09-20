package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.CategoryRequest;
import com.example.demo.dto.CategoryResponse;
import com.example.demo.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(@Valid @RequestBody CategoryRequest request) {
        try {
            CategoryResponse response = categoryService.createCategory(request);
            return ResponseEntity.ok(ApiResponse.success(response, "Category created successfully"));
        } catch (Exception e) {
            throw e; // Let GlobalExceptionHandler handle it
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(@PathVariable Long id) {
        CategoryResponse response = categoryService.getCategoryById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Category retrieved successfully"));
    }

    @GetMapping("/{id}/products")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryWithProducts(@PathVariable Long id) {
        CategoryResponse response = categoryService.getCategoryWithProducts(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Category with products retrieved successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories() {
        List<CategoryResponse> responses = categoryService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.success(responses, "Categories retrieved successfully"));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getActiveCategories() {
        List<CategoryResponse> responses = categoryService.getActiveCategories();
        return ResponseEntity.ok(ApiResponse.success(responses, "Active categories retrieved successfully"));
    }

    @GetMapping("/active/with-products")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getActiveCategoriesWithProducts() {
        List<CategoryResponse> responses = categoryService.getActiveCategoriesWithProducts();
        return ResponseEntity.ok(ApiResponse.success(responses, "Active categories with products retrieved successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @PathVariable Long id, 
            @Valid @RequestBody CategoryRequest request) {
        try {
            CategoryResponse response = categoryService.updateCategory(id, request);
            return ResponseEntity.ok(ApiResponse.success(response, "Category updated successfully"));
        } catch (Exception e) {
            throw e; // Let GlobalExceptionHandler handle it
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success("Category deleted successfully", "Category deleted successfully"));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<CategoryResponse>> deactivateCategory(@PathVariable Long id) {
        categoryService.deactivateCategory(id);
        CategoryResponse response = categoryService.getCategoryById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Category deactivated successfully"));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> searchCategories(@RequestParam String keyword) {
        List<CategoryResponse> responses = categoryService.searchCategories(keyword);
        return ResponseEntity.ok(ApiResponse.success(responses, "Search results retrieved successfully"));
    }
}
