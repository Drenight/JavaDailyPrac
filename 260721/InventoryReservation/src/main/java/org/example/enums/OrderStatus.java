package org.example.enums;

public enum OrderStatus {
    ACTIVE("ACTIVE"),
    CANCELLED("CANCELLED"),
    CONFIRMED("CONFIRMED");

    private String value;
    OrderStatus(String value) {}

    public String getValue() {
        return this.value;
    }

//    public OrderStatus getValidNextStatus(String value) {}
}
