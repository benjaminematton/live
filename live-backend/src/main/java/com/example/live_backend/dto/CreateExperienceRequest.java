package com.example.live_backend.dto;

import com.example.live_backend.model.ExperienceVisibility;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreateExperienceRequest {
    @NotBlank
    private String title;
    
    private String description;
    
    @NotNull
    private LocalDateTime startDate;
    
    @NotNull
    private LocalDateTime endDate;
    
    private ExperienceVisibility visibility;
    
    @Valid
    private List<ActivityDto> activities;

    @NotNull
    private String username;
    
    private List<String> shareWithUsernames;
} 