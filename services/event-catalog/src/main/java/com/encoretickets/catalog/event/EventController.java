package com.encoretickets.catalog.event;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventRepository repository;

    public EventController(EventRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Event> list() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Event get(@PathVariable String id) {
        return repository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(id));
    }
}