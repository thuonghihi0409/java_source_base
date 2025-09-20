package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.ProductRequest;
import com.example.demo.dto.ProductResponse;
import com.example.demo.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@Valid @RequestBody ProductRequest request) {
        try {
            ProductResponse response = productService.createProduct(request);
            return ResponseEntity.ok(ApiResponse.success(response, "Product created successfully"));
        } catch (Exception e) {
            throw e; // Let GlobalExceptionHandler handle it
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable Long id) {
        ProductResponse response = productService.getProductById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Product retrieved successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllProducts() {
        List<ProductResponse> responses = productService.getAllProducts();
        return ResponseEntity.ok(ApiResponse.success(responses, "Products retrieved successfully"));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getProductsByCategory(@PathVariable Long categoryId) {
        List<ProductResponse> responses = productService.getProductsByCategory(categoryId);
        return ResponseEntity.ok(ApiResponse.success(responses, "Products by category retrieved successfully"));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getActiveProducts() {
        List<ProductResponse> responses = productService.getActiveProducts();
        return ResponseEntity.ok(ApiResponse.success(responses, "Active products retrieved successfully"));
    }

    @GetMapping("/in-stock")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getProductsInStock() {
        List<ProductResponse> responses = productService.getProductsInStock();
        return ResponseEntity.ok(ApiResponse.success(responses, "Products in stock retrieved successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable Long id, 
            @Valid @RequestBody ProductRequest request) {
        try {
            ProductResponse response = productService.updateProduct(id, request);
            return ResponseEntity.ok(ApiResponse.success(response, "Product updated successfully"));
        } catch (Exception e) {
            throw e; // Let GlobalExceptionHandler handle it
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success("Product deleted successfully", "Product deleted successfully"));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<ProductResponse>> deactivateProduct(@PathVariable Long id) {
        productService.deactivateProduct(id);
        ProductResponse response = productService.getProductById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Product deactivated successfully"));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> searchProducts(@RequestParam String keyword) {
        List<ProductResponse> responses = productService.searchProducts(keyword);
        return ResponseEntity.ok(ApiResponse.success(responses, "Search results retrieved successfully"));
    }

    @GetMapping("/price-range")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getProductsByPriceRange(
            @RequestParam BigDecimal minPrice, 
            @RequestParam BigDecimal maxPrice) {
        List<ProductResponse> responses = productService.getProductsByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(ApiResponse.success(responses, "Products by price range retrieved successfully"));
    }
}
