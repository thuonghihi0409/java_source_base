package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.RefreshToken;
import com.example.demo.entity.User;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenAndRevokedFalse(String token);

    List<RefreshToken> findByUserAndRevokedFalse(User user);

    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.user = :user")
    void deleteByUser(@Param("user") User user);
}
