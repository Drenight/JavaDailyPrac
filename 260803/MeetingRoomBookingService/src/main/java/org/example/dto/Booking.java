package org.example.dto;

import java.time.Instant;

public record Booking(
        String meetingId,
        String meetingRoomId,
        String userId,
        Instant start,
        Instant end) {
}
