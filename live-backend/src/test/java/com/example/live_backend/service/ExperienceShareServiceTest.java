package com.example.live_backend.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import com.example.live_backend.model.Experience.Experience;
import com.example.live_backend.model.Experience.ExperienceShare;
import com.example.live_backend.model.Experience.ExperienceVisibility;
import com.example.live_backend.model.User.User;
import com.example.live_backend.dto.Activity.ActivityRequest;
import com.example.live_backend.dto.Experience.ExperienceRequest;
import com.example.live_backend.repository.Experience.ExperienceRepository;
import com.example.live_backend.repository.Experience.ExperienceShareRepository;
import com.example.live_backend.repository.User.UserRepository;
import com.example.live_backend.service.Experience.ExperienceShareService;
@ExtendWith(MockitoExtension.class)
public class ExperienceShareServiceTest {

    @InjectMocks
    private ExperienceShareService experienceShareService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ExperienceShareRepository experienceShareRepository;

    @Mock
    private ExperienceRepository experienceRepository;

    private User testUser;
    private Experience testSchedule;
    private ActivityRequest activityRequest;
    private ExperienceRequest createRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testSchedule = new Experience();
        testSchedule.setId(1L);
        testSchedule.setTitle("Test Schedule");
        testSchedule.setUser(testUser);
        testSchedule.setStartDate(LocalDateTime.now());
        testSchedule.setEndDate(LocalDateTime.now().plusDays(1));

        activityRequest = new ActivityRequest();
        activityRequest.setTitle("Test Activity");
        activityRequest.setStartTime(LocalDateTime.now());
        activityRequest.setEndTime(LocalDateTime.now().plusHours(1));

        createRequest = new ExperienceRequest();
        createRequest.setTitle("Test Schedule");
        createRequest.setStartDate(LocalDateTime.now());
        createRequest.setEndDate(LocalDateTime.now().plusDays(1));
        createRequest.setVisibility(ExperienceVisibility.PRIVATE);
        createRequest.setActivities(Arrays.asList(activityRequest));
    }

    @Test
    void shareSchedule_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));  
        when(experienceRepository.findById(1L)).thenReturn(Optional.of(testSchedule));
        
        User targetUser = new User();
        targetUser.setId(2L);
        targetUser.setUsername("targetuser");
        when(userRepository.findByUsername("targetuser")).thenReturn(Optional.of(targetUser));
        when(experienceShareRepository.findByExperienceIdAndSharedWithId(1L, 2L))
            .thenReturn(Optional.empty());

        experienceShareService.shareExperience(1L, "testuser", List.of("targetuser"));

        verify(experienceShareRepository).save(any(ExperienceShare.class));
    }

    @Test
    void shareSchedule_AlreadyShared_ShouldSkip() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(experienceRepository.findById(1L)).thenReturn(Optional.of(testSchedule));
        
        User targetUser = new User();
        targetUser.setId(2L);
        targetUser.setUsername("targetuser");
        when(userRepository.findByUsername("targetuser")).thenReturn(Optional.of(targetUser));
        when(experienceShareRepository.findByExperienceIdAndSharedWithId(1L, 2L))
            .thenReturn(Optional.of(new ExperienceShare()));

        experienceShareService.shareExperience(1L, "testuser", List.of("targetuser"));

        verify(experienceShareRepository, never()).save(any(ExperienceShare.class));
    }

    @Test
    void unshareSchedule_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(experienceRepository.findById(1L)).thenReturn(Optional.of(testSchedule));
        
        User targetUser = new User();
        targetUser.setId(2L);
        targetUser.setUsername("targetuser");
        when(userRepository.findByUsername("targetuser")).thenReturn(Optional.of(targetUser));

        experienceShareService.unshareExperience(1L, "testuser", "targetuser");

        verify(experienceShareRepository).deleteByExperienceIdAndSharedWithId(1L, 2L);
    }

    @Test
    void getSharedSchedules_Success() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        
        Experience sharedSchedule = new Experience();
        sharedSchedule.setId(2L);
        sharedSchedule.setTitle("Shared Schedule");
        
        ExperienceShare share = new ExperienceShare();
        share.setExperience(sharedSchedule);
        
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(experienceShareRepository.findBySharedWithId(1L))
            .thenReturn(List.of(share));

        List<Experience> sharedSchedules = experienceShareService.getSharedExperiences("testuser");

        assertThat(sharedSchedules).hasSize(1);
        assertThat(sharedSchedules.get(0).getTitle()).isEqualTo("Shared Schedule");
    }
    
}
