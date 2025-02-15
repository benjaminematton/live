package com.example.live_backend.dto.User;
import lombok.Data;

@Data
public class UserResponse {

    private Long id;
    private String username;
    private String email;
    private String profilePicture;
    private String bio;

    // Possibly you want to show some minimal info about followers/following 
    // (like counts, or a list of IDs). Including entire sets of user objects 
    // can get large. Example:
    private int followerCount;
    private int followingCount;

    // If you want to show if the user is an admin or not:
    private String role;  //take out?

    private boolean shareLocation;
}