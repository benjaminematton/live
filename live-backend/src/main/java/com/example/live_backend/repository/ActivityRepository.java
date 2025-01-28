package com.example.live_backend.repository;

import com.example.live_backend.model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    List<Activity> findByScheduleIdOrderByStartTime(Long scheduleId);
} 