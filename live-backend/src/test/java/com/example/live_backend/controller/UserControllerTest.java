package com.example.live_backend.controller;

import com.example.live_backend.dto.UpdateProfileRequest;
import com.example.live_backend.dto.UserProfileResponse;
import com.example.live_backend.model.User;
import com.example.live_backend.security.CustomUserDetails;
import com.example.live_backend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.testutils.WithCustomUser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private UserProfileResponse mockProfile;
    private UpdateProfileRequest updateRequest;

    @BeforeEach
    void setUp() {
        // Setup mock profile response
        mockProfile = new UserProfileResponse();
        mockProfile.setId(1L);
        mockProfile.setUsername("testuser");
        mockProfile.setEmail("test@example.com");
        mockProfile.setBio("Test bio");
        mockProfile.setProfilePicture("profile.jpg");

        // Setup update request
        updateRequest = new UpdateProfileRequest();
        updateRequest.setEmail("new@example.com");
        updateRequest.setBio("Updated bio");
        updateRequest.setProfilePicture("new-profile.jpg");
    }

    @Test
    @WithCustomUser(username="testuser")
    public void getOwnProfile_Success() throws Exception {
        when(userService.getUserProfile("testuser")).thenReturn(mockProfile);

        mockMvc.perform(get("/api/users/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.bio").value("Test bio"));
    }

    @Test
    public void getOwnProfile_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/users/profile"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithCustomUser(username="testuser")
    public void getUserProfile_Success() throws Exception {
        when(userService.getUserProfile("otheruser")).thenReturn(mockProfile);

        mockMvc.perform(get("/api/users/otheruser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    @WithCustomUser(username="testuser")
    public void getUserProfile_NotFound() throws Exception {
        when(userService.getUserProfile("nonexistent"))
                .thenThrow(new UsernameNotFoundException("User not found"));

        mockMvc.perform(get("/api/users/nonexistent"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithCustomUser(username="testuser")
    public void updateProfile_Success() throws Exception {
        UserProfileResponse updatedProfile = new UserProfileResponse();
        updatedProfile.setUsername("testuser");
        updatedProfile.setEmail("new@example.com");
        updatedProfile.setBio("Updated bio");

        when(userService.updateProfile(eq("testuser"), any(UpdateProfileRequest.class)))
                .thenReturn(updatedProfile);

        mockMvc.perform(put("/api/users/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("new@example.com"))
                .andExpect(jsonPath("$.bio").value("Updated bio"));
    }

    @Test
    @WithCustomUser(username="testuser")
    public void updateProfile_InvalidEmail() throws Exception {
        UpdateProfileRequest invalidRequest = new UpdateProfileRequest();
        invalidRequest.setEmail("invalid-email"); // Invalid email format

        mockMvc.perform(put("/api/users/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithCustomUser(username="testuser")
    public void updateProfile_EmailAlreadyExists() throws Exception {
        when(userService.updateProfile(eq("testuser"), any(UpdateProfileRequest.class)))
                .thenThrow(new RuntimeException("Email already in use"));

        mockMvc.perform(put("/api/users/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
    }
} 