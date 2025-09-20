package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/public")
    public ResponseEntity<ApiResponse<String>> publicEndpoint() {
        return ResponseEntity.ok(ApiResponse.success("This is a public endpoint", "Public access allowed"));
    }

    @GetMapping("/protected")
    public ResponseEntity<ApiResponse<String>> protectedEndpoint() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        return ResponseEntity.ok(ApiResponse.success(
            "Hello " + username + "! This is a protected endpoint", 
            "Protected access successful"
        ));
    }

    @GetMapping("/user-info")
    public ResponseEntity<ApiResponse<Object>> getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        return ResponseEntity.ok(ApiResponse.success(
            authentication.getPrincipal(), 
            "User information retrieved"
        ));
    }
}
