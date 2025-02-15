package com.example.live_backend.service;

import com.example.live_backend.dto.NotificationResponse;
import com.example.live_backend.model.Notification;
import com.example.live_backend.model.Experience.Experience;
import com.example.live_backend.model.User.User;
import com.example.live_backend.repository.NotificationRepository;
import com.example.live_backend.repository.User.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NotificationService notificationService;

    @Captor
    private ArgumentCaptor<Notification> notificationCaptor;

    private User testUser;
    private Experience testSchedule;
    private User scheduleOwner;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        scheduleOwner = new User();
        scheduleOwner.setId(2L);
        scheduleOwner.setUsername("owner");

        testSchedule = new Experience();
        testSchedule.setId(1L);
        testSchedule.setTitle("Test Schedule");
        testSchedule.setUser(scheduleOwner);
    }

    @Test
    void notifyScheduleShared_ShouldCreateNotification() {
        notificationService.notifyScheduleShared(testUser, testSchedule);

        verify(notificationRepository).save(notificationCaptor.capture());
        Notification savedNotification = notificationCaptor.getValue();

        assertThat(savedNotification.getUser()).isEqualTo(testUser);
        assertThat(savedNotification.getMessage()).contains("shared a schedule");
        assertThat(savedNotification.getType()).isEqualTo("SCHEDULE_SHARED");
        assertThat(savedNotification.getRelatedScheduleId()).isEqualTo(testSchedule.getId());
    }

    @Test
    void notifyScheduleUpdated_ShouldCreateNotificationForSharedUsers() {
        notificationService.notifyScheduleUpdated(testSchedule);

        verify(notificationRepository, times(testSchedule.getShares().size()))
            .save(any(Notification.class));
    }

    @Test
    void getUserNotifications_Success() {
        Notification notification = new Notification();
        notification.setMessage("Test notification");
        Page<Notification> notificationPage = new PageImpl<>(List.of(notification));
        
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(notificationRepository.findByUserIdOrderByCreatedAtDesc(eq(1L), any(Pageable.class)))
            .thenReturn(notificationPage);

        Page<NotificationResponse> result = notificationService
            .getUserNotifications("testuser", PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getMessage()).isEqualTo("Test notification");
    }

    @Test
    void getUnreadCount_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(notificationRepository.countByUserIdAndReadFalse(1L)).thenReturn(5L);

        long unreadCount = notificationService.getUnreadCount("testuser");

        assertThat(unreadCount).isEqualTo(5);
    }

    @Test
    void markAllAsRead_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        notificationService.markAllAsRead("testuser");

        verify(notificationRepository).markAllAsRead(1L);
    }

    @Test
    void getUserNotifications_UserNotFound_ShouldThrowException() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            notificationService.getUserNotifications("nonexistent", PageRequest.of(0, 10));
        });
    }
} 