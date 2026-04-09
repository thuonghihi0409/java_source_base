package com.example.demo.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.ViecLam;

@Repository
public interface ViecLamRepository extends JpaRepository<ViecLam, Long> {

        @Query("SELECT DISTINCT v FROM ViecLam v LEFT JOIN v.khuVucList kv "
                        + "WHERE (:keyword IS NULL OR LOWER(v.tieuDe) LIKE LOWER(CONCAT('%', :keyword, '%')) "
                        + "OR LOWER(v.moTa) LIKE LOWER(CONCAT('%', :keyword, '%'))) "
                        + "AND (:congTyId IS NULL OR v.congTy.id = :congTyId) "
                        + "AND (:hinhThucLamViecId IS NULL OR v.hinhThucLamViec.id = :hinhThucLamViecId) "
                        + "AND (:khuVucId IS NULL OR kv.id = :khuVucId) "
                        + "AND (:onlyActive IS NULL OR v.isActive = :onlyActive)")
        List<ViecLam> search(String keyword, Long congTyId, Long hinhThucLamViecId, Long khuVucId, Boolean onlyActive);

        @Query(value = "SELECT DISTINCT v FROM ViecLam v LEFT JOIN v.khuVucList kv "
                        + "WHERE (:keyword IS NULL OR LOWER(v.tieuDe) LIKE LOWER(CONCAT('%', :keyword, '%')) "
                        + "OR LOWER(v.moTa) LIKE LOWER(CONCAT('%', :keyword, '%'))) "
                        + "AND (:congTyId IS NULL OR v.congTy.id = :congTyId) "
                        + "AND (:hinhThucLamViecId IS NULL OR v.hinhThucLamViec.id = :hinhThucLamViecId) "
                        + "AND (:khuVucId IS NULL OR kv.id = :khuVucId) "
                        + "AND (:onlyActive IS NULL OR v.isActive = :onlyActive)", countQuery = "SELECT COUNT(DISTINCT v.id) FROM ViecLam v LEFT JOIN v.khuVucList kv "
                                        + "WHERE (:keyword IS NULL OR LOWER(v.tieuDe) LIKE LOWER(CONCAT('%', :keyword, '%')) "
                                        + "OR LOWER(v.moTa) LIKE LOWER(CONCAT('%', :keyword, '%'))) "
                                        + "AND (:congTyId IS NULL OR v.congTy.id = :congTyId) "
                                        + "AND (:hinhThucLamViecId IS NULL OR v.hinhThucLamViec.id = :hinhThucLamViecId) "
                                        + "AND (:khuVucId IS NULL OR kv.id = :khuVucId) "
                                        + "AND (:onlyActive IS NULL OR v.isActive = :onlyActive)")
        Page<ViecLam> searchPage(String keyword, Long congTyId, Long hinhThucLamViecId, Long khuVucId,
                        Boolean onlyActive,
                        Pageable pageable);

        List<ViecLam> findByHrUser_Id(Long hrUserId);

        boolean existsByKhuVucList_Id(Long khuVucId);

        boolean existsByCongTy_Id(Long congTyId);
}
