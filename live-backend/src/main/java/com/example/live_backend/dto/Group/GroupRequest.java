package com.example.live_backend.dto.Group;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class GroupRequest {
    @NotBlank
    private String name;

    private List<GroupMembershipRequest> members;

    private Long creatorId;
}
