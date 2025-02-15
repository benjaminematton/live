package com.example.live_backend.dto.User;

import java.time.LocalDateTime;

import com.example.live_backend.dto.Achivement.AchievementResponse;

import lombok.Data;

@Data
public class UserAchievementResponse {
    private Long id;
    private Long userId;
    private AchievementResponse achievement;
    private LocalDateTime dateEarned;
    private int points;
}