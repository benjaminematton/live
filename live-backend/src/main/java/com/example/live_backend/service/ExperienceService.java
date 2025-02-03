package com.example.live_backend.service;

import com.example.live_backend.dto.ActivityDto;
import com.example.live_backend.dto.CreateExperienceRequest;
import com.example.live_backend.model.Activity;
import com.example.live_backend.model.Experience;
import com.example.live_backend.model.User;
import com.example.live_backend.repository.ActivityRepository;
import com.example.live_backend.repository.ExperienceRepository;
import com.example.live_backend.repository.UserRepository;
import com.example.live_backend.model.ExperienceVisibility;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExperienceService {

    private final ExperienceRepository experienceRepository;
    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;
    private final ExperienceShareService experienceShareService;
    @Transactional
    public Experience createExperience(String username, CreateExperienceRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Experience schedule = new Experience();
        schedule.setTitle(request.getTitle());
        schedule.setDescription(request.getDescription());
        schedule.setStartDate(request.getStartDate());
        schedule.setEndDate(request.getEndDate());
        schedule.setVisibility(request.getVisibility());
        schedule.setUser(user);

        Experience savedSchedule = experienceRepository.save(schedule);
        experienceShareService.shareExperience(savedSchedule.getId(), user.getUsername(), List.of(user.getUsername()));

        if (request.getActivities() != null && !request.getActivities().isEmpty()) {
            List<Activity> activities = request.getActivities().stream()
                    .map(activityDto -> createActivityFromDto(activityDto, savedSchedule))
                    .collect(Collectors.toList());
            savedSchedule.setActivities(activities);
        }

        return savedSchedule;
    }

    public List<Experience> getUserExperiences(String username) {
        return experienceRepository.findByUserUsernameOrderByStartDateDesc(username);
    }

    public Experience getExperienceById(Long experienceId, String username) {
        Experience experience = experienceRepository.findById(experienceId)
                .orElseThrow(() -> new RuntimeException("Experience not found"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Check if user is the owner
        if (experience.getUser().getId().equals(user.getId())) {
            return experience;
        }
        // Check if schedule is public
        if (experience.getVisibility() == ExperienceVisibility.PUBLIC) {
            return experience;
        }

        throw new RuntimeException("Unauthorized access to experience");
    }

    @Transactional
    public Experience updateExperience(Long experienceId, String username, CreateExperienceRequest request) {
        Experience experience = getExperienceById(experienceId, username);

        experience.setTitle(request.getTitle());
        experience.setDescription(request.getDescription());
        experience.setStartDate(request.getStartDate());
        experience.setEndDate(request.getEndDate());
        experience.setVisibility(request.getVisibility());

        // Clear existing activities and add new ones
        experience.getActivities().clear();
        if (request.getActivities() != null) {
            List<Activity> activities = request.getActivities().stream()
                    .map(activityDto -> createActivityFromDto(activityDto, experience))
                    .collect(Collectors.toList());
            experience.getActivities().addAll(activities);
        }

        return experienceRepository.save(experience);
    }

    @Transactional
    public void deleteExperience(Long experienceId, String username) {
        Experience experience = getExperienceById(experienceId, username);
        experienceRepository.delete(experience);
    }

    private Activity createActivityFromDto(ActivityDto activityDto, Experience experience) {
        Activity activity = new Activity();
        activity.setTitle(activityDto.getTitle());
        activity.setDescription(activityDto.getDescription());
        activity.setLocation(activityDto.getLocation());
        activity.setStartTime(activityDto.getStartTime());
        activity.setEndTime(activityDto.getEndTime());
        return activity;
    }

    public List<Activity> getExperienceActivities(Long experienceId, String username) {
        return activityRepository.findByExperienceIdOrderByStartTime(experienceId);
    }

    @Transactional
    public void deleteActivity(Long experienceId, Long activityId, String username) {
        Experience experience = getExperienceById(experienceId, username);
        Activity activity = experience.getActivities().stream()
                .filter(a -> a.getId().equals(activityId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Activity not found"));

        experience.getActivities().remove(activity);
        activityRepository.delete(activity);
    }
} 