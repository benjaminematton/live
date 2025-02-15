package com.example.live_backend.repository.Social;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.live_backend.model.Social.ChatMessage;

import java.util.List;
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByExperienceIdOrderBySentAtAsc(Long experienceId);
}
