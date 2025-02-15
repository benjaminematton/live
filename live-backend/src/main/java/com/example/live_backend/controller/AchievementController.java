package com.example.live_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.live_backend.dto.Achivement.AchievementResponse;
import com.example.live_backend.dto.User.UserAchievementResponse;
import com.example.live_backend.model.User.User;
import com.example.live_backend.repository.User.UserAchievementRepository;
import com.example.live_backend.repository.User.UserRepository;
import com.example.live_backend.service.AchievementService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/achievements")
@RequiredArgsConstructor
public class AchievementController {
    private final AchievementService achievementService;
    private final UserAchievementRepository userAchievementRepository;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<AchievementResponse>> getAllAchievements() {
        List<AchievementResponse> all = achievementService.findAll(); // or from repo
        return ResponseEntity.ok(all);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<UserAchievementResponse>> getUserAchievements(@PathVariable Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        List<UserAchievementResponse> earned = userAchievementRepository.findAllByUser(user);
        return ResponseEntity.ok(earned);
    }
}
