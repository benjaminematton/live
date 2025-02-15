package com.example.live_backend.dto.Group;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class GroupResponse {
    private Long id;
    private String name;
    private String creatorId;
    private List<GroupMembershipResponse> members;
    private LocalDateTime createdAt;
}

