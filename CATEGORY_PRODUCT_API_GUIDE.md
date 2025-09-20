# Category-Product API Guide

## 🏗️ **Database Relationship**

### **One-to-Many Relationship**
- **1 Category** ↔ **Many Products**
- **1 Product** ↔ **1 Category**

```
Category (1) -----> (Many) Product
    |                    |
    id               category_id (FK)
    name                 name
    description          description
    is_active            price
    created_at           stock_quantity
    updated_at           is_active
                        created_at
                        updated_at
```

## 📋 **Category API Endpoints**

### **1. Create Category**
```bash
POST /api/categories
Content-Type: application/json

{
    "name": "Electronics",
    "description": "Electronic devices and gadgets",
    "isActive": true
}
```

### **2. Get All Categories**
```bash
GET /api/categories
```

### **3. Get Active Categories**
```bash
GET /api/categories/active
```

### **4. Get Category by ID**
```bash
GET /api/categories/{id}
```

### **5. Get Category with Products**
```bash
GET /api/categories/{id}/products
```

### **6. Get Active Categories with Products**
```bash
GET /api/categories/active/with-products
```

### **7. Update Category**
```bash
PUT /api/categories/{id}
Content-Type: application/json

{
    "name": "Updated Electronics",
    "description": "Updated description",
    "isActive": true
}
```

### **8. Delete Category**
```bash
DELETE /api/categories/{id}
```

### **9. Deactivate Category**
```bash
PATCH /api/categories/{id}/deactivate
```

### **10. Search Categories**
```bash
GET /api/categories/search?keyword=electronics
```

## 📦 **Product API Endpoints**

### **1. Create Product**
```bash
POST /api/products
Content-Type: application/json

{
    "name": "iPhone 15",
    "description": "Latest iPhone model",
    "price": 999.99,
    "stockQuantity": 50,
    "categoryId": 1,
    "isActive": true
}
```

### **2. Get All Products**
```bash
GET /api/products
```

### **3. Get Product by ID**
```bash
GET /api/products/{id}
```

### **4. Get Products by Category**
```bash
GET /api/products/category/{categoryId}
```

### **5. Get Active Products**
```bash
GET /api/products/active
```

### **6. Get Products in Stock**
```bash
GET /api/products/in-stock
```

### **7. Update Product**
```bash
PUT /api/products/{id}
Content-Type: application/json

{
    "name": "iPhone 15 Pro",
    "description": "Updated description",
    "price": 1099.99,
    "stockQuantity": 30,
    "categoryId": 1,
    "isActive": true
}
```

### **8. Delete Product**
```bash
DELETE /api/products/{id}
```

### **9. Deactivate Product**
```bash
PATCH /api/products/{id}/deactivate
```

### **10. Search Products**
```bash
GET /api/products/search?keyword=iphone
```

### **11. Get Products by Price Range**
```bash
GET /api/products/price-range?minPrice=100&maxPrice=1000
```

## 🧪 **Test Scenarios**

### **Scenario 1: Create Category and Products**
```bash
# 1. Create Electronics category
curl -X POST http://localhost:8080/api/categories \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Electronics",
    "description": "Electronic devices",
    "isActive": true
  }'

# 2. Create iPhone product
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "iPhone 15",
    "description": "Latest iPhone",
    "price": 999.99,
    "stockQuantity": 50,
    "categoryId": 1,
    "isActive": true
  }'

# 3. Create Samsung product
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Samsung Galaxy S24",
    "description": "Latest Samsung phone",
    "price": 899.99,
    "stockQuantity": 30,
    "categoryId": 1,
    "isActive": true
  }'
```

### **Scenario 2: Get Category with Products**
```bash
# Get Electronics category with all its products
curl http://localhost:8080/api/categories/1/products
```

### **Scenario 3: Search and Filter**
```bash
# Search products by keyword
curl "http://localhost:8080/api/products/search?keyword=iphone"

# Get products by price range
curl "http://localhost:8080/api/products/price-range?minPrice=500&maxPrice=1200"

# Get products in stock
curl http://localhost:8080/api/products/in-stock
```

