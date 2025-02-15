package com.example.live_backend.dto.Social;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ChatMessageResponse {
    private Long id;
    private String content;
    private LocalDateTime sentAt;
    private Long senderId;
    private Long conversationId;
}
