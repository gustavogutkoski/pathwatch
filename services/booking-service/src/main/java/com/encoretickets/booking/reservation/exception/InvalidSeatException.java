package com.encoretickets.booking.reservation.exception;

public class InvalidSeatException extends RuntimeException {

    private final String eventId;
    private final int seatNumber;
    private final int totalSeats;

    public InvalidSeatException(String eventId, int seatNumber, int totalSeats) {
        super("Seat " + seatNumber + " does not exist for event '" + eventId
                + "', which has " + totalSeats + " seats");
        this.eventId = eventId;
        this.seatNumber = seatNumber;
        this.totalSeats = totalSeats;
    }

    public String eventId() {
        return eventId;
    }

    public int seatNumber() {
        return seatNumber;
    }

    public int totalSeats() {
        return totalSeats;
    }
}