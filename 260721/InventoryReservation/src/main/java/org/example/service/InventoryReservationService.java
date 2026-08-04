package org.example.service;

import org.example.dto.ReservationOrderDetail;
import org.example.enums.OrderStatus;

import java.util.HashMap;

public class InventoryReservationService {

    private final HashMap<String, Integer> inventoriesQuantity;
    private final HashMap<String, ReservationOrderDetail> reservationId2OrderDetail;
    private final HashMap<String, OrderStatus> reservationId2ReservationStatus;

    public InventoryReservationService() {
        inventoriesQuantity = new HashMap<>();
        reservationId2OrderDetail = new HashMap<>();
        reservationId2ReservationStatus = new HashMap<>();
    }

    public Boolean AddStock(String sku, int quantity) {
        if (inventoriesQuantity.containsKey(sku)) {
            Integer currentQuantity = inventoriesQuantity.get(sku);
            inventoriesQuantity.put(sku, currentQuantity + quantity);
            return true;
        } else {
            inventoriesQuantity.put(sku, quantity);
            return true;
        }
    }

    public Integer QueryStock(String sku) {
        if (inventoriesQuantity.containsKey(sku)) {
            return inventoriesQuantity.get(sku);
        } else {
            throw new RuntimeException("sku not found. sku= " + sku);
        }
    }

    public OrderStatus QueryOrderStatus(String reservationId) {
        if (reservationId2ReservationStatus.containsKey(reservationId)) {
            return reservationId2ReservationStatus.get(reservationId);
        }else{
            throw new RuntimeException("reservationId not found. reservationId= " + reservationId);
        }
    }

    public ReservationOrderDetail Reserve(String reservationId, String userId, String sku, int quantity) {
        ReservationOrderDetail newReservationOrderDetail = new ReservationOrderDetail(reservationId, userId, sku, quantity);
        if (reservationId2OrderDetail.containsKey(reservationId)) {
            ReservationOrderDetail oldReservationOrderDetail = reservationId2OrderDetail.get(reservationId);
            if (oldReservationOrderDetail.equals(newReservationOrderDetail)) {
                return oldReservationOrderDetail;
            } else {
                throw new RuntimeException(String.format("Order already exists and doesn't match record, need recon, reservationId={}", reservationId));
            }
        } else {
            Integer remainQuantity = inventoriesQuantity.get(sku);
            if (remainQuantity < quantity) {
                throw new RuntimeException(String.format("Quantity is less than or equal to quantity, reservationId={}", reservationId));
            } else {
                inventoriesQuantity.put(sku, remainQuantity - quantity);
                reservationId2OrderDetail.put(reservationId, newReservationOrderDetail);
                reservationId2ReservationStatus.put(reservationId, OrderStatus.ACTIVE);
            }
        }
        return null;
    }

    public boolean ConfirmReservation(String reservationId) {
        if (!reservationId2OrderDetail.containsKey(reservationId)) {
            throw new RuntimeException(String.format("Order does not exist, reservationId=%s", reservationId));
        } else {
            if (reservationId2ReservationStatus.get(reservationId).equals(OrderStatus.ACTIVE)) {
                reservationId2ReservationStatus.put(reservationId, OrderStatus.CONFIRMED);
            } else {
                throw new RuntimeException(String.format("Order status does not support action=%s, reservationId=%s", "CONFIRM", reservationId));
            }
        }
        return false;
    }

    public boolean CancelReservation(String reservationId) {
        if (!reservationId2OrderDetail.containsKey(reservationId)) {
            throw new RuntimeException(String.format("Order does not exist, reservationId=%s", reservationId));
        } else {
            if (reservationId2ReservationStatus.get(reservationId).equals(OrderStatus.ACTIVE)) {
                reservationId2ReservationStatus.put(reservationId, OrderStatus.CANCELLED);
                ReservationOrderDetail reservationOrderDetail = reservationId2OrderDetail.get(reservationId);
                Integer nowQuantity = inventoriesQuantity.get(reservationOrderDetail.getSku());
                inventoriesQuantity.put(reservationOrderDetail.getSku(), nowQuantity + reservationOrderDetail.getQuantity());
            } else {
                throw new RuntimeException(String.format("Order status does not support action=%s, reservationId=%s", "CANCEL", reservationId));
            }
        }
        return false;
    }
}
