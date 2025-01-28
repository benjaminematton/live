package com.example.live_backend.repository;

import com.example.live_backend.model.ScheduleShare;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ScheduleShareRepository extends JpaRepository<ScheduleShare, Long> {
    List<ScheduleShare> findByScheduleId(Long scheduleId);
    List<ScheduleShare> findBySharedWithId(Long userId);
    Optional<ScheduleShare> findByScheduleIdAndSharedWithId(Long scheduleId, Long userId);
    void deleteByScheduleIdAndSharedWithId(Long scheduleId, Long userId);
} 