package com.example.live_backend.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UpdateProfileRequest {
    @Email
    private String email;
    private String bio;
    private String profilePicture;
} 