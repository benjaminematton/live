package com.example.live_backend.repository.Activity;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.live_backend.model.Activity.ActiveActivity;

public interface ActiveActivityRepository extends JpaRepository<ActiveActivity, Long> {
    
}
