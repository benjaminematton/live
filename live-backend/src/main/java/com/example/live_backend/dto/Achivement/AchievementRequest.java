package com.example.live_backend.dto.Achivement;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;

@Data
public class AchievementRequest {
    @NotBlank(message = "Code is required")
    private String code;
    
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    private String iconUrl;
    
    @Min(value = 0, message = "Points must be non-negative")
    private int points;
} 