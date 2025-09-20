package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private Long expiresIn; // in seconds
    private String username;
    private String email;
    private String fullName;
    private String message;
    
    public LoginResponse(String message) {
        this.message = message;
    }
    
    public LoginResponse(String accessToken, String refreshToken, Long expiresIn, 
                        String username, String email, String fullName, String message) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.message = message;
    }
    
    // Static factory method để tạo LoginResponse dễ dàng hơn
    public static LoginResponse create(String accessToken, String refreshToken, Long expiresIn, 
                                     String username, String email, String fullName, String message) {
        LoginResponse response = new LoginResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setExpiresIn(expiresIn);
        response.setUsername(username);
        response.setEmail(email);
        response.setFullName(fullName);
        response.setMessage(message);
        return response;
    }
}
