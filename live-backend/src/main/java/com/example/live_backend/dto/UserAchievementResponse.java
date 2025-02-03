package com.example.live_backend.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class UserAchievementResponse {
    private Long id;
    private Long userId;
    private AchievementReponse achievement;
    private LocalDateTime dateEarned;
    private int pointsAwarded;
    
}