package com.example.live_backend.service;

import com.example.live_backend.dto.ActivityDto;
import com.example.live_backend.dto.CreateScheduleRequest;
import com.example.live_backend.model.Activity;
import com.example.live_backend.model.Schedule;
import com.example.live_backend.model.User;
import com.example.live_backend.repository.ActivityRepository;
import com.example.live_backend.repository.ScheduleRepository;
import com.example.live_backend.repository.UserRepository;
import com.example.live_backend.repository.ScheduleShareRepository;
import com.example.live_backend.model.ScheduleShare;
import com.example.live_backend.model.ScheduleVisibility;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;
    private final ScheduleShareRepository scheduleShareRepository;

    @Transactional
    public Schedule createSchedule(String username, CreateScheduleRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Schedule schedule = new Schedule();
        schedule.setTitle(request.getTitle());
        schedule.setDescription(request.getDescription());
        schedule.setStartDate(request.getStartDate());
        schedule.setEndDate(request.getEndDate());
        schedule.setVisibility(request.getVisibility());
        schedule.setUser(user);

        Schedule savedSchedule = scheduleRepository.save(schedule);

        if (request.getActivities() != null && !request.getActivities().isEmpty()) {
            List<Activity> activities = request.getActivities().stream()
                    .map(activityDto -> createActivityFromDto(activityDto, savedSchedule))
                    .collect(Collectors.toList());
            savedSchedule.setActivities(activities);
        }

        return savedSchedule;
    }

    public List<Schedule> getUserSchedules(String username) {
        return scheduleRepository.findByUserUsernameOrderByStartDateDesc(username);
    }

    public Schedule getScheduleById(Long scheduleId, String username) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Check if user is the owner
        if (schedule.getUser().getId().equals(user.getId())) {
            return schedule;
        }

        // Check if schedule is shared with the user
        if (scheduleShareRepository.findByScheduleIdAndSharedWithId(scheduleId, user.getId()).isPresent()) {
            return schedule;
        }

        // Check if schedule is public
        if (schedule.getVisibility() == ScheduleVisibility.PUBLIC) {
            return schedule;
        }

        throw new RuntimeException("Unauthorized access to schedule");
    }

    @Transactional
    public Schedule updateSchedule(Long scheduleId, String username, CreateScheduleRequest request) {
        Schedule schedule = getScheduleById(scheduleId, username);

        schedule.setTitle(request.getTitle());
        schedule.setDescription(request.getDescription());
        schedule.setStartDate(request.getStartDate());
        schedule.setEndDate(request.getEndDate());
        schedule.setVisibility(request.getVisibility());

        // Clear existing activities and add new ones
        schedule.getActivities().clear();
        if (request.getActivities() != null) {
            List<Activity> activities = request.getActivities().stream()
                    .map(activityDto -> createActivityFromDto(activityDto, schedule))
                    .collect(Collectors.toList());
            schedule.getActivities().addAll(activities);
        }

        return scheduleRepository.save(schedule);
    }

    @Transactional
    public void deleteSchedule(Long scheduleId, String username) {
        Schedule schedule = getScheduleById(scheduleId, username);
        scheduleRepository.delete(schedule);
    }

    private Activity createActivityFromDto(ActivityDto activityDto, Schedule schedule) {
        Activity activity = new Activity();
        activity.setTitle(activityDto.getTitle());
        activity.setDescription(activityDto.getDescription());
        activity.setLocation(activityDto.getLocation());
        activity.setStartTime(activityDto.getStartTime());
        activity.setEndTime(activityDto.getEndTime());
        activity.setSchedule(schedule);
        return activity;
    }

    public List<Activity> getScheduleActivities(Long scheduleId, String username) {
        Schedule schedule = getScheduleById(scheduleId, username);
        return activityRepository.findByScheduleIdOrderByStartTime(scheduleId);
    }

    @Transactional
    public void deleteActivity(Long scheduleId, Long activityId, String username) {
        Schedule schedule = getScheduleById(scheduleId, username);
        Activity activity = schedule.getActivities().stream()
                .filter(a -> a.getId().equals(activityId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Activity not found"));

        schedule.getActivities().remove(activity);
        activityRepository.delete(activity);
    }

    @Transactional
    public void shareSchedule(Long scheduleId, String ownerUsername, List<String> targetUsernames) {
        Schedule schedule = getScheduleById(scheduleId, ownerUsername);
        
        for (String username : targetUsernames) {
            User targetUser = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found: " + username));
                
            if (scheduleShareRepository.findByScheduleIdAndSharedWithId(scheduleId, targetUser.getId()).isPresent()) {
                continue; // Skip if already shared
            }

            ScheduleShare share = new ScheduleShare();
            share.setSchedule(schedule);
            share.setSharedWith(targetUser);
            scheduleShareRepository.save(share);
        }
    }

    @Transactional
    public void unshareSchedule(Long scheduleId, String ownerUsername, String targetUsername) {
        Schedule schedule = getScheduleById(scheduleId, ownerUsername);
        User targetUser = userRepository.findByUsername(targetUsername)
                .orElseThrow(() -> new RuntimeException("User not found: " + targetUsername));
                
        scheduleShareRepository.deleteByScheduleIdAndSharedWithId(scheduleId, targetUser.getId());
    }

    public List<Schedule> getSharedSchedules(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
                
        return scheduleShareRepository.findBySharedWithId(user.getId()).stream()
                .map(ScheduleShare::getSchedule)
                .collect(Collectors.toList());
    }
} 