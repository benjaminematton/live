package com.example.live_backend.repository;

import com.example.live_backend.model.Experience;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ExperienceRepository extends JpaRepository<Experience, Long> {
    List<Experience> findByUserIdOrderByStartDateDesc(Long userId);
    List<Experience> findByUserUsernameOrderByStartDateDesc(String username);
} 