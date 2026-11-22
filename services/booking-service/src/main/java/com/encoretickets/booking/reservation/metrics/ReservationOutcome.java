package com.encoretickets.booking.reservation.metrics;

public enum ReservationOutcome {

    HELD("held"),
    SEAT_UNAVAILABLE("seat_unavailable"),
    EVENT_NOT_FOUND("event_not_found"),
    INVALID_SEAT("invalid_seat"),
    CATALOG_UNAVAILABLE("catalog_unavailable");

    private final String tagValue;

    ReservationOutcome(String tagValue) {
        this.tagValue = tagValue;
    }

    public String tagValue() {
        return tagValue;
    }
}