## 📊 **Response Examples**

### **Category Response**
```json
{
  "success": true,
  "message": "Category retrieved successfully",
  "data": {
    "id": 1,
    "name": "Electronics",
    "description": "Electronic devices",
    "isActive": true,
    "createdAt": "2024-01-01T10:00:00",
    "updatedAt": "2024-01-01T10:00:00",
    "productCount": 2,
    "products": [
      {
        "id": 1,
        "name": "iPhone 15",
        "description": "Latest iPhone",
        "price": 999.99,
        "stockQuantity": 50,
        "isActive": true,
        "createdAt": "2024-01-01T10:05:00",
        "updatedAt": "2024-01-01T10:05:00",
        "categoryId": 1,
        "categoryName": "Electronics"
      }
    ]
  },
  "timestamp": "2024-01-01T10:00:00"
}
```

### **Product Response**
```json
{
  "success": true,
  "message": "Product retrieved successfully",
  "data": {
    "id": 1,
    "name": "iPhone 15",
    "description": "Latest iPhone",
    "price": 999.99,
    "stockQuantity": 50,
    "isActive": true,
    "createdAt": "2024-01-01T10:05:00",
    "updatedAt": "2024-01-01T10:05:00",
    "categoryId": 1,
    "categoryName": "Electronics"
  },
  "timestamp": "2024-01-01T10:00:00"
}
```

## 🔒 **Security & Validation**

### **Validation Rules**

#### **Category:**
- `name`: Required, 2-100 characters, unique
- `description`: Optional, max 500 characters
- `isActive`: Boolean, default true

#### **Product:**
- `name`: Required, 2-200 characters, unique
- `description`: Optional, max 1000 characters
- `price`: Required, BigDecimal
- `stockQuantity`: Integer, default 0
- `categoryId`: Required, must exist
- `isActive`: Boolean, default true

### **Business Rules**
1. **Cannot delete category** with existing products
2. **Cannot create product** with non-existent category
3. **Unique names** for both categories and products
4. **Soft delete** by deactivating instead of hard delete

## 🚨 **Error Handling**

### **Common Errors**

#### **1. Category Not Found (404)**
```json
{
  "success": false,
  "message": "Category not found with id: 999",
  "data": null,
  "timestamp": "2024-01-01T10:00:00"
}
```

#### **2. Category Name Conflict (409)**
```json
{
  "success": false,
  "message": "Category with name 'Electronics' already exists",
  "data": null,
  "timestamp": "2024-01-01T10:00:00"
}
```

#### **3. Cannot Delete Category with Products (409)**
```json
{
  "success": false,
  "message": "Cannot delete category with existing products",
  "data": null,
  "timestamp": "2024-01-01T10:00:00"
}
```

#### **4. Validation Error (400)**
```json
{
  "success": false,
  "message": "Validation failed",
  "data": {
    "name": "Product name is required",
    "price": "Price is required"
  },
  "timestamp": "2024-01-01T10:00:00"
}
```

## 🎯 **Best Practices**

### **1. API Usage**
- Always check if category exists before creating product
- Use pagination for large datasets
- Implement caching for frequently accessed data
- Use appropriate HTTP status codes

### **2. Database Optimization**
- Use `@JoinColumn` for foreign key
- Use `FetchType.LAZY` for performance
- Add indexes on frequently queried columns
- Use `@Transactional` for data consistency

### **3. Security**
- Validate all input data
- Use DTOs for request/response
- Implement proper error handling
- Add audit logging

## 🔄 **Next Steps**

1. **Add Pagination** for large datasets
2. **Implement Caching** with Redis
3. **Add File Upload** for product images
4. **Implement Soft Delete** properly
5. **Add Audit Trail** for changes
6. **Create Admin Panel** for management
7. **Add Bulk Operations** for efficiency
8. **Implement Search** with Elasticsearch
