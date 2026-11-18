package com.encoretickets.booking.reservation;

public class SeatUnavailableException extends RuntimeException {

    private final String eventId;
    private final int seatNumber;

    public SeatUnavailableException(String eventId, int seatNumber) {
        super("Seat " + seatNumber + " of event '" + eventId + "' is not available");
        this.eventId = eventId;
        this.seatNumber = seatNumber;
    }

    public String eventId() {
        return eventId;
    }

    public int seatNumber() {
        return seatNumber;
    }
}