package com.example.live_backend.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;

import lombok.RequiredArgsConstructor;

import com.example.live_backend.dto.Social.ChatMessageRequest;
import com.example.live_backend.dto.Social.ChatMessageResponse;
import com.example.live_backend.service.ChatMessageService;

import java.security.Principal;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.example.live_backend.security.CustomUserDetails;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor    
public class ChatController {

    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping
    public ResponseEntity<ChatMessageResponse> sendMessage(@RequestBody ChatMessageRequest request, @RequestParam Long senderId, @RequestParam Long conversationId) {
        ChatMessageResponse message = chatMessageService.sendMessage(request, senderId, conversationId);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/{experienceId}")
    public ResponseEntity<List<ChatMessageResponse>> getMessages(@PathVariable Long experienceId) {
        return ResponseEntity.ok(chatMessageService.getMessages(experienceId));
    }
    
    @MessageMapping("/conversation/{conversationId}/chat")
    public void handleMessage(@DestinationVariable Long conversationId, ChatMessageRequest req, Principal principal) {
        // 1) Convert Principal to an Authentication, then cast getPrincipal() to CustomUserDetails
        if (principal instanceof Authentication) {
            Authentication auth = (Authentication) principal;
            Object principalObj = auth.getPrincipal();
            if (principalObj instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) principalObj;
                Long senderId = userDetails.getUserId(); // or userDetails.getUser().getId()

                // 2) Save the message
                ChatMessageResponse response = chatMessageService.sendMessage(req, senderId, conversationId);

                // 3) Broadcast to all subscribers
                messagingTemplate.convertAndSend("/topic/conversation/" + conversationId + "/chat", response);

            } else {
                throw new RuntimeException("Unexpected principal type: " + principalObj.getClass());
            }
        } else {
            throw new RuntimeException("Principal not an Authentication instance");
        }
    }
}