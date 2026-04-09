package com.example.demo.dto.congty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CongTyRequest {
    @NotBlank
    @Size(max = 200)
    private String ten;

    @Size(max = 500)
    private String moTa;

    @Size(max = 255)
    private String website;

    @Size(max = 255)
    private String diaChi;

    @Size(max = 255)
    private String logoUrl;

    private Boolean isActive = true;
}
