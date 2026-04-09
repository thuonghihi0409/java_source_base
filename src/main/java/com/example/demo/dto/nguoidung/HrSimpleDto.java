package com.example.demo.dto.nguoidung;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HrSimpleDto {
    private Long id;
    private String email;
    private String fullName;
}
