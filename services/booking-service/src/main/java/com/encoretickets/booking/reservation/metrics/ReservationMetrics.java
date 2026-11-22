package com.encoretickets.booking.reservation.metrics;

import java.util.EnumMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.encoretickets.booking.reservation.ReservationRepository;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;

@Component
public class ReservationMetrics {

    private final Map<ReservationOutcome, Counter> attempts = new EnumMap<>(ReservationOutcome.class);
    private final Counter confirmed;
    private final Counter expired;

    public ReservationMetrics(MeterRegistry registry, ReservationRepository repository) {
        for (ReservationOutcome outcome : ReservationOutcome.values()) {
            attempts.put(outcome, Counter.builder("booking.reservations")
                    .description("Reservation attempts by outcome")
                    .tag("outcome", outcome.tagValue())
                    .register(registry));
        }

        confirmed = Counter.builder("booking.reservations.confirmed")
                .description("Reservations confirmed after payment")
                .register(registry);

        expired = Counter.builder("booking.reservations.expired")
                .description("Reservations expired before confirmation")
                .register(registry);

        Gauge.builder("booking.reservations.held", repository::countHeld)
                .description("Reservations currently holding a seat")
                .register(registry);
    }

    public void recordAttempt(ReservationOutcome outcome) {
        attempts.get(outcome).increment();
    }

    public void recordConfirmed() {
        confirmed.increment();
    }

    public void recordExpired() {
        expired.increment();
    }
}