package org.example.service;

import org.example.dto.Booking;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class MeetingRoomBookingService {
    private final HashMap<String, TreeMap<Instant, Booking>> bookingsMap; //roomId-> {startTime -> booking}
    private final HashMap<String, Booking> bookingIdMap;

    public MeetingRoomBookingService() {
        this.bookingsMap = new HashMap<>();
        this.bookingIdMap = new HashMap<>();
    }

    //register room
    public Boolean addRoom(String roomId) {
        if (this.bookingsMap.containsKey(roomId)) {
            return false;
        }
        TreeMap<Instant, Booking> bookings = new TreeMap<>();
        bookingsMap.put(roomId, bookings);
        return true;
    }

    //create booking; service.book(
    //    "booking-1",
    //    "room-a",
    //    "user-1",
    //    Instant.parse("2026-08-05T17:00:00Z"),
    //    Instant.parse("2026-08-05T18:00:00Z")
    //);
    public Boolean book(String bookingId, String roomId, String userId, Instant start, Instant end) {
        if (!bookingsMap.containsKey(roomId)) {
            // addRoom first
            return false;
        }
        if (start.isAfter(end) || start.equals(end)) {
            //Invalid time
            return false;
        }
        Booking newBooking = new Booking(bookingId, roomId, userId, start, end);
        if (bookingIdMap.containsKey(bookingId)) {
            return bookingIdMap.get(bookingId).equals(newBooking);
        } else {
            //Invalid test
            if (!isBookingTimeNonConflict(roomId, start, end)) {
                return false;
            }

            //Valid, update all ds
            bookingIdMap.put(bookingId, newBooking);
            TreeMap<Instant, Booking> tree = bookingsMap.get(roomId);
            tree.put(start, newBooking);
            return true;
        }
    }

    //get booking; service.getBookings("room-a");
    public List<Booking> getBookings(String roomId) {
        if (!bookingsMap.containsKey(roomId)) {
            return new ArrayList<>();
        }
        return bookingsMap.get(roomId).values().stream().toList();
    }

    // cancel; service.cancel("booking-1");
    public Boolean cancel(String bookingId) {
        if (!bookingIdMap.containsKey(bookingId)) return false;
        Booking booking = bookingIdMap.get(bookingId);
        bookingIdMap.remove(bookingId);

        TreeMap<Instant, Booking> tree = bookingsMap.get(booking.meetingRoomId());
        tree.remove(booking.start());
        return true;
    }

    private Boolean isBookingTimeNonConflict(String roomId, Instant start, Instant end) {
        TreeMap<Instant, Booking> tree = bookingsMap.get(roomId);
        Instant beforeStart = tree.floorKey(start);
        Instant nextStart = tree.ceilingKey(start);

        if (beforeStart.equals(start) || nextStart.equals(start)) {
            return false;
        }
        Booking beforeBooking = tree.get(beforeStart);
        Booking nextBooking = tree.get(nextStart);
        if (beforeBooking != null && beforeBooking.end().isAfter(start)) {
            return false;
        }
        if (nextBooking != null && nextBooking.start().isBefore(end)) {
            return false;
        }
        return true;
    }

}
