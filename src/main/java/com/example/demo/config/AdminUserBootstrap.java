package com.example.demo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;

@Configuration
public class AdminUserBootstrap implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminUserBootstrap.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.bootstrap.admin-email:}")
    private String adminEmail;

    @Value("${app.bootstrap.admin-password:}")
    private String adminPassword;

    public AdminUserBootstrap(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!StringUtils.hasText(adminEmail) || !StringUtils.hasText(adminPassword)) {
            return;
        }
        String email = adminEmail.trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            return;
        }
        User u = new User();
        u.setEmail(email);
        u.setPasswordHash(passwordEncoder.encode(adminPassword));
        u.setFullName("System Admin");
        u.setRole(Role.ROLE_ADMIN);
        u.setEmailVerified(true);
        userRepository.save(u);
        log.warn("Da tao tai khoan admin: {} — hay doi mat khau ngay.", email);
    }
}
