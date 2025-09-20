package com.example.demo.service;

import com.example.demo.dto.ProductRequest;
import com.example.demo.dto.ProductResponse;
import com.example.demo.entity.Category;
import com.example.demo.entity.Product;
import com.example.demo.exception.ConflictException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public ProductResponse createProduct(ProductRequest request) {
        // Kiểm tra product name đã tồn tại chưa
        if (productRepository.existsByName(request.getName())) {
            throw new ConflictException("Product with name '" + request.getName() + "' already exists");
        }

        // Tìm category
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));

        // Tạo product mới
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setIsActive(request.getIsActive());
        product.setCategory(category);

        Product savedProduct = productRepository.save(product);
        return convertToResponse(savedProduct);
    }

    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findByIdWithCategory(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        return convertToResponse(product);
    }

    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAllActiveWithCategory();
        return products.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> getProductsByCategory(Long categoryId) {
        List<Product> products = productRepository.findByCategoryIdAndIsActiveTrue(categoryId);
        return products.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> getActiveProducts() {
        List<Product> products = productRepository.findByIsActiveTrue();
        return products.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        // Kiểm tra tên mới có trùng với product khác không
        if (!product.getName().equals(request.getName()) && 
            productRepository.existsByName(request.getName())) {
            throw new ConflictException("Product with name '" + request.getName() + "' already exists");
        }

        // Tìm category mới nếu có thay đổi
        if (!product.getCategory().getId().equals(request.getCategoryId())) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));
            product.setCategory(category);
        }

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setIsActive(request.getIsActive());

        Product updatedProduct = productRepository.save(product);
        return convertToResponse(updatedProduct);
    }

    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        productRepository.delete(product);
    }

    public void deactivateProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        product.setIsActive(false);
        productRepository.save(product);
    }

    public List<ProductResponse> searchProducts(String keyword) {
        List<Product> products = productRepository.findByNameContainingIgnoreCase(keyword);
        return products.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        List<Product> products = productRepository.findByPriceBetween(minPrice, maxPrice);
        return products.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> getProductsInStock() {
        List<Product> products = productRepository.findByStockQuantityGreaterThan(0);
        return products.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Helper method
    private ProductResponse convertToResponse(Product product) {
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
