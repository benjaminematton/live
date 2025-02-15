package com.example.live_backend.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;

import com.example.live_backend.service.Activity.ActiveActivityService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ActiveActivityController {

    private final ActiveActivityService activeActivityService;

    @PostMapping("/active-activities/{activeActivityId}/submit-photo")
    public ResponseEntity<Void> submitActivityPhoto(
        @PathVariable Long activeActivityId,
        @RequestPart("photo") MultipartFile photo
    ) {
        activeActivityService.submitActivityPhoto(activeActivityId, photo);
        return ResponseEntity.ok().build();
    }
}
