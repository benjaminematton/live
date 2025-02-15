package com.example.live_backend.mapper;

import com.example.live_backend.dto.Activity.ActivityDefinitionRequest;
import com.example.live_backend.dto.Activity.ActivityDefinitionResponse;
import com.example.live_backend.model.Activity.ActivityDefinition;

import org.springframework.stereotype.Component;

@Component
public class ActivityDefinitionMapper {
    
    public ActivityDefinitionResponse toResponse(ActivityDefinition activity) {
        return ActivityDefinitionResponse.builder()
                .id(activity.getId())
                .title(activity.getTitle())
                .location(activity.getLocation())
                .build();
    }
    
    public ActivityDefinition toEntity(ActivityDefinitionRequest request) {
        ActivityDefinition activity = new ActivityDefinition();
        activity.setTitle(request.getTitle());
        activity.setLocation(request.getLocation());
        return activity;
    }
    
    public ActivityDefinition updateEntity(ActivityDefinition activity, ActivityDefinitionRequest request) {
        activity.setTitle(request.getTitle());
        activity.setLocation(request.getLocation());
        return activity;
    }
}
