package com.example.live_backend.repository.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.live_backend.model.User.ActiveExperienceParticipant;

public interface ActiveExperienceParticipantRepository extends JpaRepository<ActiveExperienceParticipant, Long> {
    Optional<ActiveExperienceParticipant> findByActiveExperienceIdAndUserId(Long activeExperienceId, Long userId);
}
