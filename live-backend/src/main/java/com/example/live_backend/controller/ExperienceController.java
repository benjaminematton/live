package com.example.live_backend.controller;

import com.example.live_backend.dto.CreateExperienceRequest;
import com.example.live_backend.dto.ActivityDto;
import com.example.live_backend.model.Activity;
import com.example.live_backend.model.Experience;
import com.example.live_backend.security.CustomUserDetails;
import com.example.live_backend.service.ExperienceService;
import com.example.live_backend.service.ChatGPTService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/experiences")
@RequiredArgsConstructor
public class ExperienceController {

    private final ExperienceService experienceService;
    private final ChatGPTService chatGPTService;

    @PostMapping
    public ResponseEntity<Experience> createExperience(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CreateExperienceRequest request) {
        Experience experience = experienceService.createExperience(userDetails.getUsername(), request);
        return ResponseEntity.ok(experience);
    }

    @GetMapping
    public ResponseEntity<List<Experience>> getUserExperiences(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<Experience> experiences = experienceService.getUserExperiences(userDetails.getUsername());
        return ResponseEntity.ok(experiences);
    }

    @GetMapping("/{scheduleId}")
    public ResponseEntity<Experience> getExperienceById(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long experienceId) {
        Experience experience = experienceService.getExperienceById(experienceId, userDetails.getUsername());
        return ResponseEntity.ok(experience);
    }

    @PutMapping("/{scheduleId}")
    public ResponseEntity<Experience> updateExperience(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long experienceId,
            @Valid @RequestBody CreateExperienceRequest request) {
        Experience experience = experienceService.updateExperience(experienceId, userDetails.getUsername(), request);
        return ResponseEntity.ok(experience);
    }

    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<Void> deleteExperience(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long experienceId) {
        experienceService.deleteExperience(experienceId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{experienceId}/activities")
    public ResponseEntity<List<Activity>> getExperienceActivities(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long experienceId) {
        List<Activity> activities = experienceService.getExperienceActivities(experienceId, userDetails.getUsername());
        return ResponseEntity.ok(activities);
    }

    @DeleteMapping("/{experienceId}/activities/{activityId}")
    public ResponseEntity<Void> deleteActivity(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long experienceId,
            @PathVariable Long activityId) {
        experienceService.deleteActivity(experienceId, activityId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/suggest")
    public ResponseEntity<List<ActivityDto>> suggestExperience(
            @RequestParam String prompt,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam String location) {
        List<ActivityDto> suggestions = chatGPTService.generateExperienceSuggestions(prompt, startDate, endDate, location);
        return ResponseEntity.ok(suggestions);
    }

    @PostMapping("/{scheduleId}/refine")
    public ResponseEntity<List<ActivityDto>> refineSchedule(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long experienceId,
            @RequestParam String refinementPrompt) {
        Experience experience = experienceService.getExperienceById(experienceId, userDetails.getUsername());
        List<ActivityDto> currentActivities = experience.getActivities().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        List<ActivityDto> refinedActivities = chatGPTService.refineExperience(currentActivities, refinementPrompt);
        return ResponseEntity.ok(refinedActivities);
    }

    private ActivityDto convertToDto(Activity activity) {
        ActivityDto dto = new ActivityDto();
        dto.setTitle(activity.getTitle());
        dto.setDescription(activity.getDescription());
        dto.setLocation(activity.getLocation());
        dto.setStartTime(activity.getStartTime());
        dto.setEndTime(activity.getEndTime());
        return dto;
    }
} 