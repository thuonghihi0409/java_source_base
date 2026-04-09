package com.example.demo.dto.hinhthuclamviec;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class HinhThucLamViecRequest {
    @NotBlank
    @Size(max = 30)
    private String ma;

    @NotBlank
    @Size(max = 100)
    private String ten;

    @Size(max = 255)
    private String moTa;

    private Boolean isActive = true;
}
