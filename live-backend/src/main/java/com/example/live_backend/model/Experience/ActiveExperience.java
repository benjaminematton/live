package com.example.live_backend.model.Experience;

import java.time.LocalDateTime;

import com.example.live_backend.model.User.User;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "active_experiences")
public class ActiveExperience {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "experience_id", nullable = false)
    private Experience experience;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private LocalDateTime startTime = LocalDateTime.now();
    private LocalDateTime endTime;

    private int currentActivityIndex = 0;

    private boolean completed = false;

    private LocalDateTime photoPromptTime;

    private boolean isActive = true;
}
