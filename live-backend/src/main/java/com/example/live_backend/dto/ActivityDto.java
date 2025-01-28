package com.example.live_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ActivityDto {
    @NotBlank
    private String title;
    
    private String description;
    
    @NotNull
    private String location;
    
    private LocalDateTime startTime;
    
    private LocalDateTime endTime;
} 