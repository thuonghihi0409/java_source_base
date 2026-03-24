package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class KhuVucRequest {

    @NotBlank(message = "Ten khu vuc la bat buoc")
    @Size(min = 2, max = 100, message = "Ten khu vuc phai tu 2 den 100 ky tu")
    private String ten;

    @Size(max = 500, message = "Mo ta khong duoc vuot qua 500 ky tu")
    private String moTa;

    private Boolean isActive = true;
}
