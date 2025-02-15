package com.example.live_backend.repository.Experience;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.live_backend.model.Experience.ActiveExperience;
import com.example.live_backend.model.Experience.Experience;
public interface ActiveExperienceRepository extends JpaRepository<ActiveExperience, Long> {
    boolean existsByExperience(Experience experience);
}
