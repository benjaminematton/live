package com.example.live_backend.controller;

import com.example.live_backend.dto.CreateScheduleRequest;
import com.example.live_backend.dto.ActivityDto;
import com.example.live_backend.dto.ShareScheduleRequest;
import com.example.live_backend.model.Activity;
import com.example.live_backend.model.Schedule;
import com.example.live_backend.security.CustomUserDetails;
import com.example.live_backend.service.ScheduleService;
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
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final ChatGPTService chatGPTService;

    @PostMapping
    public ResponseEntity<Schedule> createSchedule(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CreateScheduleRequest request) {
        Schedule schedule = scheduleService.createSchedule(userDetails.getUsername(), request);
        return ResponseEntity.ok(schedule);
    }

    @GetMapping
    public ResponseEntity<List<Schedule>> getUserSchedules(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<Schedule> schedules = scheduleService.getUserSchedules(userDetails.getUsername());
        return ResponseEntity.ok(schedules);
    }

    @GetMapping("/{scheduleId}")
    public ResponseEntity<Schedule> getScheduleById(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long scheduleId) {
        Schedule schedule = scheduleService.getScheduleById(scheduleId, userDetails.getUsername());
        return ResponseEntity.ok(schedule);
    }

    @PutMapping("/{scheduleId}")
    public ResponseEntity<Schedule> updateSchedule(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long scheduleId,
            @Valid @RequestBody CreateScheduleRequest request) {
        Schedule schedule = scheduleService.updateSchedule(scheduleId, userDetails.getUsername(), request);
        return ResponseEntity.ok(schedule);
    }

    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<Void> deleteSchedule(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long scheduleId) {
        scheduleService.deleteSchedule(scheduleId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{scheduleId}/activities")
    public ResponseEntity<List<Activity>> getScheduleActivities(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long scheduleId) {
        List<Activity> activities = scheduleService.getScheduleActivities(scheduleId, userDetails.getUsername());
        return ResponseEntity.ok(activities);
    }

    @DeleteMapping("/{scheduleId}/activities/{activityId}")
    public ResponseEntity<Void> deleteActivity(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long scheduleId,
            @PathVariable Long activityId) {
        scheduleService.deleteActivity(scheduleId, activityId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/suggest")
    public ResponseEntity<List<ActivityDto>> suggestSchedule(
            @RequestParam String prompt,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<ActivityDto> suggestions = chatGPTService.generateScheduleSuggestions(prompt, startDate, endDate);
        return ResponseEntity.ok(suggestions);
    }

    @PostMapping("/{scheduleId}/refine")
    public ResponseEntity<List<ActivityDto>> refineSchedule(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long scheduleId,
            @RequestParam String refinementPrompt) {
        Schedule schedule = scheduleService.getScheduleById(scheduleId, userDetails.getUsername());
        List<ActivityDto> currentActivities = schedule.getActivities().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        List<ActivityDto> refinedActivities = chatGPTService.refineSchedule(currentActivities, refinementPrompt);
        return ResponseEntity.ok(refinedActivities);
    }

    @PostMapping("/{scheduleId}/share")
    public ResponseEntity<Void> shareSchedule(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long scheduleId,
            @Valid @RequestBody ShareScheduleRequest request) {
        scheduleService.shareSchedule(scheduleId, userDetails.getUsername(), request.getUsernames());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{scheduleId}/share/{username}")
    public ResponseEntity<Void> unshareSchedule(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long scheduleId,
            @PathVariable String username) {
        scheduleService.unshareSchedule(scheduleId, userDetails.getUsername(), username);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/shared")
    public ResponseEntity<List<Schedule>> getSharedSchedules(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<Schedule> schedules = scheduleService.getSharedSchedules(userDetails.getUsername());
        return ResponseEntity.ok(schedules);
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