package com.example.live_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.live_backend.model.Achievement;

public interface AchievementRepository extends JpaRepository<Achievement, Long> {
    Optional<Achievement> findByCode(String code);
}
