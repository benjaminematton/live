package com.example.live_backend.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.live_backend.dto.Post.PostResponse;
import com.example.live_backend.security.CustomUserDetails;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import com.example.live_backend.service.PostService;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    
    @GetMapping("/feed/following")
    public ResponseEntity<List<PostResponse>> getFollowingFeed(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(postService.getFollowingFeed(userDetails.getUsername()));
    }

    @GetMapping("/feed/explore")
    public ResponseEntity<List<PostResponse>> getExploreFeed() {
        return ResponseEntity.ok(postService.getExploreFeed());
    }
}
