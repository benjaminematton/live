package com.example.live_backend.mapper;

import com.example.live_backend.dto.User.UserRequest;
import com.example.live_backend.dto.User.UserResponse;
import com.example.live_backend.model.User.User;

import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setProfilePicture(user.getProfilePicture());
        response.setShareLocation(user.isShareLocation());
        response.setBio(user.getBio());
        return response;
    }

    public User toEntity(UserRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setProfilePicture(request.getProfilePicture());
        user.setBio(request.getBio());
        user.setShareLocation(request.isShareLocation());
        return user;
    }

    public void updateEntity(User user, UserRequest request) {
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setProfilePicture(request.getProfilePicture());
        user.setBio(request.getBio());
        user.setShareLocation(request.isShareLocation());
    }
}
