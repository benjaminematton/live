package com.example.live_backend.mapper;

import com.example.live_backend.dto.Achivement.AchievementRequest;
import com.example.live_backend.dto.Achivement.AchievementResponse;
import com.example.live_backend.model.Achievement;
import org.springframework.stereotype.Component;

@Component
public class AchievementMapper {
    
    public AchievementResponse toResponse(Achievement achievement) {
        AchievementResponse response = new AchievementResponse();
        response.setId(achievement.getId());
        response.setCode(achievement.getCode());
        response.setName(achievement.getName());
        response.setDescription(achievement.getDescription());
        response.setIconUrl(achievement.getIconUrl());
        response.setPoints(achievement.getPoints());
        return response;
    }
    
    public Achievement toEntity(AchievementRequest request) {
        Achievement achievement = new Achievement();
        updateEntity(achievement, request);
        return achievement;
    }
    
    public void updateEntity(Achievement achievement, AchievementRequest request) {
        achievement.setCode(request.getCode());
        achievement.setName(request.getName());
        achievement.setDescription(request.getDescription());
        achievement.setIconUrl(request.getIconUrl());
        achievement.setPoints(request.getPoints()); 
    }
} 