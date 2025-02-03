package com.example.live_backend.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.live_backend.model.Post;
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
    public ResponseEntity<List<Post>> getFollowingFeed(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<Post> posts = postService.getFollowingFeed(userDetails.getUsername());
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/feed/explore")
    public ResponseEntity<List<Post>> getExploreFeed() {
        List<Post> posts = postService.getExploreFeed();
        return ResponseEntity.ok(posts);
    }
}
