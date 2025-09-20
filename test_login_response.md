# Test Login Response với Refresh Token

## 🧪 **Test Login để kiểm tra Refresh Token**

### **1. Test Login Endpoint:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```

### **2. Expected Response (với Refresh Token):**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlciIsImlhdCI6MTYz...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlciIsImlhdCI6MTYz...",
    "tokenType": "Bearer",
    "expiresIn": 86400,
    "username": "testuser",
    "email": "test@example.com",
    "fullName": "Test User",
    "message": "Login successful"
  },
  "timestamp": "2024-01-01T10:00:00"
}
```

### **3. Test Register Endpoint:**
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

### **4. Test Refresh Token Endpoint:**
```bash
# Lưu refresh token từ login response
REFRESH_TOKEN="your_refresh_token_here"

curl -X POST http://localhost:8080/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d "{
    \"refreshToken\": \"$REFRESH_TOKEN\"
  }"
```

## 🔍 **Debugging Steps:**

### **1. Kiểm tra LoginResponse DTO:**
- ✅ `accessToken` field
- ✅ `refreshToken` field  
- ✅ `tokenType` field (default "Bearer")
- ✅ `expiresIn` field (86400 seconds)

### **2. Kiểm tra UserService:**
- ✅ Generate access token
- ✅ Generate refresh token
- ✅ Save refresh token to database
- ✅ Return LoginResponse with both tokens

### **3. Kiểm tra Database:**
```sql
-- Kiểm tra refresh_tokens table
SELECT * FROM refresh_tokens;

-- Kiểm tra users table
SELECT * FROM users;
```

## 🚨 **Troubleshooting:**

### **Nếu không có refreshToken trong response:**

1. **Kiểm tra compilation errors:**
```bash
mvn clean compile
```

2. **Kiểm tra application startup:**
```bash
mvn spring-boot:run
```

3. **Kiểm tra logs:**
- Look for any errors in console
- Check if RefreshToken entity is created
- Verify database connection

### **Nếu có lỗi database:**
```sql
-- Tạo table refresh_tokens nếu chưa có
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

## ✅ **Expected Behavior:**

1. **Login/Register** → Trả về cả `accessToken` và `refreshToken`
2. **API Calls** → Sử dụng `accessToken` trong Authorization header
3. **Token Expired** → Dùng `refreshToken` để lấy `accessToken` mới
4. **Logout** → Revoke `refreshToken`

## 📝 **Test Commands:**

```bash
# 1. Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "testuser", "password": "password123"}' | jq

# 2. Extract tokens
ACCESS_TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "testuser", "password": "password123"}' | jq -r '.data.accessToken')

REFRESH_TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "testuser", "password": "password123"}' | jq -r '.data.refreshToken')

# 3. Test access token
curl -H "Authorization: Bearer $ACCESS_TOKEN" \
  http://localhost:8080/api/users/profile

# 4. Test refresh token
curl -X POST http://localhost:8080/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d "{\"refreshToken\": \"$REFRESH_TOKEN\"}"
```

**Nếu vẫn không có refreshToken, hãy check logs và database!**
