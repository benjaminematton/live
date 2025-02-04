package com.example.live_backend.controller;

import com.example.live_backend.dto.ActivityDto;
import com.example.live_backend.dto.CreateExperienceRequest;
import com.example.live_backend.model.Activity;
import com.example.live_backend.model.Experience;
import com.example.live_backend.model.ExperienceVisibility;
import com.example.live_backend.service.ExperienceService;
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
    private ExperienceService scheduleService;

    private Experience testSchedule;
    private CreateExperienceRequest createRequest;
    private Activity testActivity;

    @BeforeEach
    void setUp() {
        testSchedule = new Experience();
        testSchedule.setId(1L);
        testSchedule.setTitle("Test Schedule");
        testSchedule.setStartDate(LocalDateTime.now());
        testSchedule.setEndDate(LocalDateTime.now().plusDays(1));

        ActivityDto activityDto = new ActivityDto();
        activityDto.setTitle("Test Activity");
        activityDto.setStartTime(LocalDateTime.now());
        activityDto.setEndTime(LocalDateTime.now().plusHours(1));

        createRequest = new CreateExperienceRequest();
        createRequest.setTitle("Test Schedule");
        createRequest.setStartDate(LocalDateTime.now());
        createRequest.setEndDate(LocalDateTime.now().plusDays(1));
        createRequest.setVisibility(ExperienceVisibility.PRIVATE);
        createRequest.setActivities(Arrays.asList(activityDto));

        testActivity = new Activity();
        testActivity.setId(1L);
        testActivity.setTitle("Test Activity");
    }

    @Test
    @WithCustomUser(username="testuser")
    void createSchedule_Success() throws Exception {
        when(scheduleService.createExperience(eq("testuser"), any(CreateExperienceRequest.class)))
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
        List<Experience> schedules = Arrays.asList(testSchedule);
        when(scheduleService.getUserExperiences("testuser")).thenReturn(schedules);

        mockMvc.perform(get("/api/schedules"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value(testSchedule.getTitle()));
    }

    @Test
    @WithCustomUser(username="testuser")
        void getScheduleById_Success() throws Exception {
            when(scheduleService.getExperienceById(1L, "testuser")).thenReturn(testSchedule);

        mockMvc.perform(get("/api/schedules/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(testSchedule.getTitle()));
    }

    @Test
    @WithCustomUser(username="testuser")
    void updateSchedule_Success() throws Exception {
        when(scheduleService.updateExperience(eq(1L), eq("testuser"), any(CreateExperienceRequest.class)))
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
        doNothing().when(scheduleService).deleteExperience(1L, "testuser");

        mockMvc.perform(delete("/api/schedules/1"))
                .andExpect(status().isNoContent());

        verify(scheduleService).deleteExperience(1L, "testuser");
    }

    @Test
    @WithCustomUser(username="testuser")
    void getScheduleActivities_Success() throws Exception {
        List<Activity> activities = Arrays.asList(testActivity);
        when(scheduleService.getExperienceActivities(1L, "testuser")).thenReturn(activities);

        mockMvc.perform(get("/api/schedules/1/activities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value(testActivity.getTitle()));
    }

    @Test
    @WithCustomUser(username="testuser")
    void deleteActivity_Success() throws Exception {
        doNothing().when(scheduleService).deleteActivity(1L, 1L, "testuser");

        mockMvc.perform(delete("/api/schedules/1/activities/1"))
                .andExpect(status().isNoContent());

        verify(scheduleService).deleteActivity(1L, 1L, "testuser");
    }
} 