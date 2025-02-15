package com.example.live_backend.repository.Social;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.live_backend.model.Social.Conversation;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    
}
