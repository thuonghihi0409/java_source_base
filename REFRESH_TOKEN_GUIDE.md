# Refresh Token Implementation Guide

## 🔄 **Refresh Token System**

### **Tổng quan:**
- **Access Token**: Ngắn hạn (24 giờ) - dùng cho API calls
- **Refresh Token**: Dài hạn (7 ngày) - dùng để lấy access token mới
- **Database Storage**: Refresh tokens được lưu trong database
- **Security**: Refresh tokens có thể bị revoke bất kỳ lúc nào

## 🏗️ **Architecture**

### **Token Flow:**
```
1. Login/Register → Access Token + Refresh Token
2. API Calls → Access Token (trong header)
3. Access Token Expired → Dùng Refresh Token để lấy Access Token mới
4. Logout → Revoke Refresh Token
```

### **Database Schema:**
```sql
CREATE TABLE refresh_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(500) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    expiry_date DATETIME NOT NULL,
    is_revoked BOOLEAN DEFAULT FALSE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    last_used_at DATETIME,
    device_info VARCHAR(500),
    ip_address VARCHAR(45),
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

## 🚀 **API Endpoints**

### **1. Login (với Refresh Token)**
```bash
POST /api/auth/login
Content-Type: application/json

{
    "username": "testuser",
    "password": "password123"
}
```

**Response:**
```json
{
    "success": true,
    "message": "Login successful",
    "data": {
        "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
        "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
        "tokenType": "Bearer",
        "expiresIn": 86400,
        "username": "testuser",
        "email": "test@example.com",
        "fullName": "Test User",
        "message": "Login successful"
    }
}
```

### **2. Refresh Token**
```bash
POST /api/auth/refresh
Content-Type: application/json

{
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**Response:**
```json
{
    "success": true,
    "message": "Token refreshed successfully",
    "data": {
        "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
        "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
        "tokenType": "Bearer",
        "expiresIn": 86400,
        "username": "testuser",
        "email": "test@example.com",
        "fullName": "Test User",
        "message": "Token refreshed successfully"
    }
}
```

### **3. Logout (Single Session)**
```bash
POST /api/auth/logout
Content-Type: application/json
Authorization: Bearer <access_token>

{
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

### **4. Logout All Sessions**
```bash
POST /api/auth/logout-all
Authorization: Bearer <access_token>
```

## 🧪 **Test Scenarios**

### **Scenario 1: Complete Login Flow**
```bash
# 1. Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'

# Response sẽ chứa accessToken và refreshToken
# Lưu cả 2 tokens
ACCESS_TOKEN="eyJhbGciOiJIUzI1NiJ9..."
REFRESH_TOKEN="eyJhbGciOiJIUzI1NiJ9..."

# 2. Sử dụng access token
curl -H "Authorization: Bearer $ACCESS_TOKEN" \
  http://localhost:8080/api/users/profile

# 3. Khi access token hết hạn, dùng refresh token
curl -X POST http://localhost:8080/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d "{\"refreshToken\": \"$REFRESH_TOKEN\"}"

# 4. Logout
curl -X POST http://localhost:8080/api/auth/logout \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d "{\"refreshToken\": \"$REFRESH_TOKEN\"}"
```

### **Scenario 2: Multiple Devices**
```bash
# Device 1: Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "testuser", "password": "password123"}'

# Device 2: Login (cùng user)
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "testuser", "password": "password123"}'

# Logout all sessions
curl -X POST http://localhost:8080/api/auth/logout-all \
  -H "Authorization: Bearer $ACCESS_TOKEN"
```

## 🔒 **Security Features**

### **1. Token Rotation**
- Refresh token có thể được rotate (thay đổi) mỗi lần sử dụng
- Access token luôn được tạo mới khi refresh

### **2. Token Revocation**
- Refresh tokens có thể bị revoke bất kỳ lúc nào
- Logout sẽ revoke refresh token
- Logout all sẽ revoke tất cả refresh tokens của user

### **3. Token Expiration**
- Access token: 24 giờ
- Refresh token: 7 ngày
- Expired tokens tự động bị xóa

### **4. Device Tracking**
- Refresh tokens có thể track device info
- IP address tracking
- Last used timestamp

## 📊 **Database Operations**

### **Cleanup Expired Tokens**
```java
// Tự động cleanup expired tokens
@Scheduled(fixedRate = 3600000) // Mỗi giờ
public void cleanupExpiredTokens() {
    userService.cleanupExpiredTokens();
}
```

### **Token Management**
```java
// Revoke specific refresh token
userService.revokeRefreshToken(refreshToken);

