package com.example.live_backend.repository.Experience;

import com.example.live_backend.model.Experience.Experience;
import com.example.live_backend.model.Experience.ExperienceShare;
import com.example.live_backend.model.User.User;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ExperienceShareRepository extends JpaRepository<ExperienceShare, Long> {
    List<ExperienceShare> findByExperienceId(Long experienceId);
    List<ExperienceShare> findBySharedWithId(Long userId);
    Optional<ExperienceShare> findByExperienceIdAndSharedWithId(Long experienceId, Long userId);
    void deleteByExperienceIdAndSharedWithId(Long experienceId, Long userId);

    // Find shares by user who received the share
    List<ExperienceShare> findBySharedWith(User user);

    // Check if a specific user already has access to an experience
    boolean existsByExperienceAndSharedWith(Experience experience, User sharedWith);

    // Remove a specific user's access
    void deleteByExperienceAndSharedWith(Experience experience, User sharedWith);
}
