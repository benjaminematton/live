package com.example.live_backend.repository;

import com.example.live_backend.model.Notification;
import com.example.live_backend.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class NotificationRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private NotificationRepository notificationRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        entityManager.persist(testUser);

        // Create test notifications
        createNotification("Test notification 1", false);
        createNotification("Test notification 2", true);
        createNotification("Test notification 3", false);

        entityManager.flush();
    }

    private void createNotification(String message, boolean read) {
        Notification notification = new Notification();
        notification.setUser(testUser);
        notification.setMessage(message);
        notification.setType("TEST");
        notification.setRead(read);
        entityManager.persist(notification);
    }

    @Test
    void findByUserIdOrderByCreatedAtDesc_ShouldReturnPagedNotifications() {
        Page<Notification> notifications = notificationRepository
            .findByUserIdOrderByCreatedAtDesc(testUser.getId(), PageRequest.of(0, 10));

        assertThat(notifications.getContent()).hasSize(3);
        assertThat(notifications.getContent().get(0).getMessage()).contains("Test notification");
    }

    @Test
    void countByUserIdAndReadFalse_ShouldReturnUnreadCount() {
        long unreadCount = notificationRepository.countByUserIdAndReadFalse(testUser.getId());

        assertThat(unreadCount).isEqualTo(2);
    }

    @Test
    void markAllAsRead_ShouldUpdateAllNotifications() {
        notificationRepository.markAllAsRead(testUser.getId());
        entityManager.flush();
        entityManager.clear();

        long unreadCount = notificationRepository.countByUserIdAndReadFalse(testUser.getId());
        assertThat(unreadCount).isZero();
    }

    @Test
    void findByUserIdOrderByCreatedAtDesc_WithPagination_ShouldReturnCorrectPage() {
        // Create more notifications to test pagination
        for (int i = 4; i <= 15; i++) {
            createNotification("Test notification " + i, false);
        }
        entityManager.flush();

        Page<Notification> firstPage = notificationRepository
            .findByUserIdOrderByCreatedAtDesc(testUser.getId(), PageRequest.of(0, 5));
        Page<Notification> secondPage = notificationRepository
            .findByUserIdOrderByCreatedAtDesc(testUser.getId(), PageRequest.of(1, 5));

        assertThat(firstPage.getContent()).hasSize(5);
        assertThat(secondPage.getContent()).hasSize(5);
        assertThat(firstPage.getContent())
            .extracting("message")
            .doesNotContainAnyElementsOf(
                secondPage.getContent().stream()
                    .map(Notification::getMessage)
                    .toList()
            );
    }
} 