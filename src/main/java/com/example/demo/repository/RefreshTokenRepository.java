package com.example.demo.repository;

import com.example.demo.entity.RefreshToken;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    
    // Tìm refresh token theo token string
    Optional<RefreshToken> findByToken(String token);
    
    // Tìm refresh token theo user và token
    Optional<RefreshToken> findByUserAndToken(User user, String token);
    
    // Tìm tất cả refresh tokens của user
    List<RefreshToken> findByUser(User user);
    
    // Tìm active refresh tokens của user
    List<RefreshToken> findByUserAndIsRevokedFalse(User user);
    
    // Tìm expired refresh tokens
    List<RefreshToken> findByExpiryDateBefore(LocalDateTime dateTime);
    
    // Revoke tất cả refresh tokens của user
    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.isRevoked = true WHERE rt.user = :user")
    void revokeAllByUser(@Param("user") User user);
    
    // Revoke refresh token cụ thể
    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.isRevoked = true WHERE rt.token = :token")
    void revokeByToken(@Param("token") String token);
    
    // Xóa expired refresh tokens
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiryDate < :dateTime")
    void deleteExpiredTokens(@Param("dateTime") LocalDateTime dateTime);
    
    // Đếm số lượng active refresh tokens của user
    @Query("SELECT COUNT(rt) FROM RefreshToken rt WHERE rt.user = :user AND rt.isRevoked = false AND rt.expiryDate > :now")
    long countActiveTokensByUser(@Param("user") User user, @Param("now") LocalDateTime now);
    
    // Tìm refresh tokens theo device info
    List<RefreshToken> findByUserAndDeviceInfo(User user, String deviceInfo);
}
