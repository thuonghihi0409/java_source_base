package com.example.demo.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.AdvancedQueryService;
import com.example.demo.service.FetchStrategyService;

@RestController
@RequestMapping("/api/advanced-queries")
public class AdvancedQueryController {

    @Autowired
    private AdvancedQueryService advancedQueryService;
    
    @Autowired
    private FetchStrategyService fetchStrategyService;

    // ========== TRANSACTIONAL ENDPOINTS ==========
    
    /**
     * Test transactional operations
     */
    @PostMapping("/test-transactional")
    public ResponseEntity<Map<String, String>> testTransactional() {
        Map<String, String> response = new HashMap<>();
        
        try {
            // Test 1: Simple transaction
            advancedQueryService.createCategory("Test Category", "Test Description");
            response.put("transaction", "Success");
            
            // Test 2: Complex transaction with rollback
            try {
                advancedQueryService.transferProductsBetweenCategories(1L, 1L, List.of(1L));
            } catch (Exception e) {
                response.put("rollback", "Success - Rollback as expected: " + e.getMessage());
            }
            
            // Test 3: Bulk update
            advancedQueryService.bulkUpdateProductPrices(1L, BigDecimal.valueOf(10));
            response.put("bulk_update", "Success");
            
        } catch (Exception e) {
            response.put("error", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }

    // ========== PAGINATION ENDPOINTS ==========
    
    /**
     * Test pagination
     */
    @GetMapping("/pagination/categories")
    public ResponseEntity<Page<?>> getCategoriesPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Page<?> result = advancedQueryService.getCategoriesPaginated(page, size, sortBy, sortDir);
        return ResponseEntity.ok(result);
    }
    
    /**
     * Test pagination with search
     */
    @GetMapping("/pagination/categories/search")
    public ResponseEntity<Page<?>> searchCategories(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        
        Page<?> result = advancedQueryService.searchCategories(keyword, page, size);
        return ResponseEntity.ok(result);
    }
    
    /**
     * Test complex pagination with filters
     */
    @GetMapping("/pagination/products/search")
    public ResponseEntity<Page<?>> searchProductsWithFilters(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Page<?> result = advancedQueryService.searchProductsWithFilters(
            keyword, categoryId, minPrice, maxPrice, page, size, sortBy, sortDir);
        return ResponseEntity.ok(result);
    }

    // ========== ADVANCED QUERY ENDPOINTS ==========
    
    /**
     * Test custom queries
     */
    @GetMapping("/queries/category-statistics")
    public ResponseEntity<List<Object[]>> getCategoryStatistics() {
        List<Object[]> result = advancedQueryService.getCategoryStatistics();
        return ResponseEntity.ok(result);
    }
    
    /**
     * Test native queries
     */
    @GetMapping("/queries/top-expensive-products")
    public ResponseEntity<List<Object[]>> getTopExpensiveProducts() {
        List<Object[]> result = advancedQueryService.getTopExpensiveProducts();
        return ResponseEntity.ok(result);
    }
    
    /**
     * Test named queries
     */
    @GetMapping("/queries/categories-created-after")
    public ResponseEntity<List<?>> getCategoriesCreatedAfter(@RequestParam String date) {
        LocalDateTime dateTime = LocalDateTime.parse(date);
        List<?> result = advancedQueryService.getCategoriesCreatedAfter(dateTime);
        return ResponseEntity.ok(result);
    }

    // ========== FETCH STRATEGY ENDPOINTS ==========
    
    /**
     * Test lazy loading
     */
    @GetMapping("/fetch-strategy/lazy-loading")
    public ResponseEntity<Map<String, String>> testLazyLoading() {
        Map<String, String> response = new HashMap<>();
        
        try {
            fetchStrategyService.demonstrateLazyLoading();
            response.put("lazy_loading", "Completed - Check console for details");
        } catch (Exception e) {
            response.put("error", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Test eager loading
     */
    @GetMapping("/fetch-strategy/eager-loading")
    public ResponseEntity<Map<String, String>> testEagerLoading() {
        Map<String, String> response = new HashMap<>();
        
        try {
            fetchStrategyService.demonstrateEagerLoadingWithJoinFetch();
            response.put("eager_loading", "Completed - Check console for details");
        } catch (Exception e) {
            response.put("error", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Test performance comparison
     */
    @GetMapping("/fetch-strategy/performance-comparison")
    public ResponseEntity<Map<String, String>> comparePerformance() {
        Map<String, String> response = new HashMap<>();
        
        try {
            fetchStrategyService.comparePerformance();
            response.put("performance_comparison", "Completed - Check console for details");
        } catch (Exception e) {
            response.put("error", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Test bulk eager loading
     */
    @GetMapping("/fetch-strategy/bulk-eager-loading")
    public ResponseEntity<Map<String, String>> testBulkEagerLoading() {
        Map<String, String> response = new HashMap<>();
        
        try {
            fetchStrategyService.demonstrateBulkEagerLoading();
            response.put("bulk_eager_loading", "Completed - Check console for details");
        } catch (Exception e) {
            response.put("error", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }

    // ========== COMPREHENSIVE TEST ENDPOINT ==========
    
    /**
     * Test all advanced query features
     */
    @GetMapping("/test-all")
    public ResponseEntity<Map<String, Object>> testAllFeatures() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Test pagination
            Page<?> categories = advancedQueryService.getCategoriesPaginated(0, 5, "name", "asc");
            response.put("pagination", "Success - " + categories.getTotalElements() + " categories");
            
            // Test statistics
            List<Object[]> stats = advancedQueryService.getCategoryStatistics();
            response.put("statistics", "Success - " + stats.size() + " category stats");
            
            // Test fetch strategies
            fetchStrategyService.demonstrateEagerLoadingWithJoinFetch();
            response.put("fetch_strategies", "Success - Check console");
            
            // Test complex search
            Page<?> products = advancedQueryService.searchProductsWithFilters(
                null, null, null, null, 0, 5, "name", "asc");
            response.put("complex_search", "Success - " + products.getTotalElements() + " products");
            
            response.put("overall", "All tests completed successfully!");
            
        } catch (Exception e) {
            response.put("error", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }

    // ========== UTILITY ENDPOINTS ==========
    
    /**
     * Get query examples documentation
     */
    @GetMapping("/examples")
    public ResponseEntity<Map<String, String>> getQueryExamples() {
        Map<String, String> examples = new HashMap<>();
        
        examples.put("pagination", "GET /api/advanced-queries/pagination/categories?page=0&size=5&sortBy=name&sortDir=asc");
        examples.put("search", "GET /api/advanced-queries/pagination/categories/search?keyword=test&page=0&size=5");
        examples.put("filters", "GET /api/advanced-queries/pagination/products/search?keyword=test&categoryId=1&minPrice=10&maxPrice=100");
        examples.put("statistics", "GET /api/advanced-queries/queries/category-statistics");
        examples.put("native_queries", "GET /api/advanced-queries/queries/top-expensive-products");
        examples.put("named_queries", "GET /api/advanced-queries/queries/categories-created-after?date=2024-01-01T00:00:00");
        examples.put("lazy_loading", "GET /api/advanced-queries/fetch-strategy/lazy-loading");
        examples.put("eager_loading", "GET /api/advanced-queries/fetch-strategy/eager-loading");
        examples.put("performance", "GET /api/advanced-queries/fetch-strategy/performance-comparison");
        examples.put("test_all", "GET /api/advanced-queries/test-all");
        
        return ResponseEntity.ok(examples);
    }
}
