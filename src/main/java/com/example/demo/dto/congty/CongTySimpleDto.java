package com.example.demo.dto.congty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CongTySimpleDto {
    private Long id;
    private String ten;
    private String logoUrl;
}
