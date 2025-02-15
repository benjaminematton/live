package com.example.live_backend.dto.Group;

import java.time.LocalDateTime;

import com.example.live_backend.dto.User.UserResponse;

import lombok.Data;

@Data
public class GroupMembershipResponse {
    private Long id;
    private GroupResponse group;
    private UserResponse user;
    private String role;
    private LocalDateTime joinedAt;
}
