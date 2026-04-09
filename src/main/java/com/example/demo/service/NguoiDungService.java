package com.example.demo.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.nguoidung.NguoiDungAdminUpdateRequest;
import com.example.demo.dto.profile.ProfileResponse;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.UserRepository;

@Service
@Transactional
public class NguoiDungService {

    private final UserRepository userRepository;

    public NguoiDungService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public ProfileResponse getById(Long id) {
        return toProfile(find(id));
    }

    @Transactional(readOnly = true)
    public Page<ProfileResponse> search(Role role, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100),
                Sort.by(Sort.Direction.DESC, "createdAt"));
        String kw = keyword == null ? null : keyword.trim();
        Page<User> users;
        if (kw == null || kw.isBlank()) {
            users = role == null ? userRepository.findAll(pageable) : userRepository.findByRole(role, pageable);
        } else if (role == null) {
            users = userRepository.findByEmailContainingIgnoreCaseOrFullNameContainingIgnoreCase(kw, kw, pageable);
        } else {
            users = userRepository.findByRoleAndEmailContainingIgnoreCaseOrRoleAndFullNameContainingIgnoreCase(
                    role, kw, role, kw, pageable);
        }
        return users.map(this::toProfile);
    }

    public ProfileResponse adminUpdate(Long id, NguoiDungAdminUpdateRequest request) {
        User u = find(id);
        if (request.getFullName() != null) {
            u.setFullName(request.getFullName());
        }
        if (request.getPhone() != null) {
            u.setPhone(request.getPhone());
        }
        if (request.getEnabled() != null) {
            u.setEnabled(request.getEnabled());
        }
        if (request.getRole() != null) {
            u.setRole(request.getRole());
        }
        return toProfile(userRepository.save(u));
    }

    private User find(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
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
