package com.example.live_backend.service;

import com.example.live_backend.dto.NotificationResponse;
import com.example.live_backend.model.Notification;
import com.example.live_backend.model.Schedule;
import com.example.live_backend.model.User;
import com.example.live_backend.repository.NotificationRepository;
import com.example.live_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public void notifyScheduleShared(User targetUser, Schedule schedule) {
        createNotification(
            targetUser,
            String.format("%s shared a schedule '%s' with you", 
                schedule.getUser().getUsername(), schedule.getTitle()),
            "SCHEDULE_SHARED",
            schedule.getId()
        );
    }

    public void notifyScheduleUpdated(Schedule schedule) {
        schedule.getShares().forEach(share -> {
            createNotification(
                share.getSharedWith(),
                String.format("Schedule '%s' has been updated", schedule.getTitle()),
                "SCHEDULE_UPDATED",
                schedule.getId()
            );
        });
    }

    public void notifyScheduleUnshared(User targetUser, Schedule schedule) {
        createNotification(
            targetUser,
            String.format("%s has stopped sharing schedule '%s' with you", 
                schedule.getUser().getUsername(), schedule.getTitle()),
            "SCHEDULE_UNSHARED",
            schedule.getId()
        );
    }

    private void createNotification(User user, String message, String type, Long scheduleId) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notification.setType(type);
        notification.setRelatedScheduleId(scheduleId);
        notificationRepository.save(notification);
    }

    public Page<NotificationResponse> getUserNotifications(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
                
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), pageable)
                .map(this::mapToResponse);
    }

    public long getUnreadCount(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return notificationRepository.countByUserIdAndReadFalse(user.getId());
    }

    @Transactional
    public void markAllAsRead(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        notificationRepository.markAllAsRead(user.getId());
    }

    private NotificationResponse mapToResponse(Notification notification) {
        NotificationResponse response = new NotificationResponse();
        response.setId(notification.getId());
        response.setMessage(notification.getMessage());
        response.setType(notification.getType());
        response.setRelatedScheduleId(notification.getRelatedScheduleId());
        response.setRead(notification.isRead());
        response.setCreatedAt(notification.getCreatedAt());
        return response;
    }
} 