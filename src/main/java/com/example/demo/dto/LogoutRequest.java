package com.example.demo.dto;

import lombok.Data;

@Data
public class LogoutRequest {
    
    private String refreshToken;
}
