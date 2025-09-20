package com.example.demo.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.Category;
import com.example.demo.entity.Product;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ProductRepository;

@Service
@Transactional(readOnly = true) // Mặc định tất cả method là read-only
public class AdvancedQueryService {

    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private ProductRepository productRepository;

    // ========== TRANSACTIONAL EXAMPLES ==========
    
    /**
     * 1. @Transactional(readOnly = true) - Chỉ đọc dữ liệu
     */
    public List<Category> getAllActiveCategories() {
        return categoryRepository.findByIsActiveTrue();
    }
    
    /**
     * 2. @Transactional - Cho phép ghi dữ liệu
     */
    @Transactional(readOnly = false)
    public Category createCategory(String name, String description) {
        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        category.setIsActive(true);
        return categoryRepository.save(category);
    }
    
    /**
     * 3. @Transactional với rollback khi có exception
     */
    @Transactional(rollbackFor = Exception.class)
    public void transferProductsBetweenCategories(Long fromCategoryId, Long toCategoryId, List<Long> productIds) {
        // Validate from category exists
        categoryRepository.findById(fromCategoryId)
            .orElseThrow(() -> new RuntimeException("From category not found"));
        
        Category toCategory = categoryRepository.findById(toCategoryId)
            .orElseThrow(() -> new RuntimeException("To category not found"));
        
        // Transfer products
        for (Long productId : productIds) {
            Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));
            
            product.setCategory(toCategory);
            productRepository.save(product);
        }
        
        // Nếu có lỗi xảy ra ở đây, tất cả thay đổi sẽ được rollback
        if (fromCategoryId.equals(toCategoryId)) {
            throw new RuntimeException("Cannot transfer to same category");
        }
    }
    
    /**
     * 4. @Transactional với propagation REQUIRES_NEW
     * Tạo transaction mới độc lập
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logCategoryActivity(Long categoryId, String action) {
        // Log activity - luôn được commit ngay cả khi main transaction rollback
        System.out.println("Category " + categoryId + " - Action: " + action + " at " + LocalDateTime.now());
    }
    
    /**
     * 5. @Transactional với propagation REQUIRED (mặc định)
     * Tham gia vào transaction hiện tại hoặc tạo mới
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateProductStock(Long productId, Integer quantity) {
        // Validate product exists
        productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));
        
        int updatedRows = productRepository.updateProductStock(productId, quantity);
        if (updatedRows == 0) {
            throw new RuntimeException("Insufficient stock or product not found");
        }
    }
    
    /**
     * 6. Complex business operation với multiple transactions
     */
    @Transactional
    public void bulkUpdateProductPrices(Long categoryId, BigDecimal priceIncreasePercent) {
        // Validate category exists
        categoryRepository.findById(categoryId)
            .orElseThrow(() -> new RuntimeException("Category not found"));
        
        List<Product> products = productRepository.findByCategoryIdAndIsActiveTrue(categoryId);
        
        for (Product product : products) {
            BigDecimal newPrice = product.getPrice().multiply(
                BigDecimal.ONE.add(priceIncreasePercent.divide(BigDecimal.valueOf(100)))
            );
            product.setPrice(newPrice);
            productRepository.save(product);
            
            // Log activity trong transaction mới
            logCategoryActivity(categoryId, "Price updated for product: " + product.getName());
        }
    }
    
    /**
     * 7. @Transactional với timeout
     */
    @Transactional(timeout = 30) // Timeout sau 30 giây
    public void bulkDeleteInactiveProducts(LocalDateTime cutoffDate) {
        int deletedCount = productRepository.deleteInactiveOldProducts(cutoffDate);
        System.out.println("Deleted " + deletedCount + " inactive products");
    }
    
    // ========== PAGINATION EXAMPLES ==========
    
    /**
     * 8. Pagination cơ bản
     */
    public Page<Category> getCategoriesPaginated(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : 
            Sort.by(sortBy).ascending();
            
        Pageable pageable = PageRequest.of(page, size, sort);
        return categoryRepository.findByIsActiveTrue(pageable);
    }
    
    /**
     * 9. Pagination với search
     */
    public Page<Category> searchCategories(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        return categoryRepository.findByNameContainingIgnoreCase(keyword, pageable);
    }
    
    /**
     * 10. Complex pagination với filters
     */
    public Page<Product> searchProductsWithFilters(String keyword, Long categoryId, 
                                                 BigDecimal minPrice, BigDecimal maxPrice,
                                                 int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : 
            Sort.by(sortBy).ascending();
            
        Pageable pageable = PageRequest.of(page, size, sort);
        return productRepository.findProductsWithFiltersPaged(keyword, categoryId, minPrice, maxPrice, pageable);
    }
    
    // ========== ADVANCED QUERY EXAMPLES ==========
    
    /**
     * 11. Sử dụng custom queries
     */
    public List<Object[]> getCategoryStatistics() {
        return productRepository.findCategoryStatistics();
    }
    
    /**
     * 12. Sử dụng native queries
     */
    public List<Object[]> getTopExpensiveProducts() {
        return productRepository.findTop10MostExpensiveProducts();
    }
    
    /**
     * 13. Sử dụng named queries
     */
    public List<Category> getCategoriesCreatedAfter(LocalDateTime date) {
        return categoryRepository.findCategoriesCreatedAfter(date);
    }
}
