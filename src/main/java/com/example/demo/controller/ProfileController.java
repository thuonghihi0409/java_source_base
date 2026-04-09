package com.example.demo.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.nguoidung.NguoiDungAdminUpdateRequest;
import com.example.demo.dto.profile.ProfileResponse;
import com.example.demo.dto.profile.UpdateAvatarRequest;
import com.example.demo.dto.profile.UpdateProfileRequest;
import com.example.demo.entity.Role;
import com.example.demo.service.NguoiDungService;
import com.example.demo.service.ProfileService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = "*")
@Tag(name = "profile-controller", description = "Profile API cho CUSTOMER, HR, ADMIN")
public class ProfileController {

    private final ProfileService profileService;
    private final NguoiDungService nguoiDungService;

    public ProfileController(ProfileService profileService, NguoiDungService nguoiDungService) {
        this.profileService = profileService;
        this.nguoiDungService = nguoiDungService;
    }

    @GetMapping("/me")
    @Operation(summary = "Lay profile cua user hien tai")
    public ResponseEntity<ApiResponse<ProfileResponse>> myProfile() {
        return ResponseEntity.ok(ApiResponse.success(profileService.getMyProfile(), "OK"));
    }

    @PutMapping("/me")
    @Operation(summary = "Cap nhat thong tin profile co ban")
    public ResponseEntity<ApiResponse<ProfileResponse>> updateMyProfile(
            @Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(ApiResponse.success(profileService.updateMyProfile(request), "Profile updated"));
    }

    @PatchMapping("/me/avatar")
    @Operation(summary = "Cap nhat avatar URL")
    public ResponseEntity<ApiResponse<ProfileResponse>> updateAvatar(@Valid @RequestBody UpdateAvatarRequest request) {
        return ResponseEntity.ok(ApiResponse.success(profileService.updateMyAvatar(request), "Avatar updated"));
    }

    @GetMapping("/users/{userId}")
    @Operation(summary = "ADMIN/HR: xem profile cua user theo ID")
    public ResponseEntity<ApiResponse<ProfileResponse>> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success(nguoiDungService.getById(userId), "OK"));
    }

    @GetMapping("/users")
    @Operation(summary = "ADMIN/HR: danh sach user co phan trang + loc role + tu khoa")
    public ResponseEntity<ApiResponse<Page<ProfileResponse>>> searchUsers(
            @RequestParam(required = false) Role role,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                nguoiDungService.search(role, keyword, page, size), "OK"));
    }

    @PutMapping("/users/{userId}")
    @Operation(summary = "ADMIN: cap nhat role/trang thai thong tin user")
    public ResponseEntity<ApiResponse<ProfileResponse>> adminUpdateUser(
            @PathVariable Long userId,
            @RequestBody NguoiDungAdminUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                nguoiDungService.adminUpdate(userId, request), "User updated"));
    }
}
