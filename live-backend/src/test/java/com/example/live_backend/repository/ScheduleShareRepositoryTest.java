package com.example.live_backend.repository;

import com.example.live_backend.model.Schedule;
import com.example.live_backend.model.ScheduleShare;
import com.example.live_backend.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ScheduleShareRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ScheduleShareRepository scheduleShareRepository;

    private User owner;
    private User sharedWith;
    private Schedule schedule;
    private ScheduleShare scheduleShare;

    @BeforeEach
    void setUp() {
        // Create users
        owner = new User();
        owner.setUsername("owner");
        owner.setEmail("owner@example.com");
        owner.setPassword("password");
        entityManager.persist(owner);

        sharedWith = new User();
        sharedWith.setUsername("shared");
        sharedWith.setEmail("shared@example.com");
        sharedWith.setPassword("password");
        entityManager.persist(sharedWith);

        // Create schedule
        schedule = new Schedule();
        schedule.setTitle("Test Schedule");
        schedule.setStartDate(LocalDateTime.now());
        schedule.setEndDate(LocalDateTime.now().plusDays(1));
        schedule.setUser(owner);
        entityManager.persist(schedule);

        // Create schedule share
        scheduleShare = new ScheduleShare();
        scheduleShare.setSchedule(schedule);
        scheduleShare.setSharedWith(sharedWith);
        entityManager.persist(scheduleShare);

        entityManager.flush();
    }

    @Test
    void findByScheduleId_ShouldReturnShares() {
        List<ScheduleShare> shares = scheduleShareRepository.findByScheduleId(schedule.getId());
        
        assertThat(shares).hasSize(1);
        assertThat(shares.get(0).getSharedWith().getUsername()).isEqualTo("shared");
    }

    @Test
    void findBySharedWithId_ShouldReturnShares() {
        List<ScheduleShare> shares = scheduleShareRepository.findBySharedWithId(sharedWith.getId());
        
        assertThat(shares).hasSize(1);
        assertThat(shares.get(0).getSchedule().getTitle()).isEqualTo("Test Schedule");
    }

    @Test
    void findByScheduleIdAndSharedWithId_ShouldReturnShare() {
        Optional<ScheduleShare> share = scheduleShareRepository
            .findByScheduleIdAndSharedWithId(schedule.getId(), sharedWith.getId());
        
        assertThat(share).isPresent();
        assertThat(share.get().getSharedWith().getUsername()).isEqualTo("shared");
    }

    @Test
    void deleteByScheduleIdAndSharedWithId_ShouldRemoveShare() {
        scheduleShareRepository.deleteByScheduleIdAndSharedWithId(schedule.getId(), sharedWith.getId());
        entityManager.flush();
        
        Optional<ScheduleShare> share = scheduleShareRepository
            .findByScheduleIdAndSharedWithId(schedule.getId(), sharedWith.getId());
        
        assertThat(share).isEmpty();
    }

    @Test
    void save_ShouldSetSharedAtTimestamp() {
        ScheduleShare newShare = new ScheduleShare();
        newShare.setSchedule(schedule);
        newShare.setSharedWith(sharedWith);
        
        ScheduleShare savedShare = scheduleShareRepository.save(newShare);
        
        assertThat(savedShare.getSharedAt()).isNotNull();
    }

    @Test
    void findByScheduleId_WithNoShares_ShouldReturnEmptyList() {
        Schedule newSchedule = new Schedule();
        newSchedule.setTitle("Unshared Schedule");
        newSchedule.setStartDate(LocalDateTime.now());
        newSchedule.setEndDate(LocalDateTime.now().plusDays(1));
        newSchedule.setUser(owner);
        entityManager.persist(newSchedule);
        
        List<ScheduleShare> shares = scheduleShareRepository.findByScheduleId(newSchedule.getId());
        
        assertThat(shares).isEmpty();
    }
} 