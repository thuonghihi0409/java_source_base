package com.example.demo.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.profile.ProfileResponse;
import com.example.demo.dto.profile.UpdateAvatarRequest;
import com.example.demo.dto.profile.UpdateProfileRequest;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.UnauthorizedException;
import com.example.demo.repository.UserRepository;

@Service
public class ProfileService {

    private final UserRepository userRepository;

    public ProfileService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public ProfileResponse getMyProfile() {
        return toProfile(currentUser());
    }

    @Transactional
    public ProfileResponse updateMyProfile(UpdateProfileRequest request) {
        User user = currentUser();
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setHeadline(request.getHeadline());
        user.setBio(request.getBio());
        return toProfile(userRepository.save(user));
    }

    @Transactional
    public ProfileResponse updateMyAvatar(UpdateAvatarRequest request) {
        User user = currentUser();
        user.setAvatarUrl(request.getAvatarUrl().trim());
        return toProfile(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public ProfileResponse getUserProfileById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        return toProfile(user);
    }

    @Transactional(readOnly = true)
    public Page<ProfileResponse> searchUsers(Role role, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100),
                Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<User> users;
        String kw = keyword == null ? null : keyword.trim();

        if (kw == null || kw.isBlank()) {
            users = role == null ? userRepository.findAll(pageable) : userRepository.findByRole(role, pageable);
        } else {
            if (role == null) {
                users = userRepository.findByEmailContainingIgnoreCaseOrFullNameContainingIgnoreCase(kw, kw, pageable);
            } else {
                users = userRepository.findByRoleAndEmailContainingIgnoreCaseOrRoleAndFullNameContainingIgnoreCase(
                        role, kw, role, kw, pageable);
            }
        }
        return users.map(this::toProfile);
    }

    private User currentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof org.springframework.security.core.userdetails.User userDetails) {
            return userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "email", userDetails.getUsername()));
        }
        throw new UnauthorizedException("Chua dang nhap");
    }

    private ProfileResponse toProfile(User user) {
        return new ProfileResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getPhone(),
                user.getAvatarUrl(),
                user.getAddress(),
                user.getHeadline(),
                user.getBio(),
                user.getRole(),
                user.isEmailVerified(),
                user.isEnabled(),
                user.getCreatedAt(),
                user.getUpdatedAt());
    }
}
