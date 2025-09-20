# Spring Boot Authentication API Documentation

## Tổng quan
Đây là một API đăng nhập/đăng ký hoàn chỉnh với xử lý lỗi chuyên nghiệp, được xây dựng bằng Spring Boot 3.4.9.

## Cấu trúc dự án
```
src/main/java/com/example/demo/
├── controller/          # REST Controllers
│   ├── AuthController.java
│   └── UserController.java
├── dto/                 # Data Transfer Objects
│   ├── ApiResponse.java
│   ├── LoginRequest.java
│   ├── LoginResponse.java
│   └── RegisterRequest.java
├── entity/              # JPA Entities
│   └── User.java
├── exception/           # Custom Exceptions & Handler
│   ├── ConflictException.java
│   ├── GlobalExceptionHandler.java
│   ├── ResourceNotFoundException.java
│   ├── UnauthorizedException.java
│   └── ValidationException.java
├── repository/          # Data Access Layer
│   └── UserRepository.java
└── service/             # Business Logic
    └── UserService.java
```

## API Endpoints

### 1. Authentication Endpoints

#### POST /api/auth/register
Đăng ký tài khoản mới

**Request Body:**
```json
{
    "username": "john_doe",
    "email": "john@example.com",
    "password": "password123",
    "fullName": "John Doe"
}
```

**Response (Success - 200):**
```json
{
    "success": true,
    "message": "Registration successful",
    "data": {
        "token": "Bearer_12345678-1234-1234-1234-123456789012",
        "username": "john_doe",
        "email": "john@example.com",
        "fullName": "John Doe",
        "message": "Registration successful"
    },
    "timestamp": "2024-01-01T10:00:00"
}
```

#### POST /api/auth/login
Đăng nhập

**Request Body:**
```json
{
    "username": "john_doe",
    "password": "password123"
}
```

**Response (Success - 200):**
```json
{
    "success": true,
    "message": "Login successful",
    "data": {
        "token": "Bearer_12345678-1234-1234-1234-123456789012",
        "username": "john_doe",
        "email": "john@example.com",
        "fullName": "John Doe",
        "message": "Login successful"
    },
    "timestamp": "2024-01-01T10:00:00"
}
```

#### GET /api/auth/health
Kiểm tra trạng thái service

**Response (Success - 200):**
```json
{
    "success": true,
    "message": "Service is healthy",
    "data": "Auth service is running",
    "timestamp": "2024-01-01T10:00:00"
}
```

### 2. User Endpoints

#### GET /api/users/{id}
Lấy thông tin user theo ID

**Response (Success - 200):**
```json
{
    "success": true,
    "message": "User found",
    "data": {
        "id": 1,
        "username": "john_doe",
        "email": "john@example.com",
        "fullName": "John Doe",
        "isActive": true,
        "createdAt": "2024-01-01T10:00:00",
        "updatedAt": "2024-01-01T10:00:00"
    },
    "timestamp": "2024-01-01T10:00:00"
}
```

#### GET /api/users/username/{username}
Lấy thông tin user theo username

## Error Handling

### Các loại lỗi được xử lý:

#### 1. Validation Errors (400)
```json
{
    "success": false,
    "message": "Validation failed",
    "data": {
        "username": "Username is required",
        "email": "Email should be valid",
        "password": "Password must be at least 6 characters"
    },
    "timestamp": "2024-01-01T10:00:00"
}
```

#### 2. Unauthorized (401)
```json
{
    "success": false,
    "message": "Invalid username or password",
    "data": null,
    "timestamp": "2024-01-01T10:00:00"
}
```

#### 3. Conflict (409)
```json
{
    "success": false,
    "message": "Username already exists",
    "data": null,
    "timestamp": "2024-01-01T10:00:00"
}
```

#### 4. Not Found (404)
```json
{
    "success": false,
    "message": "User not found with id: 999",
    "data": null,
    "timestamp": "2024-01-01T10:00:00"
}
```

#### 5. Internal Server Error (500)
```json
{
    "success": false,
    "message": "An unexpected error occurred",
    "data": null,
    "timestamp": "2024-01-01T10:00:00"
}
```

## Cách test API

### 1. Sử dụng cURL

**Đăng ký:**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123",
    "fullName": "Test User"
  }'
```

**Đăng nhập:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```

### 2. Sử dụng Postman
- Import các endpoint vào Postman
- Test với các trường hợp khác nhau

### 3. H2 Database Console
- Truy cập: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: `password`

## Tính năng chuyên nghiệp

### 1. Exception Handling
- `GlobalExceptionHandler` xử lý tất cả exceptions
- Custom exceptions cho từng loại lỗi
- Response format nhất quán

### 2. Validation
- Bean Validation với annotations
- Custom validation messages
- Detailed error responses

### 3. Security Features
- Password validation
- Account status checking
- Token generation (có thể nâng cấp thành JWT)

### 4. Database
- JPA/Hibernate với H2 in-memory database
- Automatic table creation
- Transaction management

### 5. Logging
- Debug logging cho development
- SQL query logging
- Error logging

## Nâng cấp có thể thực hiện

1. **JWT Authentication** - Thay thế simple token
2. **Password Encryption** - Sử dụng BCrypt
3. **Role-based Access Control** - Thêm roles và permissions
4. **Email Verification** - Xác thực email
5. **Rate Limiting** - Giới hạn số request
6. **Audit Logging** - Ghi log các hoạt động
7. **API Documentation** - Swagger/OpenAPI
8. **Unit Tests** - Test coverage
9. **Integration Tests** - Test toàn bộ flow
10. **Docker Support** - Containerization
