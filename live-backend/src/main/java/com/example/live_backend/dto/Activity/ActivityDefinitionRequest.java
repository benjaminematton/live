package com.example.live_backend.dto.Activity;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class ActivityDefinitionRequest {
    @NotBlank(message = "Title is required")
    private String title;
    
    @NotBlank(message = "Location is required")
    private String location;
} 