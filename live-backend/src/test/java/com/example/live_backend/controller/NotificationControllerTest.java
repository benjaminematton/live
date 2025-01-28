package com.example.live_backend.controller;

import com.example.live_backend.dto.NotificationResponse;
import com.example.live_backend.security.CustomUserDetails;
import com.example.live_backend.service.NotificationService;
import com.security.testutils.WithCustomUser;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @Test
    @WithCustomUser(username="testuser")
    void getUserNotifications_Success() throws Exception {
        NotificationResponse notification = new NotificationResponse();
        notification.setId(1L);
        notification.setMessage("Test notification");
        notification.setType("TEST");
        notification.setCreatedAt(LocalDateTime.now());
        
        Page<NotificationResponse> notificationPage = new PageImpl<>(List.of(notification));
        
        when(notificationService.getUserNotifications(eq("testuser"), any(Pageable.class)))
            .thenReturn(notificationPage);

        mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].message").value("Test notification"))
                .andExpect(jsonPath("$.content[0].type").value("TEST"));
    }

    @Test
    @WithCustomUser(username="testuser")
    void getUnreadCount_Success() throws Exception {
        when(notificationService.getUnreadCount("testuser")).thenReturn(5L);

        mockMvc.perform(get("/api/notifications/unread-count"))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));
    }

    @Test
    @WithCustomUser(username="testuser")
    void markAllAsRead_Success() throws Exception {
        doNothing().when(notificationService).markAllAsRead("testuser");

        mockMvc.perform(post("/api/notifications/mark-all-read"))
                .andExpect(status().isOk());

        verify(notificationService).markAllAsRead("testuser");
    }

    @Test
    void getNotifications_UnauthorizedOrForbidden() throws Exception {
        mockMvc.perform(get("/api/notifications"))
            .andExpect(result -> {
                int status = result.getResponse().getStatus();
                assertTrue(status == 401 || status == 403, 
                            "Expected status to be 401 or 403, but was: " + status);
            });
    }

    @Test
    @WithCustomUser(username="testuser")
    void getUserNotifications_EmptyPage() throws Exception {
        Page<NotificationResponse> emptyPage = new PageImpl<>(List.of());
        
        when(notificationService.getUserNotifications(eq("testuser"), any(Pageable.class)))
            .thenReturn(emptyPage);

        mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    @WithCustomUser(username="testuser")
    void getUserNotifications_WithPagination() throws Exception {
        mockMvc.perform(get("/api/notifications")
                .param("page", "1")
                .param("size", "10")
                .param("sort", "createdAt,desc"))
                .andExpect(status().isOk());

        verify(notificationService).getUserNotifications(eq("testuser"), any(Pageable.class));
    }

    @Test
    @WithCustomUser(username="testuser")
    void getUnreadCount_Zero() throws Exception {
        when(notificationService.getUnreadCount("testuser")).thenReturn(0L);

        mockMvc.perform(get("/api/notifications/unread-count"))
                .andExpect(status().isOk())
                .andExpect(content().string("0"));
    }

    // @Test
    // @WithCustomUser(username="testuser")
    // void markAllAsRead_ServiceError() throws Exception {
    //     doThrow(new RuntimeException("Service error"))
    //         .when(notificationService).markAllAsRead("testuser");

    //     mockMvc.perform(post("/api/notifications/mark-all-read"))
    //             .andExpect(status().isInternalServerError());
    // }
} 