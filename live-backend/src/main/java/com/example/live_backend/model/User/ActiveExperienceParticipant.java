package com.example.live_backend.model.User;

import java.time.LocalDateTime;

import com.example.live_backend.model.Experience.ActiveExperience;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class ActiveExperienceParticipant {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "active_experience_id", nullable = false)
    private ActiveExperience activeExperience;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // user-specific data, e.g. location
    private Double latitude;
    private Double longitude;
    private LocalDateTime lastLocationUpdate;
}
