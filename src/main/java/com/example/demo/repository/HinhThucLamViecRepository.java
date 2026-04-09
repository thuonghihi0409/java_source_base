package com.example.demo.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.HinhThucLamViec;

@Repository
public interface HinhThucLamViecRepository extends JpaRepository<HinhThucLamViec, Long> {
    boolean existsByMaIgnoreCase(String ma);

    boolean existsByTenIgnoreCase(String ten);

    List<HinhThucLamViec> findByTenContainingIgnoreCase(String keyword);

    Page<HinhThucLamViec> findByTenContainingIgnoreCase(String keyword, Pageable pageable);
}
