package com.example.live_backend.dto;

import lombok.Data;

@Data
public class CheckInRequest {
    private double latitude;
    private double longitude;
}
