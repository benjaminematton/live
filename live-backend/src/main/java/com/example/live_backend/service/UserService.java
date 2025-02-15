package com.example.live_backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.live_backend.dto.User.UserRequest;
import com.example.live_backend.dto.User.UserResponse;
import com.example.live_backend.mapper.UserMapper;
import java.util.Set;

import com.example.live_backend.model.User.GroupMembership;
import com.example.live_backend.model.User.User;
import com.example.live_backend.repository.User.UserRepository;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserResponse createUser(UserRequest request) {
        User user = userMapper.toEntity(request);
        
        // Hash the password before storing
        String hashedPassword = passwordEncoder.encode(request.getPassword());
        user.setPassword(hashedPassword);

        // Possibly set other fields
        return userMapper.toResponse(user);
    }

    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        return userMapper.toResponse(user);
    }

    @Transactional
    public UserResponse updateUser(Long id, UserRequest request) {
        User user = userRepository.findById(id)
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
        return userMapper.toResponse(updatedUser);
    }

    public Set<UserResponse> getFriends(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        return user.getFriends().stream()
            .map(userMapper::toResponse)
            .collect(Collectors.toSet());
    }

    public void followUser(Long currentUserId, Long userIdToFollow) {
        User currentUser = userRepository.findById(currentUserId)
            .orElseThrow(() -> new UsernameNotFoundException("User not found")); 
        User userToFollow = userRepository.findById(userIdToFollow)
            .orElseThrow(() -> new UsernameNotFoundException("User to follow not found"));

        // Add userToFollow to currentUser's following set
        currentUser.getFollowing().add(userToFollow);
        userRepository.save(currentUser);
    }

    public void unfollowUser(Long currentUserId, Long userIdToUnfollow) {
        User currentUser = userRepository.findById(currentUserId)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        User userToUnfollow = userRepository.findById(userIdToUnfollow)
            .orElseThrow(() -> new UsernameNotFoundException("User to unfollow not found"));

        currentUser.getFollowing().remove(userToUnfollow);
        userRepository.save(currentUser);
    }

    public Set<UserResponse > getFollowing(Long id) {   
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return user.getFollowing().stream()
            .map(userMapper::toResponse)
            .collect(Collectors.toSet());
    }

    public Set<UserResponse > getFollowers(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return user.getFollowers().stream()
            .map(userMapper::toResponse)
            .collect(Collectors.toSet());
    }

    public List<GroupMembership> getGroupMembershipsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getGroupMemberships();
    }

    public boolean getUserShareLocation(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return user.isShareLocation();
    }

    public void setUserShareLocation(Long userId, boolean shareLocation) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setShareLocation(shareLocation);
        userRepository.save(user);
    }
} 
