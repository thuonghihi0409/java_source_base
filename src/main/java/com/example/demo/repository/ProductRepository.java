package com.example.demo.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // Tìm products theo category
    List<Product> findByCategoryId(Long categoryId);
    
    // Tìm products đang active
    List<Product> findByIsActiveTrue();
    
    // Tìm products theo category và active
    List<Product> findByCategoryIdAndIsActiveTrue(Long categoryId);
    
    // Tìm product theo tên
    Optional<Product> findByName(String name);
    
    // Kiểm tra product có tồn tại theo tên
    boolean existsByName(String name);
    
    // Tìm products có chứa từ khóa trong tên
    List<Product> findByNameContainingIgnoreCase(String keyword);
    
    // Tìm products theo khoảng giá
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    // Tìm products có stock > 0
    List<Product> findByStockQuantityGreaterThan(Integer quantity);
    
    // Custom query để lấy product với category
    @Query("SELECT p FROM Product p JOIN FETCH p.category WHERE p.id = :id")
    Optional<Product> findByIdWithCategory(@Param("id") Long id);
    
    // Lấy tất cả products với category
    @Query("SELECT p FROM Product p JOIN FETCH p.category WHERE p.isActive = true")
    List<Product> findAllActiveWithCategory();
    
    // ========== ADVANCED QUERIES PRACTICE ==========
    
    // 1. Complex search với nhiều điều kiện
    @Query("SELECT p FROM Product p JOIN FETCH p.category c " +
           "WHERE p.isActive = true " +
           "AND (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:categoryId IS NULL OR c.id = :categoryId) " +
           "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
           "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +
           "AND (:inStock IS NULL OR (:inStock = true AND p.stockQuantity > 0) OR (:inStock = false AND p.stockQuantity = 0))")
    List<Product> findProductsWithFilters(@Param("keyword") String keyword, 
                                        @Param("categoryId") Long categoryId,
                                        @Param("minPrice") BigDecimal minPrice,
                                        @Param("maxPrice") BigDecimal maxPrice,
                                        @Param("inStock") Boolean inStock);
    
    // 2. Native Query - Top 10 products có giá cao nhất
    @Query(value = "SELECT p.*, c.name as category_name FROM products p " +
                   "JOIN categories c ON p.category_id = c.id " +
                   "WHERE p.is_active = true " +
                   "ORDER BY p.price DESC " +
                   "LIMIT 10", nativeQuery = true)
    List<Object[]> findTop10MostExpensiveProducts();
    
    // 3. JPQL với Window Functions (nếu database hỗ trợ)
    @Query("SELECT p.name, p.price, c.name, " +
           "ROW_NUMBER() OVER (PARTITION BY c.id ORDER BY p.price DESC) as price_rank " +
           "FROM Product p JOIN p.category c " +
           "WHERE p.isActive = true")
    List<Object[]> findProductPriceRankByCategory();
    
    // 4. Statistical queries
    @Query("SELECT c.name, " +
           "COUNT(p.id) as total_products, " +
           "AVG(p.price) as avg_price, " +
           "MIN(p.price) as min_price, " +
           "MAX(p.price) as max_price, " +
           "SUM(p.stockQuantity) as total_stock " +
           "FROM Product p JOIN p.category c " +
           "WHERE p.isActive = true " +
           "GROUP BY c.id, c.name " +
           "ORDER BY avg_price DESC")
    List<Object[]> findCategoryStatistics();
    
    // 5. Update query với @Modifying
    @Modifying
    @Query("UPDATE Product p SET p.stockQuantity = p.stockQuantity - :quantity WHERE p.id = :id AND p.stockQuantity >= :quantity")
    int updateProductStock(@Param("id") Long id, @Param("quantity") Integer quantity);
    
    // 6. Delete query với điều kiện
    @Modifying
    @Query("DELETE FROM Product p WHERE p.isActive = false AND p.updatedAt < :cutoffDate")
    int deleteInactiveOldProducts(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    // ========== PAGINATION & SORTING PRACTICE ==========
    
    // 1. Pagination cơ bản - Lấy products với pagination
    Page<Product> findByIsActiveTrue(Pageable pageable);
    
    // 2. Pagination theo category
    Page<Product> findByCategoryIdAndIsActiveTrue(Long categoryId, Pageable pageable);
    
    // 3. Pagination với complex search
    @Query(value = "SELECT p FROM Product p JOIN p.category c " +
                   "WHERE p.isActive = true " +
                   "AND (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
                   "AND (:categoryId IS NULL OR c.id = :categoryId) " +
                   "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
                   "AND (:maxPrice IS NULL OR p.price <= :maxPrice)",
           countQuery = "SELECT COUNT(p) FROM Product p JOIN p.category c " +
                       "WHERE p.isActive = true " +
                       "AND (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
                       "AND (:categoryId IS NULL OR c.id = :categoryId) " +
                       "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
                       "AND (:maxPrice IS NULL OR p.price <= :maxPrice)")
    Page<Product> findProductsWithFiltersPaged(@Param("keyword") String keyword, 
                                             @Param("categoryId") Long categoryId,
                                             @Param("minPrice") BigDecimal minPrice,
                                             @Param("maxPrice") BigDecimal maxPrice,
                                             Pageable pageable);
    
    // 4. Pagination với JOIN FETCH
    @Query(value = "SELECT p FROM Product p JOIN FETCH p.category c WHERE p.isActive = true",
           countQuery = "SELECT COUNT(p) FROM Product p WHERE p.isActive = true")
    Page<Product> findActiveProductsWithCategory(Pageable pageable);
    
    // 5. Native query với pagination
    @Query(value = "SELECT p.*, c.name as category_name FROM products p " +
                   "JOIN categories c ON p.category_id = c.id " +
                   "WHERE p.is_active = true " +
                   "ORDER BY p.price DESC",
           countQuery = "SELECT COUNT(*) FROM products p WHERE p.is_active = true",
           nativeQuery = true)
    Page<Object[]> findProductsWithCategoryInfo(Pageable pageable);
}
