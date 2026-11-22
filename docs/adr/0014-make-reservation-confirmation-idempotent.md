# 14. Make reservation confirmation idempotent

Date: 2026-11-22

## Status

Accepted

## Context

`payment-service` confirms a reservation in `booking-service` after a payment
is approved. Calls between services can fail in ambiguous ways: the
confirmation may succeed in `booking-service` while the response is lost on the
way back, for example because of a timeout. From the caller's side, it is
impossible to tell whether the confirmation happened.

The natural reaction is to retry. If a second confirmation of the same
reservation returned an error, the payment service would treat a successful
confirmation as a failure.

## Decision

Confirming a reservation is idempotent: confirming an already confirmed
reservation returns a success response with the same reservation. Calling
`POST /reservations/{id}/confirm` once or several times has the same effect.

Confirming an expired reservation returns a 409 (Reservation expired), since
the reservation still exists but its state conflicts with the requested action.

## Consequences

- `payment-service` can safely retry confirmations after timeouts or network
  errors, without having to find out whether a previous attempt succeeded.
- The contract between both services is simpler: a successful response always
  means the reservation is confirmed.
- Repeated confirmations are not distinguishable from the first one in the
  response. If this ever matters, it can be measured separately.