package com.encoretickets.catalog.event;

import java.math.BigDecimal;
import java.time.LocalDate;

public record Event(
        String id,
        String name,
        String venue,
        String city,
        LocalDate date,
        int totalSeats,
        BigDecimal price
) {}