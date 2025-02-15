package com.example.live_backend.dto.Group;

import lombok.Data;

@Data
public class GroupMembershipRequest {
    private Long groupId;
    private Long userId;
    private String role;
}