// Revoke all tokens for user
userService.logoutAll(username);

// Check active tokens count
long activeTokens = refreshTokenRepository.countActiveTokensByUser(user);
```

## 🚨 **Error Handling**

### **1. Invalid Refresh Token (401)**
```json
{
    "success": false,
    "message": "Invalid refresh token",
    "data": null,
    "timestamp": "2024-01-01T10:00:00"
}
```

### **2. Expired Refresh Token (401)**
```json
{
    "success": false,
    "message": "Refresh token is expired or revoked",
    "data": null,
    "timestamp": "2024-01-01T10:00:00"
}
```

### **3. User Deactivated (401)**
```json
{
    "success": false,
    "message": "User account is deactivated",
    "data": null,
    "timestamp": "2024-01-01T10:00:00"
}
```

## 🎯 **Best Practices**

### **1. Token Storage**
```javascript
// Frontend - Store tokens securely
localStorage.setItem('accessToken', response.data.accessToken);
localStorage.setItem('refreshToken', response.data.refreshToken);

// Or use HttpOnly cookies (more secure)
```

### **2. Automatic Token Refresh**
```javascript
// Interceptor để tự động refresh token
axios.interceptors.response.use(
    response => response,
    async error => {
        if (error.response?.status === 401) {
            const refreshToken = localStorage.getItem('refreshToken');
            if (refreshToken) {
                try {
                    const response = await axios.post('/api/auth/refresh', {
                        refreshToken: refreshToken
                    });
                    localStorage.setItem('accessToken', response.data.data.accessToken);
                    // Retry original request
                    return axios.request(error.config);
                } catch (refreshError) {
                    // Redirect to login
                    window.location.href = '/login';
                }
            }
        }
        return Promise.reject(error);
    }
);
```

### **3. Logout Implementation**
```javascript
// Logout function
async function logout() {
    const refreshToken = localStorage.getItem('refreshToken');
    if (refreshToken) {
        await axios.post('/api/auth/logout', {
            refreshToken: refreshToken
        });
    }
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    window.location.href = '/login';
}
```

## 🔧 **Configuration**

### **application.properties**
```properties
# JWT Configuration
jwt.secret=mySecretKey123456789012345678901234567890
jwt.expiration=86400000          # 24 hours
jwt.refresh-expiration=604800000  # 7 days
```

### **Environment Variables**
```bash
# Production
JWT_SECRET=your-super-secret-key-at-least-256-bits-long
JWT_EXPIRATION=3600000    # 1 hour
JWT_REFRESH_EXPIRATION=259200000  # 3 days
```

## 📈 **Monitoring & Analytics**

### **Token Usage Tracking**
- Track refresh token usage
- Monitor active sessions per user
- Detect suspicious activity
- Generate usage reports

### **Security Metrics**
- Failed refresh attempts
- Token revocation events
- Expired token cleanup
- Multiple device logins

## 🔄 **Migration from Simple JWT**

### **Before (Simple JWT):**
```json
{
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "username": "testuser",
    "email": "test@example.com"
}
```

### **After (Refresh Token):**
```json
{
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "expiresIn": 86400,
    "username": "testuser",
    "email": "test@example.com"
}
```

## ✅ **Benefits**

1. **Enhanced Security**: Refresh tokens có thể bị revoke
2. **Better UX**: Không cần đăng nhập lại thường xuyên
3. **Multi-device Support**: Quản lý nhiều sessions
4. **Audit Trail**: Track token usage
5. **Flexible Expiration**: Khác nhau cho access/refresh tokens

**Refresh Token system đã được implement hoàn chỉnh!** 🎉
