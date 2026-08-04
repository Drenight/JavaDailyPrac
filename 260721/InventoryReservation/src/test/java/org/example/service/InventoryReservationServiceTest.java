package org.example.service;

import org.example.dto.ReservationOrderDetail;
import org.example.enums.OrderStatus;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

class InventoryReservationServiceTest {
    InventoryReservationService inventoryReservationService;

    @BeforeEach
    void setUp() {
        inventoryReservationService = new InventoryReservationService();
        inventoryReservationService.AddStock("1", 10);
        inventoryReservationService.AddStock("2", 10);
        inventoryReservationService.AddStock("3", 10);
    }

    @org.junit.jupiter.api.Test
    void testQueryStock() {
        assertEquals(10, inventoryReservationService.QueryStock("1"));
    }

    @org.junit.jupiter.api.Test
    void testQueryOrderStatus() {
    }

    @org.junit.jupiter.api.Test
    void testReserveHappyPath() {
        ReservationOrderDetail orderDetail = inventoryReservationService.Reserve("1", "test_user", "1", 5);
        assertEquals(5, inventoryReservationService.QueryStock("1"));
        assertEquals(OrderStatus.ACTIVE, inventoryReservationService.QueryOrderStatus("1"));
    }

    @org.junit.jupiter.api.Test
    void confirmReservation() {
    }

    @org.junit.jupiter.api.Test
    void cancelReservation() {
    }
}