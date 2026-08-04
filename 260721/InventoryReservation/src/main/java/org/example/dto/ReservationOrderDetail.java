package org.example.dto;

import lombok.Value;

@Value
public class ReservationOrderDetail {
//    public ReservationOrderDetail(String reservationId, String orderId, String sku, int quantity) {
//        ReservationId = reservationId;
//        UserId = orderId;
//        Sku =  sku;
//        Quantity = quantity;
//    }

    String ReservationId;
    String UserId;
    String Sku;
    int Quantity;
}
