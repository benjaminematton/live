package com.example.live_backend.dto;

import lombok.Data;

@Data
public class UserProfileResponse {
    private Long id;
    private String username;
    private String email;
    private String profilePicture;
    private String bio;
} 