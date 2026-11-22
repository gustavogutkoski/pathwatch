package com.encoretickets.booking.reservation;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.encoretickets.booking.catalog.CatalogEvent;
import com.encoretickets.booking.catalog.EventCatalogClient;
import com.encoretickets.booking.reservation.exception.EventNotFoundException;
import com.encoretickets.booking.reservation.exception.InvalidSeatException;
import com.encoretickets.booking.reservation.exception.ReservationExpiredException;
import com.encoretickets.booking.reservation.exception.ReservationNotFoundException;
import com.encoretickets.booking.reservation.exception.SeatUnavailableException;

@Service
public class ReservationService {

    private final ReservationRepository repository;
    private final EventCatalogClient catalogClient;
    private final Clock clock;
    private final Duration holdDuration;

    public ReservationService(ReservationRepository repository,
                            EventCatalogClient catalogClient,
                            Clock clock,
                            @Value("${booking.hold-duration}") Duration holdDuration) {
        this.repository = repository;
        this.catalogClient = catalogClient;
        this.clock = clock;
        this.holdDuration = holdDuration;
    }

    public Reservation hold(String eventId, int seatNumber) {
        CatalogEvent event = catalogClient.findEvent(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));

        if (seatNumber > event.totalSeats()) {
            throw new InvalidSeatException(eventId, seatNumber, event.totalSeats());
        }

        Instant expiresAt = clock.instant().plus(holdDuration);
        return repository.hold(eventId, seatNumber, expiresAt)
                .orElseThrow(() -> new SeatUnavailableException(eventId, seatNumber));
    }

    public Reservation get(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException(id));
    }

    public Reservation confirm(String id) {
        Instant now = clock.instant();

        Reservation result = repository.update(id, current -> {
            if (current.status() != ReservationStatus.HELD) {
                return current;
            }
            return current.isExpiredAt(now)
                    ? current.withStatus(ReservationStatus.EXPIRED)
                    : current.withStatus(ReservationStatus.CONFIRMED);
        }).orElseThrow(() -> new ReservationNotFoundException(id));

        if (result.status() == ReservationStatus.EXPIRED) {
            repository.releaseSeat(result);
            throw new ReservationExpiredException(id);
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
            }
        }
        return expiredCount;
    }
}