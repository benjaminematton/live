package com.example.live_backend.service;

import com.example.live_backend.dto.ActivityDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatGPTService {

    private final OpenAiService openAiService;
    private final ObjectMapper objectMapper;

    @Value("${openai.model}")
    private String model;

    /**
     * In-memory conversation history for demonstration.
     * In production, consider storing in a DB or session cache keyed by user/session ID.
     */
    private final List<ChatMessage> conversation = new ArrayList<>();

    /**
     * Generates schedule suggestions based on the user's prompt and optional start/end times.
     * It appends the conversation so ChatGPT sees prior context if the user calls again.
     */
    public List<ActivityDto> generateScheduleSuggestions(String prompt, LocalDateTime startDate, LocalDateTime endDate) {
        try {
            // If this is the first call in the conversation, add a system message
            if (conversation.isEmpty()) {
                String systemPrompt = """
                    You are an assistant that returns only valid JSON.
                    
                    The user wants to do the following:

                    \"\"\" 
                    {PROMPT_PLACEHOLDER}
                    \"\"\"

                    Based on how many things the user wants to do, suggest a corresponding list of activities.
                    - If the user input implies only one desired activity, return one object.
                    - If the user input implies multiple activities, return multiple objects.

                    Return only a JSON array of objects, where each object has exactly the following keys:
                    - "title" (string)
                    - "description" (string)
                    - "location" (string)
                    - "startTime" (ISO 8601 format)
                    - "endTime" (ISO 8601 format)

                    No extra keys, no additional commentary, and do not wrap the JSON in code blocks.
                    """;

                systemPrompt = systemPrompt.replace("{PROMPT_PLACEHOLDER}", prompt);
                
                // Optionally add info about startDate/endDate
                if (startDate != null && endDate != null) {
                    systemPrompt += "\n\nAll activities should be scheduled between " 
                                   + startDate + " and " + endDate + ".";
                }

                conversation.add(new ChatMessage("system", systemPrompt));
            }

            // Add the user's latest prompt as a user message
            String userMessageContent = prompt;
            if (startDate != null && endDate != null) {
                userMessageContent += "\n(Activities should be between " + startDate + " and " + endDate + ")";
            }
            conversation.add(new ChatMessage("user", userMessageContent));

            // Build the ChatCompletionRequest using the entire conversation
            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model(model)
                    .messages(conversation)
                    .temperature(0.7)
                    .build();

            // Send to OpenAI
            String response = openAiService.createChatCompletion(request)
                    .getChoices()
                    .get(0)
                    .getMessage()
                    .getContent();

            // Record the assistant's response in the conversation
            conversation.add(new ChatMessage("assistant", response));

            // Parse and return
            return parseActivities(response);

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate schedule suggestions", e);
        }
    }

    /**
     * Let user refine the *existing* schedule. We pass the current schedule and a refinement prompt,
     * then ChatGPT returns an updated schedule. We again append to the same conversation for context.
     */
    public List<ActivityDto> refineSchedule(List<ActivityDto> currentActivities, String refinementPrompt) {
        try {
            // Convert current schedule to JSON
            String currentScheduleJson = objectMapper.writeValueAsString(currentActivities);

            // Build a new user message that references the existing schedule
            String userMessageContent = "Current schedule: " + currentScheduleJson 
                    + "\n\nRefinement request: " + refinementPrompt;

            // Add user message to conversation
            conversation.add(new ChatMessage("user", userMessageContent));

            // Build the request using the entire conversation again
            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model(model)
                    .messages(conversation)
                    .temperature(0.7)
                    .build();

            // Get the response
            String response = openAiService.createChatCompletion(request)
                    .getChoices()
                    .get(0)
                    .getMessage()
                    .getContent();

            // Add assistant's response
            conversation.add(new ChatMessage("assistant", response));

            return parseActivities(response);

        } catch (Exception e) {
            throw new RuntimeException("Failed to refine schedule", e);
        }
    }

    /**
     * Recommends follow-up activities based on a chosen activity.
     * Also appends to the same conversation so ChatGPT is aware of the prior context.
     */
    public List<ActivityDto> recommendActivities(String chosenActivity) {
        try {
            // Create a user message referencing the chosen activity
            String userMessageContent = "The user has chosen: " + chosenActivity 
                + ". Suggest 3 follow-up activities that pair well with it.";

            conversation.add(new ChatMessage("user", userMessageContent));

            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model(model)
                    .messages(conversation)
                    .temperature(0.7)
                    .build();

            String response = openAiService.createChatCompletion(request)
                    .getChoices().get(0).getMessage().getContent();

            conversation.add(new ChatMessage("assistant", response));

            return parseActivities(response);

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate activity recommendations", e);
        }
    }

    /**
     * Parses the JSON array returned by ChatGPT into a list of ActivityDto.
     */
    private List<ActivityDto> parseActivities(String jsonResponse) {
        try {
            // Extract the JSON array from the assistant's response
            String jsonArray = extractJsonArray(jsonResponse);

            // Convert that array into a list of ActivityDto
            return objectMapper.readValue(jsonArray, new TypeReference<List<ActivityDto>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse activities from ChatGPT response", e);
        }
    }

    /**
     * Finds the first '[' and the last ']' in the response to extract a valid JSON array substring.
     */
    private String extractJsonArray(String response) {
        int start = response.indexOf('[');
        int end = response.lastIndexOf(']') + 1;
        if (start == -1 || end == 0 || end <= start) {
            throw new RuntimeException("No valid JSON array found in response");
        }
        return response.substring(start, end);
    }
}
