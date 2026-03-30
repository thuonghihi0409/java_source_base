package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.auth.AuthResponse;
import com.example.demo.dto.auth.ChangePasswordRequest;
import com.example.demo.dto.auth.ForgotPasswordRequest;
import com.example.demo.dto.auth.LoginRequest;
import com.example.demo.dto.auth.LogoutRequest;
import com.example.demo.dto.auth.RefreshTokenRequest;
import com.example.demo.dto.auth.RegisterRequest;
import com.example.demo.dto.auth.ResetPasswordRequest;
import com.example.demo.dto.auth.UserSummaryDto;
import com.example.demo.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@Tag(name = "auth-controller", description = "Dang ky, dang nhap, JWT, xac thuc email")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Dang ky (mac dinh role CUSTOMER)")
    public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok(ApiResponse.success("Dang ky thanh cong. Vui long kiem tra email de xac thuc.", "OK"));
    }

    @PostMapping("/login")
    @Operation(summary = "Dang nhap — tra access + refresh token")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse res = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(res, "Dang nhap thanh cong"));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Lam moi access token bang refresh token")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse res = authService.refresh(request);
        return ResponseEntity.ok(ApiResponse.success(res, "Token da duoc lam moi"));
    }

    @GetMapping("/verify-email")
    @Operation(summary = "Xac thuc email (mo link trong email)")
    public ResponseEntity<ApiResponse<String>> verifyEmail(@RequestParam String token) {
        String msg = authService.verifyEmail(token);
        return ResponseEntity.ok(ApiResponse.success(msg, msg));
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Quen mat khau — gui email dat lai (neu email ton tai)")
    public ResponseEntity<ApiResponse<String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok(ApiResponse.success(
                "Neu email ton tai, ban se nhan huong dan dat lai mat khau.",
                "OK"));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Dat lai mat khau bang token trong email")
    public ResponseEntity<ApiResponse<String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.success("Mat khau da duoc dat lai. Vui long dang nhap lai.", "OK"));
    }

    @PostMapping("/change-password")
    @Operation(summary = "Doi mat khau (can Bearer access token)")
    public ResponseEntity<ApiResponse<String>> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(request);
        return ResponseEntity.ok(ApiResponse.success("Doi mat khau thanh cong. Vui long dang nhap lai.", "OK"));
    }

    @PostMapping("/logout")
    @Operation(summary = "Dang xuat — thu hoi refresh token (body tuy chon)")
    public ResponseEntity<ApiResponse<String>> logout(@RequestBody(required = false) LogoutRequest request) {
        if (request == null) {
            request = new LogoutRequest();
        }
        authService.logout(request);
        return ResponseEntity.ok(ApiResponse.success("Dang xuat thanh cong", "OK"));
    }

    @GetMapping("/me")
    @Operation(summary = "Thong tin user hien tai")
    public ResponseEntity<ApiResponse<UserSummaryDto>> me() {
        UserSummaryDto dto = authService.me();
        return ResponseEntity.ok(ApiResponse.success(dto, "OK"));
    }
}
