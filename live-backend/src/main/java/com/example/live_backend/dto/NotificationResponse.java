package com.example.live_backend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NotificationResponse {
    private Long id;
    private String message;
    private String type;
    private Long relatedScheduleId;
    private boolean read;
    private LocalDateTime createdAt;
} 