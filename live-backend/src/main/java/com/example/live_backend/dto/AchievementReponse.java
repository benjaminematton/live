package com.example.live_backend.dto;

import lombok.Data;

@Data
public class AchievementReponse {
    private Long id;
    private String code;
    private String name;
    private String description;
    private String iconUrl;
    private int pointsAwarded;
}
