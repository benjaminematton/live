package com.example.live_backend.dto.User;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LocationBroadcast {
    private Long userId;
    private Double latitude;
    private Double longitude;
    private LocalDateTime lastLocationUpdate;
}
