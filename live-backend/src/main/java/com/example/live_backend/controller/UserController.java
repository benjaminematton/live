package com.example.live_backend.controller;

import com.example.live_backend.dto.UpdateProfileRequest;
import com.example.live_backend.dto.UserProfileResponse;
import com.example.live_backend.security.CustomUserDetails;
import com.example.live_backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.example.live_backend.dto.UserAchievementResponse;
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        UserProfileResponse profile = userService.getUserProfile(userDetails.getUsername());
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserProfileResponse> getUserProfile(@PathVariable String username) {
        UserProfileResponse profile = userService.getUserProfile(username);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UpdateProfileRequest request) {
        UserProfileResponse updatedProfile = userService.updateProfile(userDetails.getUsername(), request);
        return ResponseEntity.ok(updatedProfile);
    }

    @GetMapping("/friends")
    public ResponseEntity<List<UserProfileResponse>> getFriends(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<UserProfileResponse> friends = userService.getFriends(userDetails.getUsername());
        return ResponseEntity.ok(friends);
    }

    @GetMapping("/following")
    public ResponseEntity<List<UserProfileResponse>> getFollowing(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<UserProfileResponse> following = userService.getFollowing(userDetails.getUsername());
        return ResponseEntity.ok(following);
    }

    @GetMapping("/followers")
    public ResponseEntity<List<UserProfileResponse>> getFollowers(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<UserProfileResponse> followers = userService.getFollowers(userDetails.getUsername());
        return ResponseEntity.ok(followers);
    }

    @PostMapping("/{userIdToFollow}/follow")
    public ResponseEntity<Void> followUser(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long userIdToFollow) {
        userService.followUser(userDetails.getUsername(), userIdToFollow);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{userIdToUnfollow}/unfollow")
    public ResponseEntity<Void> unfollowUser(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long userIdToUnfollow) {
        userService.unfollowUser(userDetails.getUsername(), userIdToUnfollow);
        return ResponseEntity.ok().build();
    }
} 