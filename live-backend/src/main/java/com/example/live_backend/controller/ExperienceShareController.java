package com.example.live_backend.controller;

import com.example.live_backend.dto.ShareExperienceRequest;
import com.example.live_backend.model.Experience;
import com.example.live_backend.security.CustomUserDetails;
import com.example.live_backend.service.ExperienceShareService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/experiences/{experienceId}/shares")
@RequiredArgsConstructor
public class ExperienceShareController {

    private final ExperienceShareService experienceShareService;

    @PostMapping("/{experienceId}/share")
    public ResponseEntity<Void> shareExperience(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long experienceId,
            @Valid @RequestBody ShareExperienceRequest request) {
        experienceShareService.shareExperience(experienceId, userDetails.getUsername(), request.getUsernames());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{experienceId}/share/{username}")
    public ResponseEntity<Void> unshareExperience(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long experienceId,
            @PathVariable String username) {
        experienceShareService.unshareExperience(experienceId, userDetails.getUsername(), username);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/shared")
        public ResponseEntity<List<Experience>> getSharedExperiences(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<Experience> experiences = experienceShareService.getSharedExperiences(userDetails.getUsername());
        return ResponseEntity.ok(experiences);
    }
}
