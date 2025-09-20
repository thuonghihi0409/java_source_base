# JWT Security Implementation Guide

## 🔐 **JWT Security Features**

### **1. JWT Token Generation**
- ✅ Secure token generation với HMAC SHA-256
- ✅ Configurable expiration time (24 hours default)
- ✅ Custom claims support
- ✅ Username-based subject

### **2. Password Security**
- ✅ BCrypt password hashing
- ✅ Salt rounds for enhanced security
- ✅ Password validation during login

### **3. Authentication Flow**
- ✅ JWT-based stateless authentication
- ✅ Automatic token validation
- ✅ Security context management
- ✅ Role-based access control ready

### **4. Security Configuration**
- ✅ CORS configuration
- ✅ CSRF protection disabled (for API)
- ✅ Stateless session management
- ✅ Protected endpoints configuration

## 🚀 **API Endpoints với JWT**

### **Public Endpoints (Không cần JWT)**
```bash
# Đăng ký
POST /api/auth/register

# Đăng nhập
POST /api/auth/login

# Health check
GET /api/auth/health

# Public test endpoint
GET /api/test/public
```

### **Protected Endpoints (Cần JWT)**
```bash
# Lấy thông tin user
GET /api/users/{id}
GET /api/users/username/{username}

# Protected test endpoints
GET /api/test/protected
GET /api/test/user-info
```

## 📝 **Cách sử dụng JWT**

### **1. Đăng ký và đăng nhập**
```bash
# Đăng ký user mới
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123",
    "fullName": "Test User"
  }'

# Response sẽ chứa JWT token
{
  "success": true,
  "message": "Registration successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlciIsImlhdCI6MTYz...",
    "username": "testuser",
    "email": "test@example.com",
    "fullName": "Test User",
    "message": "Registration successful"
  }
}
```

### **2. Sử dụng JWT token**
```bash
# Lưu token từ response đăng nhập
TOKEN="eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlciIsImlhdCI6MTYz..."

# Sử dụng token trong header
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/test/protected

# Lấy thông tin user
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/users/username/testuser
```

## 🔧 **JWT Configuration**

### **application.properties**
```properties
# JWT Configuration
jwt.secret=mySecretKey123456789012345678901234567890
jwt.expiration=86400000  # 24 hours in milliseconds
```

### **Security Configuration**
- **CORS**: Enabled for all origins
- **CSRF**: Disabled (API only)
- **Session**: Stateless
- **Authentication**: JWT-based

## 🛡️ **Security Features**

### **1. Password Hashing**
```java
// Password được hash với BCrypt
String hashedPassword = passwordEncoder.encode("password123");
// Result: $2a$10$N9qo8uLOickgx2ZMRZoMye...

// Validation
boolean isValid = passwordEncoder.matches("password123", hashedPassword);
```

### **2. JWT Token Structure**
```json
{
  "header": {
    "alg": "HS256",
    "typ": "JWT"
  },
  "payload": {
    "sub": "username",
    "iat": 1634567890,
    "exp": 1634654290
  },
  "signature": "HMACSHA256(base64UrlEncode(header) + '.' + base64UrlEncode(payload), secret)"
}
```

### **3. Token Validation**
- ✅ Signature verification
- ✅ Expiration check
- ✅ Username validation
- ✅ User status check (active/inactive)

## 🧪 **Test Cases**

### **Test 1: Đăng ký và đăng nhập**
```bash
# 1. Đăng ký
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username": "johndoe", "email": "john@example.com", "password": "password123", "fullName": "John Doe"}'

# 2. Đăng nhập
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "johndoe", "password": "password123"}'

# 3. Sử dụng token
curl -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  http://localhost:8080/api/test/protected
```

### **Test 2: Test protected endpoints**
```bash
# Không có token - sẽ bị 401
curl http://localhost:8080/api/test/protected

# Có token - sẽ thành công
curl -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  http://localhost:8080/api/test/protected
```

### **Test 3: Test token expiration**
```bash
# Token hết hạn sẽ trả về 401
curl -H "Authorization: Bearer EXPIRED_TOKEN" \
  http://localhost:8080/api/test/protected
```

## 🔒 **Security Best Practices**

### **1. Production Configuration**
```properties
# Sử dụng secret key mạnh
jwt.secret=your-super-secret-key-at-least-256-bits-long

# Giảm thời gian expiration
jwt.expiration=3600000  # 1 hour

# Disable debug logging
logging.level.org.springframework.security=WARN
```

### **2. Token Management**
- ✅ Store tokens securely (HttpOnly cookies recommended)
- ✅ Implement token refresh mechanism
- ✅ Logout với token blacklist
- ✅ Monitor token usage

### **3. Additional Security**
- ✅ Rate limiting
- ✅ IP whitelisting
- ✅ Audit logging
- ✅ Input validation
- ✅ SQL injection prevention

## 🚨 **Troubleshooting**

### **Common Issues:**

#### 1. **401 Unauthorized**
```
Error: Full authentication is required
```
**Solution:** Check if JWT token is included in Authorization header

#### 2. **403 Forbidden**
```
Error: Access Denied
```
**Solution:** Check user roles and permissions

#### 3. **Token Expired**
```
Error: JWT token is expired
```
**Solution:** Re-login to get new token

#### 4. **Invalid Token**
```
Error: JWT token is invalid
```
**Solution:** Check token format and secret key

## 📊 **Monitoring & Logging**

### **Security Logs**
```properties
# Enable security logging
logging.level.org.springframework.security=DEBUG
logging.level.com.example.demo.security=DEBUG
```

### **JWT Token Info**
- Token generation time
- Token expiration time
- User authentication attempts
- Failed authentication attempts

## 🔄 **Next Steps**

1. **Implement Refresh Tokens**
2. **Add Role-based Access Control**
3. **Implement Token Blacklist**
4. **Add Rate Limiting**
5. **Implement Audit Logging**
6. **Add Two-Factor Authentication**
