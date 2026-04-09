package com.example.demo.dto.nguoidung;

import com.example.demo.entity.Role;

import lombok.Data;

@Data
public class NguoiDungAdminUpdateRequest {
    private String fullName;
    private String phone;
    private Boolean enabled;
    private Role role;
}
