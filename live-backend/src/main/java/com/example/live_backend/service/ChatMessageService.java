package com.example.live_backend.service;

import org.springframework.stereotype.Service;

import com.example.live_backend.repository.Social.ChatMessageRepository;
import com.example.live_backend.repository.Social.ConversationRepository;
import com.example.live_backend.repository.User.UserRepository;
import com.example.live_backend.model.Social.ChatMessage;
import com.example.live_backend.model.Social.Conversation;
import com.example.live_backend.model.User.User;

import lombok.RequiredArgsConstructor;

import com.example.live_backend.dto.Social.ChatMessageRequest;
import com.example.live_backend.dto.Social.ChatMessageResponse;
import com.example.live_backend.mapper.ChatMessageMapper;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
    
    private final ChatMessageRepository chatMessageRepository;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final ChatMessageMapper chatMessageMapper;

    // Send a message
    public ChatMessageResponse sendMessage(ChatMessageRequest request, Long senderId, Long conversationId) {
        Conversation conversation = conversationRepository.findById(conversationId)
            .orElseThrow(() -> new RuntimeException("Conversation not found"));

        User sender = userRepository.findById(senderId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        ChatMessage message = new ChatMessage();
        message.setConversation(conversation);
        message.setSender(sender);
        message.setContent(request.getContent());
        message.setSentAt(LocalDateTime.now());

        return chatMessageMapper.toResponse(chatMessageRepository.save(message));
    }

    // Retrieve a message
    public ChatMessageResponse getMessage(Long messageId) {
        ChatMessage message = chatMessageRepository.findById(messageId)
            .orElseThrow(() -> new RuntimeException("Message not found"));

        return chatMessageMapper.toResponse(message);
    }

    // Retrieve conversation
    public List<ChatMessageResponse> getMessages(Long conversationId) {   
        Conversation conversation = conversationRepository.findById(conversationId)
            .orElseThrow(() -> new RuntimeException("Conversation not found"));

        return conversation.getMessages()
            .stream()
            .map(chatMessageMapper::toResponse)
            .collect(Collectors.toList());
    }
}
