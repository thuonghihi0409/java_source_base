package com.example.demo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Role;
import com.example.demo.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findByEmailVerificationToken(String token);

    Optional<User> findByPasswordResetToken(String token);

    Page<User> findByRole(Role role, Pageable pageable);

    Page<User> findByEmailContainingIgnoreCaseOrFullNameContainingIgnoreCase(String emailKeyword, String nameKeyword,
            Pageable pageable);

    Page<User> findByRoleAndEmailContainingIgnoreCaseOrRoleAndFullNameContainingIgnoreCase(
            Role role1, String emailKeyword, Role role2, String nameKeyword, Pageable pageable);
}
