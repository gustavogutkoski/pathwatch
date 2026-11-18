package com.encoretickets.booking.reservation.exception;

public class ReservationNotFoundException extends RuntimeException {

    private final String reservationId;

    public ReservationNotFoundException(String reservationId) {
        super("Reservation '" + reservationId + "' was not found");
        this.reservationId = reservationId;
    }

    public String reservationId() {
        return reservationId;
    }
}