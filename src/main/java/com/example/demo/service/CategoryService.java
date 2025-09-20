package com.example.demo.service;

import com.example.demo.dto.CategoryRequest;
import com.example.demo.dto.CategoryResponse;
import com.example.demo.dto.ProductResponse;
import com.example.demo.entity.Category;
import com.example.demo.entity.Product;
import com.example.demo.exception.ConflictException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public CategoryResponse createCategory(CategoryRequest request) {
        // Kiểm tra category name đã tồn tại chưa
        if (categoryRepository.existsByName(request.getName())) {
            throw new ConflictException("Category with name '" + request.getName() + "' already exists");
        }

        // Tạo category mới
        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setIsActive(request.getIsActive());

        Category savedCategory = categoryRepository.save(category);
        return convertToResponse(savedCategory);
    }

    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        return convertToResponse(category);
    }

    public CategoryResponse getCategoryWithProducts(Long id) {
        Category category = categoryRepository.findByIdWithProducts(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        return convertToResponseWithProducts(category);
    }

    public List<CategoryResponse> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<CategoryResponse> getActiveCategories() {
        List<Category> categories = categoryRepository.findByIsActiveTrue();
        return categories.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<CategoryResponse> getActiveCategoriesWithProducts() {
        List<Category> categories = categoryRepository.findAllActiveWithProducts();
        return categories.stream()
                .map(this::convertToResponseWithProducts)
                .collect(Collectors.toList());
    }

    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        // Kiểm tra tên mới có trùng với category khác không
        if (!category.getName().equals(request.getName()) && 
            categoryRepository.existsByName(request.getName())) {
            throw new ConflictException("Category with name '" + request.getName() + "' already exists");
        }

        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setIsActive(request.getIsActive());

        Category updatedCategory = categoryRepository.save(category);
        return convertToResponse(updatedCategory);
    }

    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        // Kiểm tra category có products không
        if (!category.getProducts().isEmpty()) {
            throw new ConflictException("Cannot delete category with existing products");
        }

        categoryRepository.delete(category);
    }

    public void deactivateCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        category.setIsActive(false);
        categoryRepository.save(category);
    }

    public List<CategoryResponse> searchCategories(String keyword) {
        List<Category> categories = categoryRepository.findByNameContainingIgnoreCase(keyword);
        return categories.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Helper methods
    private CategoryResponse convertToResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getIsActive(),
                category.getCreatedAt(),
                category.getUpdatedAt(),
                category.getProducts() != null ? category.getProducts().size() : 0
        );
    }

    private CategoryResponse convertToResponseWithProducts(Category category) {
        CategoryResponse response = convertToResponse(category);
        
        if (category.getProducts() != null) {
            List<ProductResponse> productResponses = category.getProducts().stream()
                    .map(this::convertProductToResponse)
                    .collect(Collectors.toList());
            response.setProducts(productResponses);
        }
        
        return response;
    }

    private ProductResponse convertProductToResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getIsActive(),
                product.getCreatedAt(),
                product.getUpdatedAt(),
                product.getCategory().getId(),
                product.getCategory().getName()
        );
    }
}
