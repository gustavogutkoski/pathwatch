package com.encoretickets.booking.reservation;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ReservationService {

    private final ReservationRepository repository;
    private final Clock clock;
    private final Duration holdDuration;

    public ReservationService(ReservationRepository repository,
                              Clock clock,
                              @Value("${booking.hold-duration}") Duration holdDuration) {
        this.repository = repository;
        this.clock = clock;
        this.holdDuration = holdDuration;
    }

    public Reservation hold(String eventId, int seatNumber) {
        Instant expiresAt = clock.instant().plus(holdDuration);
        return repository.hold(eventId, seatNumber, expiresAt)
                .orElseThrow(() -> new SeatUnavailableException(eventId, seatNumber));
    }

    public Reservation get(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException(id));
    }
}