package com.example.live_backend.mapper;

import com.example.live_backend.dto.Social.ChatMessageRequest;
import com.example.live_backend.dto.Social.ChatMessageResponse;
import com.example.live_backend.model.Social.ChatMessage;
import com.example.live_backend.model.Social.Conversation;
import com.example.live_backend.model.User.User;

import org.springframework.stereotype.Component;

@Component
public class ChatMessageMapper {

    public ChatMessageResponse toResponse(ChatMessage message) {
        ChatMessageResponse response = new ChatMessageResponse();
        response.setId(message.getId());
        response.setContent(message.getContent());
        response.setSentAt(message.getSentAt());
        response.setSenderId(message.getSender().getId());
        response.setConversationId(message.getConversation().getId());
        return response;
    }

    public ChatMessage toEntity(ChatMessageRequest request, Conversation conversation, User sender) {
        ChatMessage entity = new ChatMessage();
        entity.setContent(request.getContent());
        entity.setSender(sender);
        entity.setConversation(conversation);
        return entity;
    }
}
