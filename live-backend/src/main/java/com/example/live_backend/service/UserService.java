package com.example.live_backend.service;

import com.example.live_backend.dto.UpdateProfileRequest;
import com.example.live_backend.dto.UserProfileResponse;
import com.example.live_backend.model.User;
import com.example.live_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;

    public UserProfileResponse getUserProfile(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        return mapToProfileResponse(user);
    }

    @Transactional
    public UserProfileResponse updateProfile(String username, UpdateProfileRequest request) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email already in use");
            }
            user.setEmail(request.getEmail());
        }

        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }

        if (request.getProfilePicture() != null) {
            user.setProfilePicture(request.getProfilePicture());
        }

        User updatedUser = userRepository.save(user);
        return mapToProfileResponse(updatedUser);
    }

    public List<UserProfileResponse> getFriends(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        return user.getFriends().stream()
            .map(this::mapToProfileResponse)
            .collect(Collectors.toList());
    }

    private UserProfileResponse mapToProfileResponse(User user) {
        UserProfileResponse response = new UserProfileResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setProfilePicture(user.getProfilePicture());
        response.setBio(user.getBio());
        return response;
    }

    public void followUser(String currentUsername, Long userIdToFollow) {
        User currentUser = userRepository.findByUsername(currentUsername)
            .orElseThrow(() -> new UsernameNotFoundException("User not found")); 
        User userToFollow = userRepository.findById(userIdToFollow)
            .orElseThrow(() -> new UsernameNotFoundException("User to follow not found"));

        // Add userToFollow to currentUser's following set
        currentUser.getFollowing().add(userToFollow);
        userRepository.save(currentUser);
    }

    public void unfollowUser(String currentUsername, Long userIdToUnfollow) {
        User currentUser = userRepository.findByUsername(currentUsername)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        User userToUnfollow = userRepository.findById(userIdToUnfollow)
            .orElseThrow(() -> new UsernameNotFoundException("User to unfollow not found"));

        currentUser.getFollowing().remove(userToUnfollow);
        userRepository.save(currentUser);
    }

    public List<UserProfileResponse> getFollowing(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return user.getFollowing().stream()
            .map(this::mapToProfileResponse)
            .collect(Collectors.toList());
    }

    public List<UserProfileResponse> getFollowers(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return user.getFollowers().stream()
            .map(this::mapToProfileResponse)
            .collect(Collectors.toList());
    }
} 