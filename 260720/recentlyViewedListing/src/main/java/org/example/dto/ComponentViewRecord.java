package org.example.dto;

import lombok.Data;

@Data
public class ComponentViewRecord {
    public ComponentViewRecord(String listingId) {
        this.listingId = listingId;
    }
    private final String listingId;
}
