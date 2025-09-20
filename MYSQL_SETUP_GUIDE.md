# Hướng dẫn cài đặt và cấu hình MySQL cho ứng dụng Spring Boot

## 1. Cài đặt MySQL

### Windows:
1. Tải MySQL Community Server từ: https://dev.mysql.com/downloads/mysql/
2. Chạy installer và làm theo hướng dẫn
3. Ghi nhớ root password

### macOS:
```bash
# Sử dụng Homebrew
brew install mysql
brew services start mysql
```

### Ubuntu/Debian:
```bash
sudo apt update
sudo apt install mysql-server
sudo mysql_secure_installation
```

## 2. Tạo Database và User

### Cách 1: Sử dụng script SQL
```bash
# Đăng nhập MySQL
mysql -u root -p

# Chạy script
source database_setup.sql
```

### Cách 2: Tạo thủ công
```sql
-- Đăng nhập MySQL
mysql -u root -p

-- Tạo database
CREATE DATABASE demo_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Tạo user (tùy chọn)
CREATE USER 'demo_user'@'localhost' IDENTIFIED BY 'demo_password';
GRANT ALL PRIVILEGES ON demo_db.* TO 'demo_user'@'localhost';
FLUSH PRIVILEGES;
```

## 3. Cấu hình ứng dụng

### Cập nhật application.properties
```properties
# Thay đổi thông tin kết nối theo môi trường của bạn
spring.datasource.url=jdbc:mysql://localhost:3306/demo_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

### Sử dụng profiles
```bash
# Development
java -jar demo-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev

# Production
java -jar demo-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

## 4. Kiểm tra kết nối

### Test kết nối MySQL:
```bash
mysql -u root -p -e "SHOW DATABASES;"
```

### Test ứng dụng:
```bash
# Khởi động ứng dụng
mvn spring-boot:run

# Test endpoint
curl http://localhost:8080/api/auth/health
```

## 5. Troubleshooting

### Lỗi thường gặp:

#### 1. Connection refused
```
Error: Could not create connection to database server
```
**Giải pháp:**
- Kiểm tra MySQL service đang chạy
- Kiểm tra port 3306 có bị block không
- Kiểm tra firewall

#### 2. Access denied
```
Error: Access denied for user 'root'@'localhost'
```
**Giải pháp:**
- Kiểm tra username/password
- Reset password MySQL nếu cần

#### 3. Database not found
```
Error: Unknown database 'demo_db'
```
**Giải pháp:**
- Chạy script tạo database
- Kiểm tra tên database trong connection string

#### 4. Timezone issues
```
Error: The server time zone value is unrecognized
```
**Giải pháp:**
- Thêm `serverTimezone=UTC` vào connection string
- Hoặc set timezone MySQL: `SET GLOBAL time_zone = '+00:00';`

## 6. Cấu hình nâng cao

### Connection Pool Settings:
```properties
# Tối ưu cho production
spring.datasource.hikari.maximum-pool-size=50
spring.datasource.hikari.minimum-idle=10
spring.datasource.hikari.idle-timeout=300000
spring.datasource.hikari.connection-timeout=20000
spring.datasource.hikari.leak-detection-threshold=60000
```

### SSL Configuration:
```properties
# Development (không SSL)
spring.datasource.url=jdbc:mysql://localhost:3306/demo_db?useSSL=false

# Production (có SSL)
spring.datasource.url=jdbc:mysql://localhost:3306/demo_db?useSSL=true
```

## 7. Monitoring và Maintenance

### Kiểm tra kết nối:
```sql
SHOW PROCESSLIST;
SHOW STATUS LIKE 'Connections';
```

### Backup database:
```bash
mysqldump -u root -p demo_db > backup.sql
```

### Restore database:
```bash
mysql -u root -p demo_db < backup.sql
```

## 8. Security Best Practices

1. **Không dùng root user** cho ứng dụng
2. **Tạo user riêng** với quyền hạn tối thiểu
3. **Bật SSL** trong production
4. **Thay đổi default port** nếu cần
5. **Sử dụng strong passwords**
6. **Regular backup** database
7. **Monitor logs** thường xuyên
