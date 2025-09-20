package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.Category;
import com.example.demo.entity.Product;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ProductRepository;

@Service
@Transactional(readOnly = true)
public class FetchStrategyService {

    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private ProductRepository productRepository;

    // ========== LAZY vs EAGER LOADING EXAMPLES ==========
    
    /**
     * 1. LAZY LOADING - Mặc định trong entity
     * Khi load Category, products sẽ không được load ngay
     * Chỉ load khi truy cập products collection
     */
    public void demonstrateLazyLoading() {
        System.out.println("=== LAZY LOADING DEMO ===");
        
        // Load category với lazy products
        Category category = categoryRepository.findById(1L).orElse(null);
        if (category != null) {
            System.out.println("Category loaded: " + category.getName());
            
            // LazyInitializationException sẽ xảy ra nếu session đã đóng
            // Để tránh, cần @Transactional hoặc sử dụng JOIN FETCH
            try {
                List<Product> products = category.getProducts();
                System.out.println("Products count: " + products.size());
            } catch (Exception e) {
                System.out.println("LazyInitializationException: " + e.getMessage());
            }
        }
    }
    
    /**
     * 2. EAGER LOADING - Sử dụng JOIN FETCH
     * Load category cùng với products trong 1 query
     */
    public void demonstrateEagerLoadingWithJoinFetch() {
        System.out.println("=== EAGER LOADING WITH JOIN FETCH ===");
        
        // Sử dụng query có JOIN FETCH
        Category category = categoryRepository.findByIdWithProducts(1L).orElse(null);
        if (category != null) {
            System.out.println("Category loaded with products: " + category.getName());
            List<Product> products = category.getProducts();
            System.out.println("Products count: " + products.size());
            
            // Không có LazyInitializationException vì products đã được load
            for (Product product : products) {
                System.out.println("- Product: " + product.getName() + ", Price: " + product.getPrice());
            }
        }
    }
    
    /**
     * 3. EAGER LOADING - Load tất cả categories với products
     */
    public void demonstrateBulkEagerLoading() {
        System.out.println("=== BULK EAGER LOADING ===");
        
        // Load tất cả active categories với products
        List<Category> categories = categoryRepository.findAllActiveWithProducts();
        
        System.out.println("Total categories: " + categories.size());
        for (Category category : categories) {
            System.out.println("Category: " + category.getName() + 
                             " - Products: " + category.getProducts().size());
        }
    }
    
    /**
     * 4. Product với Category - Many-to-One relationship
     */
    public void demonstrateProductWithCategory() {
        System.out.println("=== PRODUCT WITH CATEGORY ===");
        
        // Load product với lazy category
        Product product = productRepository.findById(1L).orElse(null);
        if (product != null) {
            System.out.println("Product: " + product.getName());
            
            // Category sẽ được load lazy
            Category category = product.getCategory();
            System.out.println("Category: " + category.getName());
        }
    }
    
    /**
     * 5. Product với EAGER category loading
     */
    public void demonstrateProductWithEagerCategory() {
        System.out.println("=== PRODUCT WITH EAGER CATEGORY ===");
        
        // Load product với eager category
        Product product = productRepository.findByIdWithCategory(1L).orElse(null);
        if (product != null) {
            System.out.println("Product: " + product.getName());
            Category category = product.getCategory();
            System.out.println("Category: " + category.getName());
        }
    }
    
    /**
     * 6. Load tất cả products với categories
     */
    public void demonstrateAllProductsWithCategories() {
        System.out.println("=== ALL PRODUCTS WITH CATEGORIES ===");
        
        List<Product> products = productRepository.findAllActiveWithCategory();
        
        System.out.println("Total products: " + products.size());
        for (Product product : products) {
            System.out.println("Product: " + product.getName() + 
                             " - Category: " + product.getCategory().getName() +
                             " - Price: " + product.getPrice());
        }
    }
    
    // ========== PERFORMANCE COMPARISON ==========
    
    /**
     * 7. So sánh performance giữa Lazy và Eager
     */
    public void comparePerformance() {
        System.out.println("=== PERFORMANCE COMPARISON ===");
        
        // Test 1: Lazy loading - N+1 problem
        long startTime = System.currentTimeMillis();
        List<Category> lazyCategories = categoryRepository.findByIsActiveTrue();
        for (Category category : lazyCategories) {
            // Mỗi lần truy cập products sẽ tạo thêm 1 query (N+1 problem)
            try {
                category.getProducts().size();
            } catch (Exception e) {
                // LazyInitializationException
            }
        }
        long lazyTime = System.currentTimeMillis() - startTime;
        System.out.println("Lazy loading time: " + lazyTime + "ms (with N+1 problem)");
        
        // Test 2: Eager loading với JOIN FETCH
        startTime = System.currentTimeMillis();
        List<Category> eagerCategories = categoryRepository.findAllActiveWithProducts();
        for (Category category : eagerCategories) {
            category.getProducts().size(); // Không có N+1 problem
        }
        long eagerTime = System.currentTimeMillis() - startTime;
        System.out.println("Eager loading time: " + eagerTime + "ms (single query)");
        
        System.out.println("Performance improvement: " + 
                         (lazyTime > 0 ? (lazyTime - eagerTime) * 100 / lazyTime : 0) + "%");
    }
    
    // ========== BEST PRACTICES EXAMPLES ==========
    
    /**
     * 8. Khi nào nên dùng LAZY loading
     */
    public void demonstrateLazyLoadingBestPractice() {
        System.out.println("=== LAZY LOADING BEST PRACTICE ===");
        
        // Tốt cho: Chỉ cần thông tin cơ bản, không cần related data
        List<Category> categories = categoryRepository.findByIsActiveTrue();
        System.out.println("Categories list (no products needed):");
        for (Category category : categories) {
            System.out.println("- " + category.getName() + " (" + category.getDescription() + ")");
        }
    }
    
    /**
     * 9. Khi nào nên dùng EAGER loading
     */
    public void demonstrateEagerLoadingBestPractice() {
        System.out.println("=== EAGER LOADING BEST PRACTICE ===");
        
        // Tốt cho: Cần hiển thị category với danh sách products
        List<Category> categories = categoryRepository.findAllActiveWithProducts();
        System.out.println("Categories with products:");
        for (Category category : categories) {
            System.out.println("- " + category.getName() + " (" + 
                             category.getProducts().size() + " products)");
        }
    }
    
    /**
     * 10. Conditional loading - Chỉ load khi cần
     */
    public void demonstrateConditionalLoading() {
        System.out.println("=== CONDITIONAL LOADING ===");
        
        // Load category cơ bản trước
        Category category = categoryRepository.findById(1L).orElse(null);
        if (category != null) {
            System.out.println("Basic category info: " + category.getName());
            
            // Chỉ load products khi thực sự cần
            if (needsProductDetails()) {
                Category categoryWithProducts = categoryRepository.findByIdWithProducts(1L).orElse(null);
                if (categoryWithProducts != null) {
                    System.out.println("Products: " + categoryWithProducts.getProducts().size());
                }
            }
        }
    }
    
    private boolean needsProductDetails() {
        // Logic để quyết định có cần load products hay không
        return true; // Demo purpose
    }
}
