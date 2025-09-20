package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.UserResponse;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        // JWT token sẽ được validate tự động bởi JwtAuthenticationFilter
        // Nếu token không hợp lệ, request sẽ bị reject với 401
        UserResponse user = userService.getUserResponseById(id);
        return ResponseEntity.ok(ApiResponse.success(user, "User found"));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByUsername(@PathVariable String username) {
        // JWT token sẽ được validate tự động bởi JwtAuthenticationFilter
        UserResponse user = userService.getUserResponseByUsername(username);
        return ResponseEntity.ok(ApiResponse.success(user, "User found"));
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUserProfile() {
        // Lấy thông tin user hiện tại từ JWT token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        UserResponse user = userService.getUserResponseByUsername(username);
        return ResponseEntity.ok(ApiResponse.success(user, "Current user profile retrieved"));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Object>> getCurrentUserInfo() {
        // Lấy thông tin authentication hiện tại
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        return ResponseEntity.ok(ApiResponse.success(
            authentication.getPrincipal(), 
            "Current user authentication info"
        ));
    }
}
