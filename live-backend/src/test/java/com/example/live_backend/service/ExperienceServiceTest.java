package com.example.live_backend.service;

import com.example.live_backend.dto.ActivityDto;
import com.example.live_backend.dto.CreateExperienceRequest;
import com.example.live_backend.model.Activity;
import com.example.live_backend.model.Experience;
import com.example.live_backend.model.ExperienceVisibility;
import com.example.live_backend.model.User;
import com.example.live_backend.repository.ActivityRepository;
import com.example.live_backend.repository.ExperienceRepository;
import com.example.live_backend.repository.UserRepository;
import com.example.live_backend.repository.ExperienceShareRepository;
import com.example.live_backend.model.ExperienceShare;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExperienceServiceTest {

    @Mock
    private ExperienceRepository experienceRepository;

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ExperienceShareRepository experienceShareRepository;

    @InjectMocks
    private ExperienceService experienceService;

    private User testUser;
    private Experience testSchedule;
    private CreateExperienceRequest createRequest;
    private ActivityDto activityDto;

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

        activityDto = new ActivityDto();
        activityDto.setTitle("Test Activity");
        activityDto.setStartTime(LocalDateTime.now());
        activityDto.setEndTime(LocalDateTime.now().plusHours(1));

        createRequest = new CreateExperienceRequest();
        createRequest.setTitle("Test Schedule");
        createRequest.setStartDate(LocalDateTime.now());
        createRequest.setEndDate(LocalDateTime.now().plusDays(1));
        createRequest.setVisibility(ExperienceVisibility.PRIVATE);
        createRequest.setActivities(Arrays.asList(activityDto));
    }

    @Test
    void createSchedule_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(experienceRepository.save(any(Experience.class))).thenReturn(testSchedule);

        Experience result = experienceService.createExperience("testuser", createRequest);  

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo(createRequest.getTitle());
        verify(experienceRepository).save(any(Experience.class));
    }

    @Test
    void createSchedule_UserNotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            experienceService.createExperience("nonexistent", createRequest);
        });
    }

    @Test
    void getUserSchedules_Success() {
        List<Experience> schedules = Arrays.asList(testSchedule);
        when(experienceRepository.findByUserUsernameOrderByStartDateDesc("testuser"))
                .thenReturn(schedules);

        List<Experience> result = experienceService.getUserExperiences("testuser");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo(testSchedule.getTitle());
    }

    @Test
    void getScheduleById_Success() {
        when(experienceRepository.findById(1L)).thenReturn(Optional.of(testSchedule));

        Experience result = experienceService.getExperienceById(1L, "testuser");

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getScheduleById_UnauthorizedAccess() {
        when(experienceRepository.findById(1L)).thenReturn(Optional.of(testSchedule));

        assertThrows(RuntimeException.class, () -> {
            experienceService.getExperienceById(1L, "otheruser");
        });
    }

    @Test
    void deleteSchedule_Success() {
        when(experienceRepository.findById(1L)).thenReturn(Optional.of(testSchedule));
        doNothing().when(experienceRepository).delete(testSchedule);

        experienceService.deleteExperience(1L, "testuser");

        verify(experienceRepository).delete(testSchedule);
    }
} 