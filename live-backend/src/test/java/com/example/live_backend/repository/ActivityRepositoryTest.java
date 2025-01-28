package com.example.live_backend.repository;

import com.example.live_backend.model.Activity;
import com.example.live_backend.model.Schedule;
import com.example.live_backend.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ActivityRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ActivityRepository activityRepository;

    private User testUser;
    private Schedule testSchedule;
    private Activity activity1;
    private Activity activity2;

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        entityManager.persist(testUser);

        // Create test schedule
        testSchedule = new Schedule();
        testSchedule.setTitle("Test Schedule");
        testSchedule.setStartDate(LocalDateTime.now());
        testSchedule.setEndDate(LocalDateTime.now().plusDays(1));
        testSchedule.setUser(testUser);
        entityManager.persist(testSchedule);

        // Create test activities
        activity1 = new Activity();
        activity1.setTitle("Activity 1");
        activity1.setDescription("Description 1");
        activity1.setStartTime(LocalDateTime.now());
        activity1.setEndTime(LocalDateTime.now().plusHours(1));
        activity1.setSchedule(testSchedule);
        entityManager.persist(activity1);

        activity2 = new Activity();
        activity2.setTitle("Activity 2");
        activity2.setDescription("Description 2");
        activity2.setStartTime(LocalDateTime.now().plusHours(2));
        activity2.setEndTime(LocalDateTime.now().plusHours(3));
        activity2.setSchedule(testSchedule);
        entityManager.persist(activity2);

        entityManager.flush();
    }

    @Test
    void findByScheduleIdOrderByStartTime_ShouldReturnActivitiesInOrder() {
        List<Activity> activities = activityRepository.findByScheduleIdOrderByStartTime(testSchedule.getId());
        
        assertThat(activities).hasSize(2);
        assertThat(activities.get(0).getStartTime())
            .isBefore(activities.get(1).getStartTime());
    }

    @Test
    void saveActivity_ShouldSetCreatedAndUpdatedDates() {
        Activity newActivity = new Activity();
        newActivity.setTitle("New Activity");
        newActivity.setStartTime(LocalDateTime.now());
        newActivity.setEndTime(LocalDateTime.now().plusHours(1));
        newActivity.setSchedule(testSchedule);

        Activity savedActivity = activityRepository.save(newActivity);

        assertThat(savedActivity.getCreatedAt()).isNotNull();
        assertThat(savedActivity.getUpdatedAt()).isNotNull();
    }

    @Test
    void deleteActivity_ShouldRemoveActivity() {
        activityRepository.delete(activity1);
        List<Activity> activities = activityRepository.findByScheduleIdOrderByStartTime(testSchedule.getId());
        
        assertThat(activities).hasSize(1);
        assertThat(activities.get(0).getId()).isEqualTo(activity2.getId());
    }
} 