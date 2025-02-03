package com.example.live_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "achievements")
public class Achievement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;       // e.g., "FIRST_POST", "VISIT_3_PLACES"
    private String name;       // e.g., "First Post", "Explorer"
    private String description;
    private String iconUrl;
    private int pointsAwarded;
}