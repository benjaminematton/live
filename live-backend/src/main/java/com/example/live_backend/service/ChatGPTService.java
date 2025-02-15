package com.example.live_backend.service;

import com.example.live_backend.dto.Activity.ActivityResponse;
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
     * Generates schedule suggestions based on the user's prompt, location, and optional start/end times.
     * It appends to the conversation so ChatGPT sees prior context if the user calls again.
     */
    public List<ActivityResponse> generateActivitiesSuggestions(
            String prompt,
            LocalDateTime startDate,
            LocalDateTime endDate,
            String location
    ) {
        try {
            // If this is the first call in the conversation, we add a system message
            if (conversation.isEmpty()) {
                String systemPrompt = buildInitialSystemPrompt(prompt, location, startDate, endDate);
                conversation.add(new ChatMessage("system", systemPrompt));
            }

            // Add the user's latest prompt as a user message
            String userMessageContent = prompt;
            if (startDate != null && endDate != null) {
                userMessageContent += "\n(Activities should be between "
                        + startDate + " and " + endDate + ")";
            }
            conversation.add(new ChatMessage("user", userMessageContent));

            // Send to OpenAI and parse response
            String response = sendChatCompletionRequest(conversation);
            return parseActivities(response);

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate schedule suggestions", e);
        }
    }

    /**
     * Allows the user to refine the *existing* schedule. We pass the current schedule JSON
     * and a refinement prompt, then ChatGPT returns an updated schedule. 
     * This also appends to the same conversation for context.
     */
    public List<ActivityResponse> refineExperience(
            List<ActivityResponse> currentActivities,
            String refinementPrompt
    ) {
        try {
            // Convert current schedule to JSON
            String currentScheduleJson = objectMapper.writeValueAsString(currentActivities);

            // We add a system message so ChatGPT knows how to handle refinement
            String refinementSystemPrompt = """
                You are an assistant that refines schedules.
                You receive the user's current schedule as a JSON array of activities, plus a refinement request.
                Return only the updated schedule in valid JSON, preserving the same structure and keys:
                [title, description, location, startTime, endTime].
                
                If the user wants changes—like editing times, removing or adding activities—apply them.
                Do not include any extra commentary, keys, or code blocks.
                Output must be a JSON array of objects, each with exactly the five keys listed.
                No other text outside of the JSON array.
            """;
            // We add or replace the system message for refinement (optional approach).
            // If you want to *append* to the existing conversation while changing instructions, 
            // you can add another system message. 
            // Alternatively, if you'd rather keep the first system message, remove this step.
            conversation.add(new ChatMessage("system", refinementSystemPrompt));

            // Build a user message referencing the existing schedule
            String userMessageContent = "Current schedule: " + currentScheduleJson
                    + "\n\nRefinement request: " + refinementPrompt;
            conversation.add(new ChatMessage("user", userMessageContent));

            // Send request
            String response = sendChatCompletionRequest(conversation);
            return parseActivities(response);

        } catch (Exception e) {
            throw new RuntimeException("Failed to refine schedule", e);
        }
    }

    /**
     * Recommends follow-up activities based on a chosen activity.
     * Also appends to the conversation so ChatGPT is aware of the prior context.
     */
    public List<ActivityResponse> recommendActivities(String chosenActivity) {
        try {
            // Create a user message referencing the chosen activity
            String userMessageContent = "The user has chosen: " + chosenActivity
                    + ". Suggest 3 follow-up activities that pair well with it.";
            conversation.add(new ChatMessage("user", userMessageContent));

            String response = sendChatCompletionRequest(conversation);
            return parseActivities(response);

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate activity recommendations", e);
        }
    }

    /**
     * Helper to create the system prompt for the initial schedule generation.
     */
    private String buildInitialSystemPrompt(
            String userPrompt,
            String location,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        String systemPrompt = """
            You are an assistant that returns only valid JSON.
            
            The user wants to do the following in {LOCATION}:

            \"\"\"
            {PROMPT_PLACEHOLDER}
            \"\"\"

            Based on how many things the user wants to do, suggest a corresponding list of activities.
            - If the user input implies only one desired activity, return one object.
            - If it implies multiple activities, return multiple objects.

            Identify specific real place names for each desired activity and put that in the "title" field.
            Return only a JSON array of objects, where each object has exactly the following keys:
            - "title" (string)
            - "description" (string)
            - "startTime" (ISO 8601 format)
            - "endTime" (ISO 8601 format)

            No extra keys, no additional commentary, and do not wrap the JSON in code blocks.
            """;

        // Inject location and prompt
        systemPrompt = systemPrompt
                .replace("{LOCATION}", location == null ? "an unspecified location" : location)
                .replace("{PROMPT_PLACEHOLDER}", userPrompt);

        // Add optional scheduling constraints
        if (startDate != null && endDate != null) {
            systemPrompt += "\n\nAll activities should be scheduled between "
                    + startDate + " and " + endDate + ".";
        }
        return systemPrompt;
    }

    /**
     * Sends the current conversation to the OpenAI API and returns the assistant's textual response.
     */
    private String sendChatCompletionRequest(List<ChatMessage> conversation) {
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(model)
                .messages(conversation)
                .temperature(0.7)
                .build();

        String response = openAiService.createChatCompletion(request)
                .getChoices()
                .get(0)
                .getMessage()
                .getContent();

        // Record the assistant response in the conversation
        conversation.add(new ChatMessage("assistant", response));
        return response;
    }

    /**
     * Parses the JSON array returned by ChatGPT into a list of ActivityDto.
     */
    private List<ActivityResponse> parseActivities(String jsonResponse) {
        try {
            // Extract the JSON array from the assistant's response
            String jsonArray = extractJsonArray(jsonResponse);

            // Convert that array into a list of ActivityDto
            return objectMapper.readValue(jsonArray, new TypeReference<List<ActivityResponse>>() {});
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
