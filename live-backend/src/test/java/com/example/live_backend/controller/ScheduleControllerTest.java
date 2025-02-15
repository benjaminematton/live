package com.example.live_backend.controller;

import com.example.live_backend.model.Activity.Activity;
import com.example.live_backend.model.Experience.Experience;
import com.example.live_backend.model.Experience.ExperienceVisibility;
import com.example.live_backend.service.Experience.ExperienceService;
import com.example.live_backend.dto.Activity.ActivityRequest;
import com.example.live_backend.dto.Experience.ExperienceRequest;
import com.example.live_backend.dto.Experience.ExperienceResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.testutils.WithCustomUser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ScheduleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ExperienceService experienceService;

    private Experience testSchedule;
    private Activity testActivity;
    private ExperienceRequest createRequest;

    @BeforeEach
    void setUp() {
        testSchedule = new Experience();
        testSchedule.setId(1L);
        testSchedule.setTitle("Test Schedule");
        testSchedule.setStartDate(LocalDateTime.now());
        testSchedule.setEndDate(LocalDateTime.now().plusDays(1));

        ActivityRequest activityRequest = new ActivityRequest();
        activityRequest.setTitle("Test Activity");
        activityRequest.setStartTime(LocalDateTime.now());
        activityRequest.setEndTime(LocalDateTime.now().plusHours(1));

        createRequest = new ExperienceRequest();
        createRequest.setTitle("Test Schedule");
        createRequest.setStartDate(LocalDateTime.now());
        createRequest.setEndDate(LocalDateTime.now().plusDays(1));
        createRequest.setVisibility(ExperienceVisibility.PRIVATE);
        createRequest.setActivities(Arrays.asList(activityRequest));
        
        testActivity = new Activity();
        testActivity.setId(1L);
        testActivity.setTitle("Test Activity");
    }

    @Test
    @WithCustomUser(username="testuser")
    void createSchedule_Success() throws Exception {
        when(experienceService.createExperience(eq("testuser"), any(ExperienceRequest.class)))
                .thenReturn(testSchedule);

        mockMvc.perform(post("/api/schedules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(testSchedule.getTitle()));
    }

    @Test
    @WithCustomUser(username="testuser")
    void getUserSchedules_Success() throws Exception {
        ExperienceResponse responseSchedule = new ExperienceResponse(testSchedule);
        List<ExperienceResponse> schedules = Arrays.asList(responseSchedule);
        when(experienceService.getUserExperiences("testuser")).thenReturn(schedules);

        mockMvc.perform(get("/api/schedules"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value(testSchedule.getTitle()));
    }

    @Test
    @WithCustomUser(username="testuser")
    void getScheduleById_Success() throws Exception {
        when(experienceService.getExperienceById(1L, "testuser")).thenReturn(testSchedule);

        mockMvc.perform(get("/api/schedules/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(testSchedule.getTitle()));
    }

    @Test
    @WithCustomUser(username="testuser")
    void updateSchedule_Success() throws Exception {
        when(experienceService.updateExperience(eq(1L), eq("testuser"), any(ExperienceRequest.class)))
                .thenReturn(testSchedule);

        mockMvc.perform(put("/api/schedules/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(testSchedule.getTitle()));
    }

    @Test
    @WithCustomUser(username="testuser")
    void deleteSchedule_Success() throws Exception {
        doNothing().when(experienceService).deleteExperience(1L, "testuser");

        mockMvc.perform(delete("/api/schedules/1"))
                .andExpect(status().isNoContent());

        verify(experienceService).deleteExperience(1L, "testuser");
    }

    @Test
    @WithCustomUser(username="testuser")
    void getScheduleActivities_Success() throws Exception {
        List<Activity> activities = Arrays.asList(testActivity);
        when(experienceService.getExperienceActivities(1L, "testuser")).thenReturn(activities);

        mockMvc.perform(get("/api/schedules/1/activities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value(testActivity.getTitle()));
    }

    @Test
    @WithCustomUser(username="testuser")
    void deleteActivity_Success() throws Exception {
        doNothing().when(experienceService).deleteActivity(1L, 1L, "testuser");

        mockMvc.perform(delete("/api/schedules/1/activities/1"))
                .andExpect(status().isNoContent());

        verify(experienceService).deleteActivity(1L, 1L, "testuser");
    }
} 