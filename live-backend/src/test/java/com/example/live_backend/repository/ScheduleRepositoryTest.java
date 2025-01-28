package com.example.live_backend.repository;

import com.example.live_backend.model.Schedule;
import com.example.live_backend.model.User;
import com.example.live_backend.model.ScheduleVisibility;
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
    private ScheduleRepository scheduleRepository;

    private User testUser;
    private Schedule schedule1;
    private Schedule schedule2;

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        entityManager.persist(testUser);

        // Create test schedules
        schedule1 = new Schedule();
        schedule1.setTitle("Test Schedule 1");
        schedule1.setDescription("Description 1");
        schedule1.setStartDate(LocalDateTime.now());
        schedule1.setEndDate(LocalDateTime.now().plusDays(1));
        schedule1.setUser(testUser);
        schedule1.setVisibility(ScheduleVisibility.PUBLIC);
        entityManager.persist(schedule1);

        schedule2 = new Schedule();
        schedule2.setTitle("Test Schedule 2");
        schedule2.setDescription("Description 2");
        schedule2.setStartDate(LocalDateTime.now().plusDays(2));
        schedule2.setEndDate(LocalDateTime.now().plusDays(3));
        schedule2.setUser(testUser);
        schedule2.setVisibility(ScheduleVisibility.PRIVATE);
        entityManager.persist(schedule2);

        entityManager.flush();
    }

    @Test
    void findByUserIdOrderByStartDateDesc_ShouldReturnSchedulesInOrder() {
        List<Schedule> schedules = scheduleRepository.findByUserIdOrderByStartDateDesc(testUser.getId());
        
        assertThat(schedules).hasSize(2);
        assertThat(schedules.get(0).getStartDate())
            .isAfterOrEqualTo(schedules.get(1).getStartDate());
    }

    @Test
    void findByUserUsernameOrderByStartDateDesc_ShouldReturnSchedulesInOrder() {
        List<Schedule> schedules = scheduleRepository.findByUserUsernameOrderByStartDateDesc("testuser");
        
        assertThat(schedules).hasSize(2);
        assertThat(schedules.get(0).getStartDate())
            .isAfterOrEqualTo(schedules.get(1).getStartDate());
    }

    @Test
    void saveSchedule_ShouldSetCreatedAndUpdatedDates() {
        Schedule newSchedule = new Schedule();
        newSchedule.setTitle("New Schedule");
        newSchedule.setStartDate(LocalDateTime.now());
        newSchedule.setEndDate(LocalDateTime.now().plusDays(1));
        newSchedule.setUser(testUser);

        Schedule savedSchedule = scheduleRepository.save(newSchedule);

        assertThat(savedSchedule.getCreatedAt()).isNotNull();
        assertThat(savedSchedule.getUpdatedAt()).isNotNull();
    }
} 