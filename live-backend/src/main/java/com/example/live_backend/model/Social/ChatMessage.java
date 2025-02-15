package com.example.live_backend.model.Social;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

import com.example.live_backend.model.User.User;

@Data
@Entity
@Table(name = "chat_messages")
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;
    private LocalDateTime sentAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne
    private Conversation conversation;
}
