package com.example.live_backend.mapper;

import org.springframework.stereotype.Component;

import com.example.live_backend.dto.Post.PostRequest;
import com.example.live_backend.dto.Post.PostResponse;
import com.example.live_backend.model.Social.Post;
import com.example.live_backend.model.User.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PostMapper {

    private final UserMapper userMapper;
    

    public PostResponse toResponse(Post post) {
        PostResponse response = new PostResponse();
        response.setId(post.getId());
        response.setUser(userMapper.toResponse(post.getUser()));
        response.setCreatedAt(post.getCreatedAt());
        response.setCaption(post.getCaption());
        response.setImageUrl(post.getImageUrl());
        response.setPublic(post.isPublic());
        response.setLocation(post.getLocation());
        response.setLikeCount(post.getLikes().size());
        response.setCommentCount(post.getComments().size());
        return response;
    }

    public Post toEntity(PostRequest request, User user) { 
        Post post = new Post();
        post.setUser(user);
        post.setCaption(request.getCaption());
        post.setImageUrl(request.getImageUrl());
        post.setPublic(request.isPublic());
        post.setLocation(request.getLocation());
        return post;
    }

    public Post updateEntity(Post post, PostRequest request) {
        post.setCaption(request.getCaption());
        post.setImageUrl(request.getImageUrl());
        post.setPublic(request.isPublic());
        post.setLocation(request.getLocation());
        return post;
    }
}

