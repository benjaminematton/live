package com.example.live_backend.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.List;

@Data
public class ShareScheduleRequest {
    @NotEmpty
    private List<String> usernames;
} 