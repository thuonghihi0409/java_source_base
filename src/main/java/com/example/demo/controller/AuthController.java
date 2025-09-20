package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.dto.LogoutRequest;
import com.example.demo.dto.RefreshTokenRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse response = userService.login(loginRequest);
            return ResponseEntity.ok(ApiResponse.success(response, "Login successful"));
        } catch (Exception e) {
            throw e; // Let GlobalExceptionHandler handle it
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<LoginResponse>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            LoginResponse response = userService.register(registerRequest);
            return ResponseEntity.ok(ApiResponse.success(response, "Registration successful"));
        } catch (Exception e) {
            throw e; // Let GlobalExceptionHandler handle it
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        try {
            LoginResponse response = userService.refreshToken(request);
            return ResponseEntity.ok(ApiResponse.success(response, "Token refreshed successfully"));
        } catch (Exception e) {
            throw e; // Let GlobalExceptionHandler handle it
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(@RequestBody LogoutRequest request) {
        try {
            userService.logout(request.getRefreshToken());
            return ResponseEntity.ok(ApiResponse.success("Logout successful", "User logged out successfully"));
        } catch (Exception e) {
            throw e; // Let GlobalExceptionHandler handle it
        }
    }

    @PostMapping("/logout-all")
    public ResponseEntity<ApiResponse<String>> logoutAll() {
        try {
            // Get current user from SecurityContext
            String username = org.springframework.security.core.context.SecurityContextHolder
                    .getContext().getAuthentication().getName();
            userService.logoutAll(username);
            return ResponseEntity.ok(ApiResponse.success("All sessions logged out", "All sessions logged out successfully"));
        } catch (Exception e) {
            throw e; // Let GlobalExceptionHandler handle it
        }
    }

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("Auth service is running", "Service is healthy"));
    }
}
