-- MySQL Database Setup Script
-- Chạy script này để tạo database và user cho ứng dụng

-- Tạo database
CREATE DATABASE IF NOT EXISTS demo_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Tạo user cho ứng dụng (tùy chọn - có thể dùng root)
CREATE USER IF NOT EXISTS 'demo_user'@'localhost' IDENTIFIED BY 'demo_password';

-- Cấp quyền cho user
GRANT ALL PRIVILEGES ON demo_db.* TO 'demo_user'@'localhost';

-- Cấp quyền cho user từ bất kỳ host nào (chỉ dùng cho development)
GRANT ALL PRIVILEGES ON demo_db.* TO 'demo_user'@'%';

-- Làm mới quyền
FLUSH PRIVILEGES;

-- Sử dụng database
USE demo_db;

-- Tạo bảng users (sẽ được tạo tự động bởi Hibernate, nhưng có thể tạo thủ công)
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert dữ liệu mẫu (tùy chọn)
INSERT INTO users (username, email, password, full_name, is_active) VALUES
('admin', 'admin@example.com', 'admin123', 'Administrator', TRUE),
('testuser', 'test@example.com', 'password123', 'Test User', TRUE)
ON DUPLICATE KEY UPDATE username=username;
