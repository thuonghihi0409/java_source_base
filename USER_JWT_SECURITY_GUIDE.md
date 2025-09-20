# User API với JWT Security Guide

## 🔐 **JWT Security cho User Endpoints**

### **Có, tất cả User endpoints đều cần JWT token!**

## 📋 **User API Endpoints (Protected)**

### **1. Get User by ID**
```bash
GET /api/users/{id}
Authorization: Bearer <JWT_TOKEN>
```

### **2. Get User by Username**
```bash
GET /api/users/username/{username}
Authorization: Bearer <JWT_TOKEN>
```

### **3. Get Current User Profile**
```bash
GET /api/users/profile
Authorization: Bearer <JWT_TOKEN>
```

### **4. Get Current User Info**
```bash
GET /api/users/me
Authorization: Bearer <JWT_TOKEN>
```

## 🛡️ **Security Configuration**

### **SecurityConfig.java**
```java
.authorizeHttpRequests(authz -> authz
    .requestMatchers("/api/auth/**").permitAll()  // Chỉ auth endpoints không cần JWT
    .requestMatchers("/h2-console/**").permitAll()
    .requestMatchers("/actuator/**").permitAll()
    .anyRequest().authenticated()  // Tất cả endpoints khác cần JWT
)
```

### **JWT Authentication Flow**
1. **JwtAuthenticationFilter** tự động validate JWT token
2. Nếu token hợp lệ → Set Authentication vào SecurityContext
3. Nếu token không hợp lệ → Trả về 401 Unauthorized
4. Controller có thể access user info từ SecurityContext

## 🧪 **Test User Endpoints với JWT**

### **Step 1: Đăng nhập để lấy JWT token**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```

**Response:**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlciIsImlhdCI6MTYz...",
    "username": "testuser",
    "email": "test@example.com",
    "fullName": "Test User",
    "message": "Login successful"
  }
}
```

### **Step 2: Sử dụng JWT token để access User endpoints**

#### **Get User by ID:**
```bash
curl -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlciIsImlhdCI6MTYz..." \
  http://localhost:8080/api/users/1
```

#### **Get Current User Profile:**
```bash
curl -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlciIsImlhdCI6MTYz..." \
  http://localhost:8080/api/users/profile
```

#### **Get Current User Info:**
```bash
curl -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlciIsImlhdCI6MTYz..." \
  http://localhost:8080/api/users/me
```

## 📊 **Response Examples**

### **User Response (Không có password)**
```json
{
  "success": true,
  "message": "User found",
  "data": {
    "id": 1,
    "username": "testuser",
    "email": "test@example.com",
    "fullName": "Test User",
    "isActive": true,
    "createdAt": "2024-01-01T10:00:00",
    "updatedAt": "2024-01-01T10:00:00"
  },
  "timestamp": "2024-01-01T10:00:00"
}
```

### **Current User Info Response**
```json
{
  "success": true,
  "message": "Current user authentication info",
  "data": {
    "username": "testuser",
    "authorities": [
      {
        "authority": "ROLE_USER"
      }
    ],
    "accountNonExpired": true,
    "accountNonLocked": true,
    "credentialsNonExpired": true,
    "enabled": true
  },
  "timestamp": "2024-01-01T10:00:00"
}
```

## 🚨 **Error Responses**

### **1. Không có JWT token (401)**
```bash
curl http://localhost:8080/api/users/1
```

**Response:**
```json
{
  "success": false,
  "message": "Full authentication is required to access this resource",
  "data": null,
  "timestamp": "2024-01-01T10:00:00"
}
```

### **2. JWT token không hợp lệ (401)**
```bash
curl -H "Authorization: Bearer invalid_token" \
  http://localhost:8080/api/users/1
```

**Response:**
```json
{
  "success": false,
  "message": "JWT token is invalid",
  "data": null,
  "timestamp": "2024-01-01T10:00:00"
}
```

### **3. JWT token hết hạn (401)**
```bash
curl -H "Authorization: Bearer expired_token" \
  http://localhost:8080/api/users/1
```

**Response:**
```json
{
  "success": false,
  "message": "JWT token is expired",
  "data": null,
  "timestamp": "2024-01-01T10:00:00"
}
```

### **4. User không tồn tại (404)**
```bash
curl -H "Authorization: Bearer valid_token" \
  http://localhost:8080/api/users/999
```

**Response:**
```json
{
  "success": false,
  "message": "User not found with id: 999",
  "data": null,
  "timestamp": "2024-01-01T10:00:00"
}
```

## 🔒 **Security Features**

### **1. Automatic JWT Validation**
- **JwtAuthenticationFilter** tự động validate mọi request
- Không cần code validation trong controller
- Tự động set Authentication vào SecurityContext

### **2. Password Protection**
- **UserResponse DTO** không include password
- Chỉ trả về thông tin cần thiết
- Bảo mật thông tin nhạy cảm

### **3. User Context Access**
- Controller có thể access current user từ SecurityContext
- Không cần truyền user ID qua parameter
- Tự động lấy thông tin từ JWT token

### **4. Role-based Ready**
- Sẵn sàng cho role-based access control
- Có thể thêm @PreAuthorize annotations
- Support multiple roles per user

## 🎯 **Best Practices**

### **1. Always Use JWT Token**
```bash
# ✅ Đúng
curl -H "Authorization: Bearer <token>" http://localhost:8080/api/users/1

# ❌ Sai - sẽ bị 401
curl http://localhost:8080/api/users/1
```

### **2. Store Token Securely**
- Store JWT token trong HttpOnly cookies (recommended)
- Hoặc localStorage/sessionStorage (less secure)
- Không hardcode token trong code

### **3. Handle Token Expiration**
- Check token expiration trước khi gọi API
- Implement refresh token mechanism
- Redirect to login khi token hết hạn

### **4. Use HTTPS in Production**
- JWT tokens có thể bị intercept qua HTTP
- Always use HTTPS cho production
- Set secure cookie flags

## 🔄 **Complete Flow Example**

### **1. Register User**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "email": "new@example.com",
    "password": "password123",
    "fullName": "New User"
  }'
```

### **2. Login to Get Token**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "password": "password123"
  }'
```

### **3. Use Token for Protected Endpoints**
```bash
# Get current user profile
curl -H "Authorization: Bearer <token>" \
  http://localhost:8080/api/users/profile

# Get user by ID
curl -H "Authorization: Bearer <token>" \
  http://localhost:8080/api/users/1

# Get current user info
curl -H "Authorization: Bearer <token>" \
  http://localhost:8080/api/users/me
```

## 📝 **Summary**

✅ **Tất cả User endpoints đều được bảo vệ bởi JWT**
✅ **JWT token được validate tự động**
✅ **Password không được expose trong response**
✅ **Current user context có thể access từ SecurityContext**
✅ **Error handling chuyên nghiệp cho authentication**
✅ **Sẵn sàng cho role-based access control**

**Kết luận: User endpoints đã được bảo mật hoàn toàn với JWT authentication!**
