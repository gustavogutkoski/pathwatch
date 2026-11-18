package com.encoretickets.booking.reservation;

import java.time.Instant;

public record Reservation(
        String id,
        String eventId,
        int seatNumber,
        ReservationStatus status,
        Instant expiresAt
) {

    public Reservation withStatus(ReservationStatus newStatus) {
        return new Reservation(id, eventId, seatNumber, newStatus, expiresAt);
    }

    public String seatKey() {
        return eventId + ":" + seatNumber;
    }
}