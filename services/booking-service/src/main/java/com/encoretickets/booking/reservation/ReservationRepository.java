package com.encoretickets.booking.reservation;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.UnaryOperator;

import org.springframework.stereotype.Repository;

@Repository
public class ReservationRepository {

    private final ConcurrentMap<String, Reservation> reservations = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, String> seats = new ConcurrentHashMap<>();

    public Optional<Reservation> hold(String eventId, int seatNumber, Instant expiresAt) {
        String id = UUID.randomUUID().toString();
        Reservation reservation = new Reservation(id, eventId, seatNumber, ReservationStatus.HELD, expiresAt);

        String currentHolder = seats.putIfAbsent(reservation.seatKey(), id);
        if (currentHolder != null) {
            return Optional.empty();
        }

        reservations.put(id, reservation);
        return Optional.of(reservation);
    }

    public Optional<Reservation> findById(String id) {
        return Optional.ofNullable(reservations.get(id));
    }

    public Optional<Reservation> update(String id, UnaryOperator<Reservation> change) {
        return Optional.ofNullable(reservations.computeIfPresent(id, (key, current) -> change.apply(current)));
    }

    public void releaseSeat(Reservation reservation) {
        seats.remove(reservation.seatKey(), reservation.id());
    }

    public List<Reservation> findHeldExpiredAt(Instant now) {
        return reservations.values().stream()
                .filter(reservation -> reservation.status() == ReservationStatus.HELD)
                .filter(reservation -> reservation.isExpiredAt(now))
                .toList();
    }
}