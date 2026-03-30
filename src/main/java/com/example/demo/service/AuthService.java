package com.example.demo.service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.auth.AuthResponse;
import com.example.demo.dto.auth.ChangePasswordRequest;
import com.example.demo.dto.auth.ForgotPasswordRequest;
import com.example.demo.dto.auth.LoginRequest;
import com.example.demo.dto.auth.LogoutRequest;
import com.example.demo.dto.auth.RefreshTokenRequest;
import com.example.demo.dto.auth.RegisterRequest;
import com.example.demo.dto.auth.ResetPasswordRequest;
import com.example.demo.dto.auth.UserSummaryDto;
import com.example.demo.entity.RefreshToken;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ConflictException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.UnauthorizedException;
import com.example.demo.repository.RefreshTokenRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtUtil;

@Service
public class AuthService {

    private static final SecureRandom RANDOM = new SecureRandom();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EmailService emailService;

    @Value("${jwt.expiration}")
    private long accessTokenExpirationMs;

    @Value("${jwt.refresh-expiration}")
    private long refreshTokenExpirationMs;

    @Value("${app.auth.require-email-verification-for-login:true}")
    private boolean requireEmailVerificationForLogin;

    @Value("${app.backend-public-url:http://localhost:8080}")
    private String backendPublicUrl;

    @Value("${app.frontend-base-url:http://localhost:3000}")
    private String frontendBaseUrl;

    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail().trim().toLowerCase())) {
            throw new ConflictException("Email da duoc su dung");
        }

        User user = new User();
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setRole(Role.ROLE_CUSTOMER);
        user.setEmailVerified(false);

        String token = java.util.UUID.randomUUID().toString();
        user.setEmailVerificationToken(token);
        user.setEmailVerificationExpiresAt(Instant.now().plus(48, ChronoUnit.HOURS));

        userRepository.save(user);

        String verifyUrl = backendPublicUrl.replaceAll("/$", "")
                + "/api/auth/verify-email?token=" + token;
        emailService.sendVerificationEmail(user.getEmail(), verifyUrl);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail().trim().toLowerCase())
                .orElseThrow(() -> new UnauthorizedException("Email hoac mat khau khong dung"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Email hoac mat khau khong dung");
        }

        if (!user.isEnabled()) {
            throw new UnauthorizedException("Tai khoan da bi khoa");
        }

        if (requireEmailVerificationForLogin && !user.isEmailVerified()) {
            throw new UnauthorizedException("Vui long xac thuc email truoc khi dang nhap");
        }

        return issueTokens(user);
    }

    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {
        RefreshToken stored = refreshTokenRepository.findByTokenAndRevokedFalse(request.getRefreshToken())
                .orElseThrow(() -> new BadRequestException("Refresh token khong hop le"));

        if (stored.getExpiresAt().isBefore(Instant.now())) {
            throw new BadRequestException("Refresh token het han");
        }

        User user = stored.getUser();
        stored.setRevoked(true);
        refreshTokenRepository.save(stored);

        return issueTokens(user);
    }

    @Transactional
    public String verifyEmail(String token) {
        User user = userRepository.findByEmailVerificationToken(token)
                .orElseThrow(() -> new BadRequestException("Token xac thuc khong hop le"));

        if (user.getEmailVerificationExpiresAt() != null
                && user.getEmailVerificationExpiresAt().isBefore(Instant.now())) {
            throw new BadRequestException("Token xac thuc da het han");
        }

        user.setEmailVerified(true);
        user.setEmailVerificationToken(null);
        user.setEmailVerificationExpiresAt(null);
        userRepository.save(user);

        return "Email da duoc xac thuc thanh cong";
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        userRepository.findByEmail(email).ifPresent(user -> {
            String token = java.util.UUID.randomUUID().toString();
            user.setPasswordResetToken(token);
            user.setPasswordResetExpiresAt(Instant.now().plus(1, ChronoUnit.HOURS));
            userRepository.save(user);

            String resetUrl = frontendBaseUrl.replaceAll("/$", "") + "/reset-password?token=" + token;
            emailService.sendPasswordResetEmail(user.getEmail(), resetUrl);
        });
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByPasswordResetToken(request.getToken())
                .orElseThrow(() -> new BadRequestException("Token dat lai mat khau khong hop le"));

        if (user.getPasswordResetExpiresAt() == null
                || user.getPasswordResetExpiresAt().isBefore(Instant.now())) {
            throw new BadRequestException("Token dat lai mat khau da het han");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        user.setPasswordResetToken(null);
        user.setPasswordResetExpiresAt(null);
        userRepository.save(user);

        refreshTokenRepository.deleteByUser(user);
    }

    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        String email = currentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Mat khau hien tai khong dung");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        refreshTokenRepository.deleteByUser(user);
    }

    @Transactional
    public void logout(LogoutRequest request) {
        String email = currentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        if (request.getRefreshToken() != null && !request.getRefreshToken().isBlank()) {
            refreshTokenRepository.findByTokenAndRevokedFalse(request.getRefreshToken()).ifPresent(rt -> {
                if (!rt.getUser().getId().equals(user.getId())) {
                    throw new BadRequestException("Refresh token khong thuoc tai khoan hien tai");
                }
                rt.setRevoked(true);
                refreshTokenRepository.save(rt);
            });
        } else {
            refreshTokenRepository.deleteByUser(user);
        }
    }

    @Transactional(readOnly = true)
    public UserSummaryDto me() {
        String email = currentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return toSummary(user);
    }

    private String currentUserEmail() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof org.springframework.security.core.userdetails.User u) {
            return u.getUsername();
        }
        throw new UnauthorizedException("Chua dang nhap");
    }

    private AuthResponse issueTokens(User user) {
        String access = jwtUtil.generateAccessToken(user);
        String rawRefresh = newRefreshTokenValue();
        Instant exp = Instant.now().plusMillis(refreshTokenExpirationMs);

        RefreshToken rt = new RefreshToken();
        rt.setToken(rawRefresh);
        rt.setUser(user);
        rt.setExpiresAt(exp);
        rt.setRevoked(false);
        refreshTokenRepository.save(rt);

        AuthResponse res = new AuthResponse();
        res.setAccessToken(access);
        res.setRefreshToken(rawRefresh);
        res.setTokenType("Bearer");
        res.setExpiresInMs(accessTokenExpirationMs);
        res.setUser(toSummary(user));
        return res;
    }

    private static String newRefreshTokenValue() {
        byte[] buf = new byte[48];
        RANDOM.nextBytes(buf);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(buf);
    }

    private static UserSummaryDto toSummary(User user) {
        return new UserSummaryDto(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole(),
                user.isEmailVerified());
    }
}
