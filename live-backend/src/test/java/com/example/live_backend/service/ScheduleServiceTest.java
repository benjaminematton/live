package com.example.live_backend.service;

import com.example.live_backend.dto.ActivityDto;
import com.example.live_backend.dto.CreateScheduleRequest;
import com.example.live_backend.model.Activity;
import com.example.live_backend.model.Schedule;
import com.example.live_backend.model.ScheduleVisibility;
import com.example.live_backend.model.User;
import com.example.live_backend.repository.ActivityRepository;
import com.example.live_backend.repository.ScheduleRepository;
import com.example.live_backend.repository.UserRepository;
import com.example.live_backend.repository.ScheduleShareRepository;
import com.example.live_backend.model.ScheduleShare;
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
public class ScheduleServiceTest {

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ScheduleShareRepository scheduleShareRepository;

    @InjectMocks
    private ScheduleService scheduleService;

    private User testUser;
    private Schedule testSchedule;
    private CreateScheduleRequest createRequest;
    private ActivityDto activityDto;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testSchedule = new Schedule();
        testSchedule.setId(1L);
        testSchedule.setTitle("Test Schedule");
        testSchedule.setUser(testUser);
        testSchedule.setStartDate(LocalDateTime.now());
        testSchedule.setEndDate(LocalDateTime.now().plusDays(1));

        activityDto = new ActivityDto();
        activityDto.setTitle("Test Activity");
        activityDto.setStartTime(LocalDateTime.now());
        activityDto.setEndTime(LocalDateTime.now().plusHours(1));

        createRequest = new CreateScheduleRequest();
        createRequest.setTitle("Test Schedule");
        createRequest.setStartDate(LocalDateTime.now());
        createRequest.setEndDate(LocalDateTime.now().plusDays(1));
        createRequest.setVisibility(ScheduleVisibility.PRIVATE);
        createRequest.setActivities(Arrays.asList(activityDto));
    }

    @Test
    void createSchedule_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(scheduleRepository.save(any(Schedule.class))).thenReturn(testSchedule);

        Schedule result = scheduleService.createSchedule("testuser", createRequest);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo(createRequest.getTitle());
        verify(scheduleRepository).save(any(Schedule.class));
    }

    @Test
    void createSchedule_UserNotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            scheduleService.createSchedule("nonexistent", createRequest);
        });
    }

    @Test
    void getUserSchedules_Success() {
        List<Schedule> schedules = Arrays.asList(testSchedule);
        when(scheduleRepository.findByUserUsernameOrderByStartDateDesc("testuser"))
                .thenReturn(schedules);

        List<Schedule> result = scheduleService.getUserSchedules("testuser");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo(testSchedule.getTitle());
    }

    @Test
    void getScheduleById_Success() {
        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(testSchedule));

        Schedule result = scheduleService.getScheduleById(1L, "testuser");

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getScheduleById_UnauthorizedAccess() {
        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(testSchedule));

        assertThrows(RuntimeException.class, () -> {
            scheduleService.getScheduleById(1L, "otheruser");
        });
    }

    @Test
    void deleteSchedule_Success() {
        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(testSchedule));
        doNothing().when(scheduleRepository).delete(testSchedule);

        scheduleService.deleteSchedule(1L, "testuser");

        verify(scheduleRepository).delete(testSchedule);
    }

    @Test
    void shareSchedule_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(testSchedule));
        
        User targetUser = new User();
        targetUser.setId(2L);
        targetUser.setUsername("targetuser");
        when(userRepository.findByUsername("targetuser")).thenReturn(Optional.of(targetUser));
        when(scheduleShareRepository.findByScheduleIdAndSharedWithId(1L, 2L))
            .thenReturn(Optional.empty());

        scheduleService.shareSchedule(1L, "testuser", List.of("targetuser"));

        verify(scheduleShareRepository).save(any(ScheduleShare.class));
    }

    @Test
    void shareSchedule_AlreadyShared_ShouldSkip() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(testSchedule));
        
        User targetUser = new User();
        targetUser.setId(2L);
        targetUser.setUsername("targetuser");
        when(userRepository.findByUsername("targetuser")).thenReturn(Optional.of(targetUser));
        when(scheduleShareRepository.findByScheduleIdAndSharedWithId(1L, 2L))
            .thenReturn(Optional.of(new ScheduleShare()));

        scheduleService.shareSchedule(1L, "testuser", List.of("targetuser"));

        verify(scheduleShareRepository, never()).save(any(ScheduleShare.class));
    }

    @Test
    void unshareSchedule_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(testSchedule));
        
        User targetUser = new User();
        targetUser.setId(2L);
        targetUser.setUsername("targetuser");
        when(userRepository.findByUsername("targetuser")).thenReturn(Optional.of(targetUser));

        scheduleService.unshareSchedule(1L, "testuser", "targetuser");

        verify(scheduleShareRepository).deleteByScheduleIdAndSharedWithId(1L, 2L);
    }

    @Test
    void getSharedSchedules_Success() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        
        Schedule sharedSchedule = new Schedule();
        sharedSchedule.setId(2L);
        sharedSchedule.setTitle("Shared Schedule");
        
        ScheduleShare share = new ScheduleShare();
        share.setSchedule(sharedSchedule);
        
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(scheduleShareRepository.findBySharedWithId(1L))
            .thenReturn(List.of(share));

        List<Schedule> sharedSchedules = scheduleService.getSharedSchedules("testuser");

        assertThat(sharedSchedules).hasSize(1);
        assertThat(sharedSchedules.get(0).getTitle()).isEqualTo("Shared Schedule");
    }
} 