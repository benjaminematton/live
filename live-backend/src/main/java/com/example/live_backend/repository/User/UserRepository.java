package com.example.live_backend.repository.User;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.live_backend.model.User.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
} 