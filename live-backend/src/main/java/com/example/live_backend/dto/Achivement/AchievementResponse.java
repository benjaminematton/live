package com.example.live_backend.dto.Achivement;

import lombok.Data;

@Data
public class AchievementResponse {
    private Long id;
    private String code;
    private String name;
    private String description;
    private String iconUrl;
    private int points;
}