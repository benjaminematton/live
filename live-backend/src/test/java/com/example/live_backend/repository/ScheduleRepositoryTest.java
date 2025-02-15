package com.example.live_backend.repository;

import com.example.live_backend.model.Experience.Experience;
import com.example.live_backend.model.Experience.ExperienceVisibility;
import com.example.live_backend.model.User.User;
import com.example.live_backend.repository.Experience.ExperienceRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ScheduleRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ExperienceRepository scheduleRepository;

    private User testUser;
    private Experience schedule1;
    private Experience schedule2;

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        entityManager.persist(testUser);

        // Create test schedules
        schedule1 = new Experience();
        schedule1.setTitle("Test Schedule 1");
        schedule1.setStartDate(LocalDateTime.now());
        schedule1.setEndDate(LocalDateTime.now().plusDays(1));
        schedule1.setUser(testUser);
        schedule1.setVisibility(ExperienceVisibility.PUBLIC);
        entityManager.persist(schedule1);

        schedule2 = new Experience();
        schedule2.setTitle("Test Schedule 2");
        schedule2.setStartDate(LocalDateTime.now().plusDays(2));
        schedule2.setEndDate(LocalDateTime.now().plusDays(3));
        schedule2.setUser(testUser);
        schedule2.setVisibility(ExperienceVisibility.PRIVATE);
        entityManager.persist(schedule2);

        entityManager.flush();
    }

    @Test
    void findByUserIdOrderByStartDateDesc_ShouldReturnSchedulesInOrder() {
        List<Experience> schedules = scheduleRepository.findByUserIdOrderByStartDateDesc(testUser.getId());
        
        assertThat(schedules).hasSize(2);
        assertThat(schedules.get(0).getStartDate())
            .isAfterOrEqualTo(schedules.get(1).getStartDate());
    }

    @Test
    void findByUserUsernameOrderByStartDateDesc_ShouldReturnSchedulesInOrder() {
        List<Experience> schedules = scheduleRepository.findByUserUsernameOrderByStartDateDesc("testuser");
        
        assertThat(schedules).hasSize(2);
        assertThat(schedules.get(0).getStartDate())
            .isAfterOrEqualTo(schedules.get(1).getStartDate());
    }

    @Test
    void saveSchedule_ShouldSetCreatedAndUpdatedDates() {
        Experience newSchedule = new Experience();
        newSchedule.setTitle("New Schedule");
        newSchedule.setStartDate(LocalDateTime.now());
        newSchedule.setEndDate(LocalDateTime.now().plusDays(1));
        newSchedule.setUser(testUser);

        Experience savedSchedule = scheduleRepository.save(newSchedule);

        assertThat(savedSchedule.getCreatedAt()).isNotNull();
        assertThat(savedSchedule.getUpdatedAt()).isNotNull();
    }
} 