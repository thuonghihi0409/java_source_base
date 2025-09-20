# 🚀 HƯỚNG DẪN THỰC HÀNH TRUY VẤN NÂNG CAO SPRING BOOT

## 📚 Tổng quan

Đây là hướng dẫn thực hành chi tiết cho phần **Truy vấn nâng cao** trong kế hoạch học tập Spring Boot của bạn. Chúng ta sẽ thực hành với project hiện tại.

## 🎯 Các phần đã được thực hiện

### ✅ 1. @Query và @NamedQuery
- **Custom JPQL Queries**: Các query phức tạp với JOIN, subquery, aggregation
- **Native SQL Queries**: Sử dụng SQL thuần để tối ưu performance
- **Named Queries**: Định nghĩa query trong Entity để tái sử dụng

### ✅ 2. Pagination và Sorting
- **Spring Data Pagination**: Sử dụng `Pageable` và `Page`
- **Dynamic Sorting**: Sort theo nhiều field khác nhau
- **Custom Pagination**: Pagination với custom queries

### ✅ 3. @Transactional
- **Transaction Management**: Các loại transaction khác nhau
- **Rollback Strategies**: Xử lý rollback khi có lỗi
- **Propagation Types**: REQUIRED, REQUIRES_NEW, etc.

### ✅ 4. Lazy vs Eager Loading
- **Fetch Strategies**: LAZY, EAGER, JOIN FETCH
- **N+1 Problem**: Hiểu và giải quyết N+1 queries
- **Performance Optimization**: Tối ưu performance với fetch strategies

## 🛠️ Cách thực hành

### Bước 1: Khởi động ứng dụng
```bash
cd C:\LVT\demo
mvn spring-boot:run
```

### Bước 2: Test các API endpoints

#### 🔍 **Test Pagination**
```bash
# Pagination cơ bản
curl "http://localhost:8080/api/advanced-queries/pagination/categories?page=0&size=5&sortBy=name&sortDir=asc"

# Pagination với search
curl "http://localhost:8080/api/advanced-queries/pagination/categories/search?keyword=test&page=0&size=5"

# Complex pagination với filters
curl "http://localhost:8080/api/advanced-queries/pagination/products/search?keyword=test&categoryId=1&minPrice=10&maxPrice=100&page=0&size=10"
```

#### 📊 **Test Advanced Queries**
```bash
# Category statistics
curl "http://localhost:8080/api/advanced-queries/queries/category-statistics"

# Top expensive products (native query)
curl "http://localhost:8080/api/advanced-queries/queries/top-expensive-products"

# Categories created after date (named query)
curl "http://localhost:8080/api/advanced-queries/queries/categories-created-after?date=2024-01-01T00:00:00"
```

#### ⚡ **Test Fetch Strategies**
```bash
# Lazy loading demo
curl "http://localhost:8080/api/advanced-queries/fetch-strategy/lazy-loading"

# Eager loading demo
curl "http://localhost:8080/api/advanced-queries/fetch-strategy/eager-loading"

# Performance comparison
curl "http://localhost:8080/api/advanced-queries/fetch-strategy/performance-comparison"

# Bulk eager loading
curl "http://localhost:8080/api/advanced-queries/fetch-strategy/bulk-eager-loading"
```

#### 🔄 **Test Transactional Operations**
```bash
# Test all transactional features
curl -X POST "http://localhost:8080/api/advanced-queries/test-transactional"
```

#### 🧪 **Test Everything**
```bash
# Test all features at once
curl "http://localhost:8080/api/advanced-queries/test-all"

# Get all available examples
curl "http://localhost:8080/api/advanced-queries/examples"
```

## 📖 Kiến thức cần nắm vững

### 1. **@Query Annotation**
```java
// JPQL Query
@Query("SELECT c FROM Category c WHERE c.isActive = true")
List<Category> findActiveCategories();

// Native Query
@Query(value = "SELECT * FROM categories WHERE is_active = true", nativeQuery = true)
List<Category> findActiveCategoriesNative();

// Query với parameters
@Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice")
List<Product> findByPriceRange(@Param("minPrice") BigDecimal minPrice, 
                              @Param("maxPrice") BigDecimal maxPrice);
```

### 2. **Pagination**
```java
// Repository method
Page<Category> findByIsActiveTrue(Pageable pageable);

// Service usage
Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());
Page<Category> categories = categoryRepository.findByIsActiveTrue(pageable);
```

### 3. **@Transactional**
```java
@Transactional(readOnly = true)  // Chỉ đọc
@Transactional(rollbackFor = Exception.class)  // Rollback khi có exception
@Transactional(propagation = Propagation.REQUIRES_NEW)  // Transaction mới
@Transactional(timeout = 30)  // Timeout sau 30 giây
```

### 4. **Fetch Strategies**
```java
// Entity level
@OneToMany(fetch = FetchType.LAZY)  // Mặc định
@OneToMany(fetch = FetchType.EAGER)

// Query level với JOIN FETCH
@Query("SELECT c FROM Category c LEFT JOIN FETCH c.products")
List<Category> findAllWithProducts();
```

## 🎯 Bài tập thực hành

### Bài tập 1: Tạo custom query
Tạo một query để tìm tất cả products có giá cao hơn giá trung bình của category đó.

### Bài tập 2: Pagination với complex search
Tạo một API endpoint để search products với nhiều filters và pagination.

### Bài tập 3: Transaction management
Tạo một method để transfer products giữa các categories với rollback khi có lỗi.

### Bài tập 4: Performance optimization
So sánh performance giữa lazy loading và eager loading với dataset lớn.

## 📝 Checklist ôn tập

- [ ] Hiểu được sự khác biệt giữa JPQL và Native SQL
- [ ] Biết cách sử dụng @Query với parameters
- [ ] Thành thạo pagination và sorting
- [ ] Hiểu các loại @Transactional và khi nào sử dụng
- [ ] Nắm vững Lazy vs Eager loading
- [ ] Biết cách tránh N+1 problem
- [ ] Có thể tối ưu performance queries
- [ ] Thực hành với các API endpoints đã tạo

## 🔗 Tài liệu tham khảo

1. **Spring Data JPA Documentation**: https://docs.spring.io/spring-data/jpa/docs/current/reference/html/
2. **JPQL Reference**: https://docs.oracle.com/javaee/6/tutorial/doc/bnbtg.html
3. **Hibernate Fetch Strategies**: https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html#fetching

## 🚀 Bước tiếp theo

Sau khi hoàn thành phần này, bạn có thể chuyển sang **Tuần 4: Security và Testing** trong kế hoạch học tập của mình.

---

**Lưu ý**: Hãy thực hành nhiều lần và thử nghiệm với các query khác nhau để nắm vững kiến thức!
