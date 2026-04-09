package com.example.demo.dto.hinhthuclamviec;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HinhThucLamViecResponse {
    private Long id;
    private String ma;
    private String ten;
    private String moTa;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
