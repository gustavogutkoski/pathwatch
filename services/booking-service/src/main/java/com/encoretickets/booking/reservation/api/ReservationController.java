package com.encoretickets.booking.reservation.api;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.encoretickets.booking.reservation.Reservation;
import com.encoretickets.booking.reservation.ReservationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService service;

    public ReservationController(ReservationService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Reservation> create(@Valid @RequestBody CreateReservationRequest request) {
        Reservation reservation = service.hold(request.eventId(), request.seatNumber());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(reservation.id())
                .toUri();
        return ResponseEntity.created(location).body(reservation);
    }

    @GetMapping("/{id}")
    public Reservation get(@PathVariable String id) {
        return service.get(id);
    }

    @PostMapping("/{id}/confirm")
    public Reservation confirm(@PathVariable String id) {
        return service.confirm(id);
    }
}