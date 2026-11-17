package com.encoretickets.catalog.event;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Repository;

@Repository
public class EventRepository {

    private final Map<String, Event> events = new LinkedHashMap<>();

    public EventRepository() {
        add(new Event("evt-001", "The Midnight Echoes - World Tour", "Arena Sul",
                "Porto Alegre", LocalDate.of(2027, 3, 12), 15000, new BigDecimal("280.00")));
        add(new Event("evt-002", "Neon Harbor Festival", "Parque Central",
                "São Paulo", LocalDate.of(2027, 4, 24), 40000, new BigDecimal("450.00")));
        add(new Event("evt-003", "Aurora Strings Orchestra", "Teatro Municipal",
                "Rio de Janeiro", LocalDate.of(2027, 5, 8), 1800, new BigDecimal("190.00")));
    }

    private void add(Event event) {
        events.put(event.id(), event);
    }

    public List<Event> findAll() {
        return List.copyOf(events.values());
    }

    public Optional<Event> findById(String id) {
        return Optional.ofNullable(events.get(id));
    }
}