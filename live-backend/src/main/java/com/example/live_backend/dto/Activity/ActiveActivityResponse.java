package com.example.live_backend.dto.Activity;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ActiveActivityResponse {
    private Long id;
    private Long activityId;
    private LocalDateTime photoPromptTime;
    private boolean photoSubmitted;
    private String photoUrl;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean skipped;
    private boolean completed;
}
