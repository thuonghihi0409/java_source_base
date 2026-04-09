package com.example.demo.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "viec_lam")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ViecLam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 200)
    @Column(nullable = false, length = 200)
    private String tieuDe;

    @Column(columnDefinition = "TEXT")
    private String moTa;

    @Column(name = "muc_luong_min", precision = 15, scale = 2)
    private BigDecimal mucLuongMin;

    @Column(name = "muc_luong_max", precision = 15, scale = 2)
    private BigDecimal mucLuongMax;

    @Size(max = 100)
    @Column(name = "cap_do_kinh_nghiem", length = 100)
    private String capDoKinhNghiem;

    @Column(name = "han_nop")
    private LocalDate hanNop;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cong_ty_id", nullable = false)
    private CongTy congTy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hr_user_id", nullable = false)
    private User hrUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hinh_thuc_lam_viec_id", nullable = false)
    private HinhThucLamViec hinhThucLamViec;

    @ManyToMany
    @JoinTable(name = "viec_lam_khu_vuc", joinColumns = @JoinColumn(name = "viec_lam_id"), inverseJoinColumns = @JoinColumn(name = "khu_vuc_id"))
    private List<KhuVuc> khuVucList = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
