package com.encoretickets.booking.reservation.exception;

public class ReservationExpiredException extends RuntimeException {

    private final String reservationId;

    public ReservationExpiredException(String reservationId) {
        super("Reservation '" + reservationId + "' has expired");
        this.reservationId = reservationId;
    }

    public String reservationId() {
        return reservationId;
    }
}