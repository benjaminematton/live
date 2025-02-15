package com.example.live_backend.mapper;

import com.example.live_backend.dto.Activity.ActivityRequest;
import com.example.live_backend.dto.Activity.ActivityResponse;
import com.example.live_backend.model.Activity.Activity;


public class ActivityMapper {

    /**
     * Convert from a request DTO to a new Activity entity.
     * Used when creating a new Activity or updating from the DTO fields.
     */
    public Activity toEntity(ActivityRequest request) {
        Activity entity = new Activity();
        entity.setTitle(request.getTitle());
        entity.setDescription(request.getDescription());
        entity.setLocation(request.getLocation());
        entity.setStartTime(request.getStartTime());
        entity.setEndTime(request.getEndTime());
        return entity;
    }

    /**
     * Convert from an Activity entity to a response DTO.
     * Used when returning data to the client.
     */
    public ActivityResponse toResponse(Activity entity) {
        ActivityResponse response = new ActivityResponse();
        response.setId(entity.getId());
        response.setTitle(entity.getTitle());
        response.setDescription(entity.getDescription());
        response.setLocation(entity.getLocation());
        response.setStartTime(entity.getStartTime());
        response.setEndTime(entity.getEndTime());
        return response;
    }

    /**
     * If you want to update an existing Activity entity in place
     * (e.g., partial or full update), you can do something like:
     */
    public void updateEntity(Activity entity, ActivityRequest request) {
        entity.setTitle(request.getTitle());
        entity.setDescription(request.getDescription());
        entity.setLocation(request.getLocation());
        entity.setStartTime(request.getStartTime());
        entity.setEndTime(request.getEndTime());
        // We don't change createdAt or updatedAt here; JPA @PreUpdate handles updatedAt
    }
}

