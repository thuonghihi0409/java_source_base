package com.example.demo.service;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.dto.RefreshTokenRequest;
import com.example.demo.dto.UserResponse;
import com.example.demo.entity.RefreshToken;
import com.example.demo.entity.User;
import com.example.demo.exception.ConflictException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.UnauthorizedException;
import com.example.demo.repository.RefreshTokenRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    public LoginResponse login(LoginRequest loginRequest) {
        // Find user by username
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new UnauthorizedException("Invalid username or password"));

        // Check if user is active
        if (!user.getIsActive()) {
            throw new UnauthorizedException("Account is deactivated");
        }

        // Validate password using BCrypt
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid username or password");
        }

        // Generate access token
        String accessToken = jwtUtil.generateToken(user.getUsername());
        
        // Generate refresh token
        String refreshTokenString = jwtUtil.generateRefreshToken(user.getUsername());
        
        // Save refresh token to database
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(refreshTokenString);
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(LocalDateTime.now().plusDays(7)); // 7 days
        refreshTokenRepository.save(refreshToken);

        return LoginResponse.create(
                accessToken,
                refreshTokenString,
                86400L, // 24 hours in seconds
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                "Login successful"
        );
    }

    public LoginResponse register(RegisterRequest registerRequest) {
        // Check if username already exists
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new ConflictException("Username already exists");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new ConflictException("Email already exists");
        }

        // Create new user
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword())); // Hash password with BCrypt
        user.setFullName(registerRequest.getFullName());
        user.setIsActive(true);

        User savedUser = userRepository.save(user);

        // Generate access token
        String accessToken = jwtUtil.generateToken(savedUser.getUsername());
        
        // Generate refresh token
        String refreshTokenString = jwtUtil.generateRefreshToken(savedUser.getUsername());
        
        // Save refresh token to database
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(refreshTokenString);
        refreshToken.setUser(savedUser);
        refreshToken.setExpiryDate(LocalDateTime.now().plusDays(7)); // 7 days
        refreshTokenRepository.save(refreshToken);

        return LoginResponse.create(
                accessToken,
                refreshTokenString,
                86400L, // 24 hours in seconds
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getFullName(),
                "Registration successful"
        );
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    }

    public UserResponse getUserResponseById(Long id) {
        User user = getUserById(id);
        return convertToUserResponse(user);
    }

    public UserResponse getUserResponseByUsername(String username) {
        User user = getUserByUsername(username);
        return convertToUserResponse(user);
    }

    // Refresh token methods
    public LoginResponse refreshToken(RefreshTokenRequest request) {
        // Validate refresh token
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        // Check if token is active
        if (!refreshToken.isActive()) {
            throw new UnauthorizedException("Refresh token is expired or revoked");
        }

        // Check if user is still active
        if (!refreshToken.getUser().getIsActive()) {
            throw new UnauthorizedException("User account is deactivated");
        }

        // Generate new access token
        String newAccessToken = jwtUtil.generateToken(refreshToken.getUser().getUsername());
        
        // Update refresh token last used
        refreshToken.updateLastUsed();
        refreshTokenRepository.save(refreshToken);

        return LoginResponse.create(
                newAccessToken,
                request.getRefreshToken(), // Keep same refresh token
                86400L, // 24 hours in seconds
                refreshToken.getUser().getUsername(),
                refreshToken.getUser().getEmail(),
                refreshToken.getUser().getFullName(),
                "Token refreshed successfully"
        );
    }

    public void logout(String refreshToken) {
        if (refreshToken != null && !refreshToken.isEmpty()) {
            refreshTokenRepository.findByToken(refreshToken)
                    .ifPresent(RefreshToken::revoke);
        }
    }

    public void logoutAll(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        refreshTokenRepository.revokeAllByUser(user);
    }

    public void revokeRefreshToken(String refreshToken) {
        refreshTokenRepository.revokeByToken(refreshToken);
    }

    public void cleanupExpiredTokens() {
        refreshTokenRepository.deleteExpiredTokens(LocalDateTime.now());
    }

    // Helper method để convert User entity sang UserResponse (không include password)
    private UserResponse convertToUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getIsActive(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

}
