package com.example.live_backend.mapper;

import org.springframework.stereotype.Component;

import com.example.live_backend.dto.Activity.ActiveActivityResponse;
import com.example.live_backend.model.Activity.ActiveActivity;
import com.example.live_backend.model.Activity.Activity;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ActiveActivityMapper {

    public ActiveActivityResponse toResponse(ActiveActivity activeActivity) {
        ActiveActivityResponse response = new ActiveActivityResponse();
        response.setId(activeActivity.getId());
        response.setActivityId(activeActivity.getActivity().getId());
        response.setPhotoPromptTime(activeActivity.getPhotoPromptTime());
        response.setPhotoSubmitted(activeActivity.isPhotoSubmitted());
        response.setPhotoUrl(activeActivity.getPhotoUrl());
        response.setStartTime(activeActivity.getStartTime());
        response.setEndTime(activeActivity.getEndTime());
        response.setSkipped(activeActivity.isSkipped());
        response.setCompleted(activeActivity.isCompleted());
        return response;
    }

    public ActiveActivity toEntity(ActiveActivityResponse response, Activity activity) {
        ActiveActivity activeActivity = new ActiveActivity();
        activeActivity.setId(response.getId());
        activeActivity.setActivity(activity);
        activeActivity.setPhotoPromptTime(response.getPhotoPromptTime());
        activeActivity.setPhotoSubmitted(response.isPhotoSubmitted());
        activeActivity.setPhotoUrl(response.getPhotoUrl());
        return activeActivity;
    }
}
