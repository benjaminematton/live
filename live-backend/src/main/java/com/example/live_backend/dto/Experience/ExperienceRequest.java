package com.example.live_backend.dto.Experience;

import com.example.live_backend.dto.Activity.ActivityRequest;
import com.example.live_backend.model.Experience.ExperienceVisibility;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ExperienceRequest {
    @NotBlank
    private String title;
    
    @NotNull
    private LocalDateTime startDate;
    
    @NotNull
    private LocalDateTime endDate;
    
    private ExperienceVisibility visibility;
    
    @Valid
    private List<ActivityRequest> activities;
} 