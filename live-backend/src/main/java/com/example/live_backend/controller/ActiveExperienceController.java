package com.example.live_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.example.live_backend.dto.Activity.ActiveExperienceRequest;
import com.example.live_backend.dto.Activity.ActiveExperienceResponse;
import com.example.live_backend.security.CustomUserDetails;
import com.example.live_backend.service.Experience.ActiveExperienceService;
import com.example.live_backend.dto.User.LocationUpdateRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ActiveExperienceController {
    private final ActiveExperienceService activeExperienceService;

    // @PostMapping("/active-experiences")
    // public ActiveExperienceResponse startActiveExperience(@RequestBody ActiveExperienceRequest request) {
    //     return activeExperienceService.startExperience(request);
    // }

    @PostMapping("/{activeExperienceId}/location")
    public ResponseEntity<Void> updateLocation(@PathVariable Long activeExperienceId, @AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody LocationUpdateRequest req) {
        activeExperienceService.updateUserLocation(activeExperienceId, userDetails.getUser().getId(), req.getLatitude(), req.getLongitude());
        return ResponseEntity.ok().build();
    }
}
