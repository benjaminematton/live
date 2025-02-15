package com.example.live_backend.dto.Activity;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class ActivityDefinitionResponse {
    private Long id;
    private String title;
    private String location;
} 