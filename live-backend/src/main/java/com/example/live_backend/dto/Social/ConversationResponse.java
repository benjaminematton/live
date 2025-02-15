package com.example.live_backend.dto.Social;

import com.example.live_backend.model.Social.ConversationType;

import lombok.Data;

@Data
public class ConversationResponse {
    private Long id;
    private ConversationType type;
    private Long groupId;
    private Long experienceId;
}
