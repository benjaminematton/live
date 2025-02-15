package com.example.live_backend.dto.Experience;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.List;

@Data
public class ShareExperienceRequest {
    @NotEmpty
    private List<String> usernames;
} 