package com.example.live_backend.model.Social;

import java.util.ArrayList;

import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;
import jakarta.persistence.EnumType;
import jakarta.persistence.CascadeType;
import java.util.List;
@Entity
@Data
public class Conversation {
    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    private ConversationType type; // e.g. GROUP, EXPERIENCE

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatMessage> messages = new ArrayList<>();

    private Long groupId;       // if type == GROUP
    private Long experienceId;
}
