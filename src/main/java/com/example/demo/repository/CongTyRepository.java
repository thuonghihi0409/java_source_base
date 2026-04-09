package com.example.demo.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.CongTy;

@Repository
public interface CongTyRepository extends JpaRepository<CongTy, Long> {
    boolean existsByTenIgnoreCase(String ten);

    List<CongTy> findByTenContainingIgnoreCase(String keyword);

    Page<CongTy> findByTenContainingIgnoreCase(String keyword, Pageable pageable);
}
