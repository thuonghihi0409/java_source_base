package com.example.demo.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    // Tìm category theo tên
    Optional<Category> findByName(String name);
    
    // Kiểm tra category có tồn tại theo tên
    boolean existsByName(String name);
    
    // Tìm tất cả category đang active
    List<Category> findByIsActiveTrue();
    
    // Tìm category theo tên (case insensitive)
    Optional<Category> findByNameIgnoreCase(String name);
    
    // Tìm category có chứa từ khóa trong tên
    List<Category> findByNameContainingIgnoreCase(String keyword);
    
    // Custom query để lấy category với số lượng products
    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.products WHERE c.id = :id")
    Optional<Category> findByIdWithProducts(@Param("id") Long id);
    
    // Lấy tất cả category với products
    @Query("SELECT DISTINCT c FROM Category c LEFT JOIN FETCH c.products WHERE c.isActive = true")
    List<Category> findAllActiveWithProducts();
    
    // ========== ADVANCED QUERIES PRACTICE ==========
    
    // 1. Native Query - Đếm số products trong mỗi category
    @Query(value = "SELECT c.name, COUNT(p.id) as product_count FROM categories c " +
                   "LEFT JOIN products p ON c.id = p.category_id " +
                   "WHERE c.is_active = true " +
                   "GROUP BY c.id, c.name " +
                   "ORDER BY product_count DESC", nativeQuery = true)
    List<Object[]> findCategoryProductCounts();
    
    // 2. JPQL với aggregation - Tìm category có nhiều products nhất
    @Query("SELECT c.name, COUNT(p.id) FROM Category c " +
           "LEFT JOIN c.products p " +
           "WHERE c.isActive = true " +
           "GROUP BY c.id, c.name " +
           "HAVING COUNT(p.id) = (SELECT MAX(COUNT(p2.id)) FROM Category c2 LEFT JOIN c2.products p2 GROUP BY c2.id)")
    List<Object[]> findCategoryWithMostProducts();
    
    // 3. Subquery - Tìm categories có ít nhất 1 product
    @Query("SELECT c FROM Category c WHERE c.isActive = true AND " +
           "EXISTS (SELECT 1 FROM Product p WHERE p.category = c AND p.isActive = true)")
    List<Category> findCategoriesWithActiveProducts();
    
    // 4. Conditional query với CASE
    @Query("SELECT c.name, " +
           "CASE WHEN COUNT(p.id) = 0 THEN 'No Products' " +
           "     WHEN COUNT(p.id) <= 5 THEN 'Few Products' " +
           "     ELSE 'Many Products' END as status " +
           "FROM Category c LEFT JOIN c.products p " +
           "WHERE c.isActive = true " +
           "GROUP BY c.id, c.name")
    List<Object[]> findCategoryStatus();
    
    // 5. Named Query (sẽ định nghĩa trong Entity)
    @Query(name = "Category.findCategoriesCreatedAfter")
    List<Category> findCategoriesCreatedAfter(@Param("date") LocalDateTime date);
    
    // ========== PAGINATION & SORTING PRACTICE ==========
    
    // 1. Pagination cơ bản - Lấy categories với pagination
    Page<Category> findByIsActiveTrue(Pageable pageable);
    
    // 2. Pagination với sorting - Tìm categories theo tên với pagination
    Page<Category> findByNameContainingIgnoreCase(String keyword, Pageable pageable);
    
    // 3. Custom pagination query với JOIN FETCH
    @Query(value = "SELECT DISTINCT c FROM Category c LEFT JOIN FETCH c.products p WHERE c.isActive = true",
           countQuery = "SELECT COUNT(DISTINCT c) FROM Category c WHERE c.isActive = true")
    Page<Category> findActiveCategoriesWithProducts(Pageable pageable);
    
    // 4. Pagination với complex conditions
    @Query("SELECT c FROM Category c WHERE c.isActive = true " +
           "AND (:keyword IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:createdAfter IS NULL OR c.createdAt >= :createdAfter)")
    Page<Category> findCategoriesWithFilters(@Param("keyword") String keyword, 
                                           @Param("createdAfter") LocalDateTime createdAfter, 
                                           Pageable pageable);
}
