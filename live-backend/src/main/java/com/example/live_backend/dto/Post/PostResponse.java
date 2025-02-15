package com.example.live_backend.dto.Post;

import java.time.LocalDateTime;
import java.util.List;

import com.example.live_backend.dto.User.UserResponse;

import lombok.Data;

@Data
public class PostResponse {
    private Long id;
    private UserResponse user; 
    private LocalDateTime createdAt;
    private String caption;
    private List<String> imageUrl;
    private boolean isPublic;
    private String location;
    private int likeCount;
    private int commentCount;
}
    