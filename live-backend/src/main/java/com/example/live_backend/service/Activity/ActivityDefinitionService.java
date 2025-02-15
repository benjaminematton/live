package com.example.live_backend.service.Activity;
import com.example.live_backend.dto.Activity.ActivityDefinitionRequest;
import com.example.live_backend.dto.Activity.ActivityDefinitionResponse;
import com.example.live_backend.mapper.ActivityDefinitionMapper;
import com.example.live_backend.model.Activity.ActivityDefinition;
import com.example.live_backend.repository.Activity.ActivityDefinitionRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActivityDefinitionService {
    private final ActivityDefinitionRepository activityDefinitionRepository;
    private final ActivityDefinitionMapper activityDefinitionMapper;

    public ActivityDefinitionResponse getActivityDefinition(String activityName) {
        ActivityDefinition activityDefinition = activityDefinitionRepository.findByTitle(activityName);
        return activityDefinitionMapper.toResponse(activityDefinition);
    }

    public ActivityDefinitionResponse getActivityDefinition(String activityName, String location) {
        ActivityDefinition activityDefinition = activityDefinitionRepository.findByTitleAndLocation(activityName, location);
        return activityDefinitionMapper.toResponse(activityDefinition);
    }

    public ActivityDefinitionResponse createActivityDefinition(ActivityDefinitionRequest request) {
        ActivityDefinition activityDefinition = activityDefinitionMapper.toEntity(request);
        activityDefinitionRepository.save(activityDefinition);
        return activityDefinitionMapper.toResponse(activityDefinition);
    }

    public ActivityDefinitionResponse updateActivityDefinition(String activityName, ActivityDefinitionRequest request) {
        ActivityDefinition activityDefinition = activityDefinitionRepository.findByTitle(activityName);
        activityDefinitionMapper.updateEntity(activityDefinition, request);
        activityDefinitionRepository.save(activityDefinition);
        return activityDefinitionMapper.toResponse(activityDefinition);
    }
}
