package com.example.demo.dto.auth;

import lombok.Data;

@Data
public class LogoutRequest {

    /**
     * Neu co: chi thu hoi phien refresh token nay. Neu null: thu hoi tat ca refresh
     * token cua tai khoan.
     */
    private String refreshToken;
}
