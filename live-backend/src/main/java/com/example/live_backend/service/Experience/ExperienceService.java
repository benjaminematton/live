package com.example.live_backend.service.Experience;

import com.example.live_backend.dto.Activity.ActivityResponse;
import com.example.live_backend.dto.Experience.ExperienceRequest;
import com.example.live_backend.dto.Experience.ExperienceResponse;
import com.example.live_backend.model.Activity.Activity;
import com.example.live_backend.model.Experience.Experience;
import com.example.live_backend.model.Experience.ExperienceVisibility;
import com.example.live_backend.model.User.User;
import com.example.live_backend.repository.Activity.ActivityRepository;
import com.example.live_backend.repository.Experience.ExperienceRepository;
import com.example.live_backend.repository.User.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.live_backend.mapper.ActivityMapper;
import com.example.live_backend.mapper.ExperienceMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExperienceService {

    private final ExperienceRepository experienceRepository;
    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;
    private final ExperienceShareService experienceShareService;
    private final ActivityMapper activityMapper;
    private final ExperienceMapper experienceMapper;
    private final ActiveExperienceService activeExperienceService;

    @Transactional
    public ExperienceResponse createExperience(String username, ExperienceRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Experience experience = experienceMapper.toEntity(request);
        experience.setUser(user);

        Experience savedExperience = experienceRepository.save(experience);
        experienceShareService.shareExperience(savedExperience.getId(), user.getUsername(), List.of(user.getUsername()));

        if (request.getActivities() != null && !request.getActivities().isEmpty()) {
            List<Activity> activities = request.getActivities().stream()
                    .map(activityMapper::toEntity)
                    .collect(Collectors.toList());
            savedExperience.setActivities(activities);
        }

        return experienceMapper.toResponse(savedExperience);
    }

    public List<ExperienceResponse> getUserExperiences(String username) {
        List<Experience> experiences = experienceRepository.findByUserUsernameOrderByStartDateDesc(username);
        return experiences.stream()
                .map(experienceMapper::toResponse)
                .collect(Collectors.toList());
    }

    public ExperienceResponse getExperienceById(Long experienceId, String username) {
        Experience experience = experienceRepository.findById(experienceId)
                .orElseThrow(() -> new RuntimeException("Experience not found"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Check if user is the owner
        if (experience.getUser().getId().equals(user.getId())) {
            return experienceMapper.toResponse(experience);
        }
        // Check if schedule is public
        if (experience.getVisibility() == ExperienceVisibility.PUBLIC) {
            return experienceMapper.toResponse(experience);
        }

        throw new RuntimeException("Unauthorized access to experience");
    }

    @Transactional
        public ExperienceResponse updateExperience(Long experienceId, String username, ExperienceRequest request) {
        Experience experience = experienceRepository.findById(experienceId)
                .orElseThrow(() -> new RuntimeException("Experience not found"));   

        experienceMapper.updateEntity(experience, request);

        // Clear existing activities and add new ones
        experience.getActivities().clear();
        if (request.getActivities() != null) {
            List<Activity> activities = request.getActivities().stream()
                    .map(activityMapper::toEntity)
                    .collect(Collectors.toList());
            experience.getActivities().addAll(activities);
        }

        return experienceMapper.toResponse(experienceRepository.save(experience));
    }

    @Transactional
    public void deleteExperience(Long experienceId, String username) {
        Experience experience = experienceRepository.findById(experienceId)
                .orElseThrow(() -> new RuntimeException("Experience not found"));
        experienceRepository.delete(experience);
    }

    public List<ActivityResponse> getExperienceActivities(Long experienceId) {
        Experience experience = experienceRepository.findById(experienceId)
                .orElseThrow(() -> new RuntimeException("Experience not found"));
        return experience.getActivities().stream()
                .map(activityMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteActivity(Long experienceId, Long activityId, String username) {
        Experience experience = experienceRepository.findById(experienceId)
                .orElseThrow(() -> new RuntimeException("Experience not found"));
        Activity activity = experience.getActivities().stream()
                .filter(a -> a.getId().equals(activityId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Activity not found"));

        experience.getActivities().remove(activity);
        activityRepository.delete(activity);
    }

    public void startActiveExperience(Long experienceId, String username) {
        Experience experience = experienceRepository.findById(experienceId)
                .orElseThrow(() -> new RuntimeException("Experience not found"));
        activeExperienceService.startExperience(experience);
    }
} 
