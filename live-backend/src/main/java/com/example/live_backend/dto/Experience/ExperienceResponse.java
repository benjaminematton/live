package com.example.live_backend.dto.Experience;

import com.example.live_backend.dto.Activity.ActivityResponse;
import com.example.live_backend.model.Experience.ExperienceVisibility;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ExperienceResponse {
    private Long id;
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private ExperienceVisibility visibility;
    private List<ActivityResponse> activities;
    private String username;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 