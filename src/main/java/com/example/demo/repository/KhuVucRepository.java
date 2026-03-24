package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.KhuVuc;

@Repository
public interface KhuVucRepository extends JpaRepository<KhuVuc, Long> {

    Optional<KhuVuc> findByTenIgnoreCase(String ten);

    boolean existsByTenIgnoreCase(String ten);

    List<KhuVuc> findByTenContainingIgnoreCase(String keyword);
}
