package com.example.demo.dto.vieclam;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.example.demo.dto.KhuVucSimpleDto;
import com.example.demo.dto.congty.CongTySimpleDto;
import com.example.demo.dto.hinhthuclamviec.HinhThucLamViecSimpleDto;
import com.example.demo.dto.nguoidung.HrSimpleDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ViecLamResponse {
    private Long id;
    private String tieuDe;
    private String moTa;
    private BigDecimal mucLuongMin;
    private BigDecimal mucLuongMax;
    private String capDoKinhNghiem;
    private LocalDate hanNop;
    private Boolean isActive;

    private CongTySimpleDto congTy;
    private HinhThucLamViecSimpleDto hinhThucLamViec;
    private HrSimpleDto hrDangTuyen;
    private List<KhuVucSimpleDto> khuVucList;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
