package com.encoretickets.booking.reservation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ReservationExpirationJob {

    private static final Logger log = LoggerFactory.getLogger(ReservationExpirationJob.class);

    private final ReservationService service;

    public ReservationExpirationJob(ReservationService service) {
        this.service = service;
    }

    @Scheduled(fixedDelayString = "${booking.expiration-interval}")
    public void expireOverdueHolds() {
        int expired = service.expireOverdueHolds();
        if (expired > 0) {
            log.info("Expired {} overdue reservation hold(s)", expired);
        }
    }
}