package org.example.service;

import org.example.dto.NoticiationStatus;
import org.example.dto.Notification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class NotificationServiceTest {

    private NotificationService notificationService;

    @BeforeEach
    public void init() {
        notificationService = new NotificationService(3);
    }

    @Test
    void testDeliverAndGetNotificationsAndMarkAsReadHappyPath() {
        notificationService.deliver("1", "1", "test1, order placed!");
        notificationService.deliver("1", "2", "test2, order completed!");
        notificationService.deliver("2", "1", "test1, order placed!");

        assertEquals(2, notificationService.getNotifications("1", 2).size());
        assertEquals("test2, order completed!", notificationService.getNotifications("1", 2).get(0).message());
        assertEquals(NoticiationStatus.UNREAD, notificationService.getNotifications("2", 2).get(0).noticiationStatus());

        notificationService.markAsRead("2", "1");
        assertEquals(NoticiationStatus.READ, notificationService.getNotifications("2", 2).get(0).noticiationStatus());
    }

    @Test
    void testMaxLimit() {
        notificationService.deliver("1", "1", "test1, order placed!");
        notificationService.deliver("1", "2", "test2, order completed!");
        notificationService.deliver("1", "3", "test3, order delivered!");
        notificationService.deliver("1", "4", "test4, order finished!");

        assertEquals(3, notificationService.getNotifications("1", 100).size());
        assertEquals(1, notificationService.getNotifications("1", 1).size());
        ArrayList<Notification> notifications = new ArrayList<>(notificationService.getNotifications("1", 100));
        notifications.forEach(v -> assertNotEquals("test1, order placed!", v.message()));
    }

    @Test
    void testMessageOrderNotChanged() {
        notificationService.deliver("1", "1", "test1, order placed!");
        notificationService.deliver("1", "2", "test2, order completed!");
        notificationService.deliver("1", "3", "test3, order delivered!");

        assertEquals("test3, order delivered!", notificationService.getNotifications("1", 4).get(0).message());
        notificationService.markAsRead("1", "1");
        assertEquals("test3, order delivered!", notificationService.getNotifications("1", 4).get(0).message());

        notificationService.deliver("1", "4", "test4, order finished!");
        ArrayList<Notification> notifications = new ArrayList<>(notificationService.getNotifications("1", 100));
        assertEquals(3, notifications.size());
        assertEquals("test4, order finished!", notifications.get(0).message());
        assertEquals("test3, order delivered!", notifications.get(1).message());
        assertEquals("test2, order completed!", notifications.get(2).message());
    }

    @Test
    void testDuplicateDeliveryIdempotency() {
        Boolean res1 = notificationService.deliver("1", "1", "test1");
        notificationService.deliver("1", "2", "test2");
        notificationService.markAsRead("1","1");
        Boolean res2 = notificationService.deliver("1", "1", "test1");

        assertEquals(res1, res2);
        ArrayList<Notification> notifications = new ArrayList<>(notificationService.getNotifications("1", 100));
        assertEquals(2, notifications.size());
        assertEquals("test2", notifications.get(0).message());
        assertEquals("test1", notifications.get(1).message());
        assertEquals(NoticiationStatus.READ, notifications.get(1).noticiationStatus());
    }

    @Test
    void testConflictDiffMessageExpectException(){
        notificationService.deliver("1", "1", "test1");
        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> notificationService.deliver("1", "1", "test2")
        );
        assertEquals(
                "Same notification id but different message.",
                ex.getMessage()
        );

    }
}