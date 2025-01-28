package com.example.live_backend.service;

import com.example.live_backend.dto.ActivityDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import com.theokanning.openai.completion.chat.ChatCompletionChoice;

@ExtendWith(MockitoExtension.class)
public class ChatGPTServiceTest {

    @Mock
    private OpenAiService openAiService;

    @InjectMocks
    private ChatGPTService chatGPTService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(chatGPTService, "model", "gpt-3.5-turbo");
        ReflectionTestUtils.setField(chatGPTService, "objectMapper", objectMapper);
        
        startDate = LocalDateTime.now();
        endDate = startDate.plusDays(1);
    }

    @Test
    void generateScheduleSuggestions_Success() {
        String mockResponse = """
            [
                {
                    "title": "Morning Coffee",
                    "description": "Start the day with coffee",
                    "location": "Local Cafe",
                    "startTime": "%s",
                    "endTime": "%s"
                }
            ]
            """.formatted(startDate, startDate.plusHours(1));

        mockChatGPTResponse(mockResponse);

        List<ActivityDto> suggestions = chatGPTService.generateScheduleSuggestions(
                "Plan a day in the city", startDate, endDate);

        assertThat(suggestions).hasSize(1);
        assertThat(suggestions.get(0).getTitle()).isEqualTo("Morning Coffee");
        assertThat(suggestions.get(0).getLocation()).isEqualTo("Local Cafe");
    }

    @Test
    void generateScheduleSuggestions_MultipleActivities() {
        String mockResponse = """
            [
                {
                    "title": "Morning Coffee",
                    "description": "Start the day with coffee",
                    "startTime": "%s",
                    "endTime": "%s"
                },
                {
                    "title": "Lunch Meeting",
                    "description": "Business lunch",
                    "startTime": "%s",
                    "endTime": "%s"
                }
            ]
            """.formatted(
                startDate, 
                startDate.plusHours(1),
                startDate.plusHours(4),
                startDate.plusHours(5)
            );

        mockChatGPTResponse(mockResponse);

        List<ActivityDto> suggestions = chatGPTService.generateScheduleSuggestions(
                "Plan a business day", startDate, endDate);

        assertThat(suggestions).hasSize(2);
        assertThat(suggestions.get(0).getTitle()).isEqualTo("Morning Coffee");
        assertThat(suggestions.get(1).getTitle()).isEqualTo("Lunch Meeting");
    }

    @Test
    void generateScheduleSuggestions_InvalidResponse() {
        String invalidResponse = "This is not a JSON response";
        mockChatGPTResponse(invalidResponse);

        assertThrows(RuntimeException.class, () -> {
            chatGPTService.generateScheduleSuggestions("Plan a day", startDate, endDate);
        });
    }

    @Test
    void generateScheduleSuggestions_EmptyResponse() {
        String emptyResponse = "[]";
        mockChatGPTResponse(emptyResponse);

        List<ActivityDto> suggestions = chatGPTService.generateScheduleSuggestions(
                "Plan a day", startDate, endDate);

        assertThat(suggestions).isEmpty();
    }

    @Test
    void refineSchedule_Success() {
        List<ActivityDto> currentActivities = createSampleActivities();
        String mockResponse = """
            [
                {
                    "title": "Refined Activity",
                    "description": "Updated description",
                    "startTime": "%s",
                    "endTime": "%s"
                }
            ]
            """.formatted(startDate, startDate.plusHours(2));

        mockChatGPTResponse(mockResponse);

        List<ActivityDto> refinedActivities = chatGPTService.refineSchedule(
                currentActivities, "Make it longer");

        assertThat(refinedActivities).hasSize(1);
        assertThat(refinedActivities.get(0).getTitle()).isEqualTo("Refined Activity");
    }

    @Test
    void refineSchedule_PreservesOriginalTiming() {
        List<ActivityDto> currentActivities = createSampleActivities();
        String mockResponse = """
            [
                {
                    "title": "Updated Activity",
                    "description": "Same timing as original",
                    "startTime": "%s",
                    "endTime": "%s"
                }
            ]
            """.formatted(
                currentActivities.get(0).getStartTime(),
                currentActivities.get(0).getEndTime()
            );

        mockChatGPTResponse(mockResponse);

        List<ActivityDto> refinedActivities = chatGPTService.refineSchedule(
                currentActivities, "Update description only");

        assertThat(refinedActivities).hasSize(1);
        assertThat(refinedActivities.get(0).getStartTime())
            .isEqualTo(currentActivities.get(0).getStartTime());
        assertThat(refinedActivities.get(0).getEndTime())
            .isEqualTo(currentActivities.get(0).getEndTime());
    }

    @Test
    void refineSchedule_APIError() {
        List<ActivityDto> currentActivities = createSampleActivities();
        when(openAiService.createChatCompletion(any()))
            .thenThrow(new RuntimeException("API Error"));

        assertThrows(RuntimeException.class, () -> {
            chatGPTService.refineSchedule(currentActivities, "Make changes");
        });
    }

    private List<ActivityDto> createSampleActivities() {
        ActivityDto activity = new ActivityDto();
        activity.setTitle("Original Activity");
        activity.setDescription("Original description");
        activity.setStartTime(startDate);
        activity.setEndTime(startDate.plusHours(1));
        return List.of(activity);
    }

    private void mockChatGPTResponse(String content) {
        ChatCompletionResult mockResult = mock(ChatCompletionResult.class);
        ChatMessage mockMessage = mock(ChatMessage.class);
        when(mockMessage.getContent()).thenReturn(content);
        
        List<com.theokanning.openai.completion.chat.ChatCompletionChoice> choices = new ArrayList<>();
        com.theokanning.openai.completion.chat.ChatCompletionChoice mockChoice = mock(com.theokanning.openai.completion.chat.ChatCompletionChoice.class);
        when(mockChoice.getMessage()).thenReturn(mockMessage);
        choices.add(mockChoice);
        
        when(mockResult.getChoices()).thenReturn(choices);
        when(openAiService.createChatCompletion(any(ChatCompletionRequest.class)))
            .thenReturn(mockResult);
    }
} 