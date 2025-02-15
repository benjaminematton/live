package com.example.live_backend.model.Activity;

import java.time.LocalDateTime;

import com.example.live_backend.model.Experience.ActiveExperience;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@Data
public class ActiveActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "active_experience_id", nullable = false)
    private ActiveExperience activeExperience;

    @OneToOne
    @JoinColumn(name = "activity_id", nullable = false)
    private Activity activity;

    // The random time within the first 20 mins
    private LocalDateTime photoPromptTime;
    private boolean photoSubmitted;  
    private String photoUrl; // If stored in S3 or local, store the URL

    private LocalDateTime startTime;
    private LocalDateTime endTime; 

    private boolean skipped = false;
    private boolean completed = false;
}
