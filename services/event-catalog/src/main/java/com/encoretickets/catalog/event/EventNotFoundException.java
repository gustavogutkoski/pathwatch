package com.encoretickets.catalog.event;

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