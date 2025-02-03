package com.example.live_backend.repository;

import com.example.live_backend.model.Achievement;
import com.example.live_backend.model.User;
import com.example.live_backend.model.UserAchievement;
import com.example.live_backend.dto.UserAchievementResponse;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAchievementRepository extends JpaRepository<UserAchievement, Long> {
    boolean existsByUserAndAchievement(User user, Achievement achievement);

    List<UserAchievementResponse> findAllByUser(User user); 
}
