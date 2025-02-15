package com.example.live_backend.dto.Activity;

import java.time.LocalDateTime;
import java.util.List;

import com.example.live_backend.model.Experience.Experience;
import com.example.live_backend.model.User.User;

import lombok.Data;

@Data
public class ActiveExperienceResponse {
    private Long id;
    private Experience experience;
    private User user;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int currentActivityIndex;
    private boolean completed;
    private List<LocalDateTime> photoPromptTime;
}
