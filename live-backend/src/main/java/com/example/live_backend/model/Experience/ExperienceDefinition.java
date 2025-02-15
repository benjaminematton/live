package com.example.live_backend.model.Experience;

import java.util.ArrayList;
import java.util.List;

import com.example.live_backend.model.Activity.ActivityDefinition;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "experience_definitions")
public class ExperienceDefinition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;        // e.g. "New York Pizza Tour"

    // Possibly aggregated rating or popularity metrics
    private double averageRating;
    private int totalRatings;
    private int popularity;  // like a usage count

    // Possibly a list of "Activities" if each definition can have multiple steps
    @OneToMany(mappedBy = "experienceDefinition", cascade = CascadeType.ALL)
    private List<ActivityDefinition> activities = new ArrayList<>();
}
