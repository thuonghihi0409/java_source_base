package com.example.demo.dto.vieclam;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ViecLamRequest {
    @NotBlank
    @Size(max = 200)
    private String tieuDe;

    private String moTa;
    private BigDecimal mucLuongMin;
    private BigDecimal mucLuongMax;

    @Size(max = 100)
    private String capDoKinhNghiem;

    private LocalDate hanNop;
    private Boolean isActive = true;

    @NotNull
    private Long congTyId;

    @NotNull
    private Long hinhThucLamViecId;

    @NotEmpty
    private List<Long> khuVucIds;

    // Neu null: he thong mac dinh lay user dang dang nhap (ROLE_HR)
    private Long hrUserId;
}
