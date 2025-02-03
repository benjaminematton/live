package com.example.live_backend.repository;

import com.example.live_backend.model.Experience;
import com.example.live_backend.model.ExperienceShare;
import com.example.live_backend.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import com.example.live_backend.repository.ExperienceShareRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ScheduleShareRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ExperienceShareRepository experienceShareRepository;

    private User owner;
    private User sharedWith;
    private Experience schedule;
    private ExperienceShare scheduleShare;

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
        schedule = new Experience();
        schedule.setTitle("Test Schedule");
        schedule.setStartDate(LocalDateTime.now());
        schedule.setEndDate(LocalDateTime.now().plusDays(1));
        schedule.setUser(owner);
        entityManager.persist(schedule);

        // Create schedule share
        scheduleShare = new ExperienceShare();
        scheduleShare.setExperience(schedule);
        scheduleShare.setSharedWith(sharedWith);
        entityManager.persist(scheduleShare);

        entityManager.flush();
    }

    @Test
    void findByScheduleId_ShouldReturnShares() {
        List<ExperienceShare> shares = experienceShareRepository.findByExperienceId(schedule.getId());
        
        assertThat(shares).hasSize(1);
        assertThat(shares.get(0).getSharedWith().getUsername()).isEqualTo("shared");
    }

    @Test
    void findBySharedWithId_ShouldReturnShares() {
        List<ExperienceShare> shares = experienceShareRepository.findBySharedWithId(sharedWith.getId());
        
        assertThat(shares).hasSize(1);
        assertThat(shares.get(0).getExperience().getTitle()).isEqualTo("Test Schedule");
    }

    @Test
    void findByScheduleIdAndSharedWithId_ShouldReturnShare() {
        Optional<ExperienceShare> share = experienceShareRepository
            .findByExperienceIdAndSharedWithId(schedule.getId(), sharedWith.getId());
        
        assertThat(share).isPresent();
        assertThat(share.get().getSharedWith().getUsername()).isEqualTo("shared");
    }

    @Test
    void deleteByScheduleIdAndSharedWithId_ShouldRemoveShare() {
        experienceShareRepository.deleteByExperienceIdAndSharedWithId(schedule.getId(), sharedWith.getId());
        entityManager.flush();
        
        Optional<ExperienceShare> share = experienceShareRepository
            .findByExperienceIdAndSharedWithId(schedule.getId(), sharedWith.getId());
        
        assertThat(share).isEmpty();
    }

    @Test
    void save_ShouldSetSharedAtTimestamp() {
        ExperienceShare newShare = new ExperienceShare();
        newShare.setExperience(schedule);
        newShare.setSharedWith(sharedWith);
        
        ExperienceShare savedShare = experienceShareRepository.save(newShare);
        
        assertThat(savedShare.getSharedAt()).isNotNull();
    }

    @Test
    void findByScheduleId_WithNoShares_ShouldReturnEmptyList() {
        Experience newSchedule = new Experience();
        newSchedule.setTitle("Unshared Schedule");
        newSchedule.setStartDate(LocalDateTime.now());
        newSchedule.setEndDate(LocalDateTime.now().plusDays(1));
        newSchedule.setUser(owner);
        entityManager.persist(newSchedule);
        
        List<ExperienceShare> shares = experienceShareRepository.findByExperienceId(newSchedule.getId());
        
        assertThat(shares).isEmpty();
    }
} 