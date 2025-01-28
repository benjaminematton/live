package com.example.live_backend.repository;

import com.example.live_backend.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByUserIdOrderByStartDateDesc(Long userId);
    List<Schedule> findByUserUsernameOrderByStartDateDesc(String username);
} 