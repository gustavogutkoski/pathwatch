package com.encoretickets.booking.reservation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record CreateReservationRequest(
        @NotBlank String eventId,
        @Positive int seatNumber
) {}