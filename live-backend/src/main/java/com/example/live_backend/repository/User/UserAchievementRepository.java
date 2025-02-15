package com.example.live_backend.repository.User;

import com.example.live_backend.dto.User.UserAchievementResponse;
import com.example.live_backend.model.Achievement;
import com.example.live_backend.model.User.User;
import com.example.live_backend.model.User.UserAchievement;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAchievementRepository extends JpaRepository<UserAchievement, Long> {
    boolean existsByUserAndAchievement(User user, Achievement achievement);

    List<UserAchievementResponse> findAllByUser(User user); 
}
