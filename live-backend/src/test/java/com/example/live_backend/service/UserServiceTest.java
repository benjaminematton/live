package com.example.live_backend.service;

import com.example.live_backend.dto.User.UserRequest;
import com.example.live_backend.dto.User.UserResponse;
import com.example.live_backend.model.User.User;
import com.example.live_backend.repository.User.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserRequest updateRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setBio("Original bio");

        updateRequest = new UserRequest();
        updateRequest.setEmail("new@example.com");
        updateRequest.setBio("Updated bio");
    }

    @Test
    void getUserProfile_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        UserResponse response = userService.getUserById(1L);

        assertNotNull(response);
        assertEquals(testUser.getUsername(), response.getUsername());
        assertEquals(testUser.getEmail(), response.getEmail());
    }

    @Test
    void getUserProfile_UserNotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userService.getUserById(3L);
        });
    }

    @Test
    void updateProfile_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserResponse response = userService.updateUser(1L, updateRequest);

        assertNotNull(response);
        assertEquals(updateRequest.getEmail(), response.getEmail());
        assertEquals(updateRequest.getBio(), response.getBio());
    }

    @Test
    void updateProfile_EmailAlreadyExists() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail("new@example.com")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> {
            userService.updateUser(1L, updateRequest);
        });
    }
} 