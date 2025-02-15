package com.example.live_backend.controller;

import com.example.live_backend.dto.User.ShareLocationRequest;
import com.example.live_backend.dto.User.UserRequest;
import com.example.live_backend.dto.User.UserResponse;
import com.example.live_backend.security.CustomUserDetails;
import com.example.live_backend.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(userService.getUserById(userDetails.getUser().getId()));
    }

    @GetMapping("/profile/{username}")
    public ResponseEntity<UserResponse> getUserProfile(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/profile")
    public ResponseEntity<UserResponse> updateProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.updateUser(userDetails.getUser().getId(), request));
    }

    @GetMapping("/friends")
    public ResponseEntity<Set<UserResponse>> getFriends(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(userService.getFriends(userDetails.getUser().getId()));
    }

    @GetMapping("/following")
    public ResponseEntity<Set<UserResponse>> getFollowing(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(userService.getFollowing(userDetails.getUser().getId()));
    }

    @GetMapping("/followers")
    public ResponseEntity<Set<UserResponse>> getFollowers(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(userService.getFollowers(userDetails.getUser().getId()));
    }

    @PostMapping("/{userIdToFollow}/follow")
    public ResponseEntity<Void> followUser(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long userIdToFollow) {
        userService.followUser(userDetails.getUser().getId(), userIdToFollow);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{userIdToUnfollow}/unfollow")
    public ResponseEntity<Void> unfollowUser(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long userIdToUnfollow) {
        userService.unfollowUser(userDetails.getUser().getId(), userIdToUnfollow);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{userId}/share-location")
    public ResponseEntity<Void> setShareLocation(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long userId, @RequestBody ShareLocationRequest request) {
        userService.setUserShareLocation(userId, request.isShareLocation());
        return ResponseEntity.ok().build();
    }
} 