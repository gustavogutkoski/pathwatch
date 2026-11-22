package com.encoretickets.booking.reservation.exception;

public class EventNotFoundException extends RuntimeException {

    private final String eventId;

    public EventNotFoundException(String eventId) {
        super("Event '" + eventId + "' was not found");
        this.eventId = eventId;
    }

    public String eventId() {
        return eventId;
    }
}