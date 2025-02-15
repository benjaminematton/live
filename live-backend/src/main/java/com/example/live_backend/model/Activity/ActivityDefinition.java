package com.example.live_backend.model.Activity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "activities_definitions")  
public class ActivityDefinition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String location;
}
