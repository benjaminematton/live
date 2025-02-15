package com.example.live_backend.mapper;

import org.springframework.stereotype.Component;

import com.example.live_backend.dto.Activity.ActiveExperienceRequest;
import com.example.live_backend.dto.Activity.ActiveExperienceResponse;
import com.example.live_backend.model.Experience.ActiveExperience;
import com.example.live_backend.model.Experience.Experience;
import com.example.live_backend.model.User.User;

@Component
public class ActiveExperienceMapper {
    public ActiveExperienceResponse toResponse(ActiveExperience activeExperience) {
        ActiveExperienceResponse response = new ActiveExperienceResponse();
        response.setId(activeExperience.getId());
        response.setExperience(activeExperience.getExperience());
        response.setUser(activeExperience.getUser());
        response.setCurrentActivityIndex(activeExperience.getCurrentActivityIndex());
        response.setCompleted(activeExperience.isCompleted());
        return response;
    }

    public ActiveExperience toEntity(ActiveExperienceRequest activeExperienceRequest, User user, Experience experience) {
        ActiveExperience activeExperience = new ActiveExperience();
        activeExperience.setExperience(experience);
        activeExperience.setUser(user);
        return activeExperience;
    }
}
