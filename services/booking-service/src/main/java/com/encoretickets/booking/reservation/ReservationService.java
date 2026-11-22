package com.encoretickets.booking.reservation;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.encoretickets.booking.catalog.CatalogEvent;
import com.encoretickets.booking.catalog.CatalogUnavailableException;
import com.encoretickets.booking.catalog.EventCatalogClient;
import com.encoretickets.booking.reservation.exception.EventNotFoundException;
import com.encoretickets.booking.reservation.exception.InvalidSeatException;
import com.encoretickets.booking.reservation.exception.ReservationExpiredException;
import com.encoretickets.booking.reservation.exception.ReservationNotFoundException;
import com.encoretickets.booking.reservation.exception.SeatUnavailableException;
import com.encoretickets.booking.reservation.metrics.ReservationMetrics;
import com.encoretickets.booking.reservation.metrics.ReservationOutcome;

@Service
public class ReservationService {

    private final ReservationRepository repository;
    private final EventCatalogClient catalogClient;
    private final Clock clock;
    private final Duration holdDuration;
    private final ReservationMetrics metrics;

    public ReservationService(ReservationRepository repository,
                            EventCatalogClient catalogClient,
                            Clock clock,
                            @Value("${booking.hold-duration}") Duration holdDuration,
                            ReservationMetrics metrics) {
        this.repository = repository;
        this.catalogClient = catalogClient;
        this.clock = clock;
        this.holdDuration = holdDuration;
        this.metrics = metrics;
    }

    public Reservation hold(String eventId, int seatNumber) {
        CatalogEvent event;
        try {
            event = catalogClient.findEvent(eventId).orElse(null);
        } catch (CatalogUnavailableException e) {
            metrics.recordAttempt(ReservationOutcome.CATALOG_UNAVAILABLE);
            throw e;
        }

        if (event == null) {
            metrics.recordAttempt(ReservationOutcome.EVENT_NOT_FOUND);
            throw new EventNotFoundException(eventId);
        }

        if (seatNumber > event.totalSeats()) {
            metrics.recordAttempt(ReservationOutcome.INVALID_SEAT);
            throw new InvalidSeatException(eventId, seatNumber, event.totalSeats());
        }

        Instant expiresAt = clock.instant().plus(holdDuration);
        Optional<Reservation> held = repository.hold(eventId, seatNumber, expiresAt);

        if (held.isEmpty()) {
            metrics.recordAttempt(ReservationOutcome.SEAT_UNAVAILABLE);
            throw new SeatUnavailableException(eventId, seatNumber);
        }

        metrics.recordAttempt(ReservationOutcome.HELD);
        return held.get();
    }

    public Reservation get(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException(id));
    }

    public Reservation confirm(String id) {
        Instant now = clock.instant();
        AtomicReference<ReservationStatus> previousStatus = new AtomicReference<>();

        Reservation result = repository.update(id, current -> {
            previousStatus.set(current.status());
            if (current.status() != ReservationStatus.HELD) {
                return current;
            }
            return current.isExpiredAt(now)
                    ? current.withStatus(ReservationStatus.EXPIRED)
                    : current.withStatus(ReservationStatus.CONFIRMED);
        }).orElseThrow(() -> new ReservationNotFoundException(id));

        boolean changedNow = previousStatus.get() == ReservationStatus.HELD;

        if (result.status() == ReservationStatus.EXPIRED) {
            if (changedNow) {
                metrics.recordExpired();
            }
            repository.releaseSeat(result);
            throw new ReservationExpiredException(id);
        }

        if (changedNow) {
            metrics.recordConfirmed();
        }
        return result;
    }

    public int expireOverdueHolds() {
        Instant now = clock.instant();
        int expiredCount = 0;

        for (Reservation candidate : repository.findHeldExpiredAt(now)) {
            AtomicBoolean expiredNow = new AtomicBoolean(false);

            repository.update(candidate.id(), current -> {
                if (current.status() == ReservationStatus.HELD && current.isExpiredAt(now)) {
                    expiredNow.set(true);
                    return current.withStatus(ReservationStatus.EXPIRED);
                }
                return current;
            }).ifPresent(reservation -> {
                if (expiredNow.get()) {
                    repository.releaseSeat(reservation);
                }
            });

            if (expiredNow.get()) {
                expiredCount++;
                metrics.recordExpired();
            }
        }
        return expiredCount;
    }
}