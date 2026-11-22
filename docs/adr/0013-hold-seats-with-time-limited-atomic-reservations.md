# 13. Hold seats with time-limited atomic reservations

Date: 2026-11-22

## Status

Accepted

## Context

Buying a ticket takes time: the customer picks a seat and then pays. During
that window, the seat must not be sold to someone else, but it also cannot be
blocked forever if the customer gives up.

At the same time, popular events attract many simultaneous requests for the
same seats. A naive "check if the seat is free, then reserve it" approach has
a race condition: two requests can both see the seat as free before either
reserves it, selling the same seat twice.

The options considered were:

- Selling seats directly, without a reservation step.
- Reserving seats with explicit locks around a check-then-act sequence.
- **Reserving seats with time-limited holds, using atomic operations.**

## Decision

A reservation holds a seat for a limited time, configured through
`booking.hold-duration` (two minutes by default). A reservation moves through
three states: `HELD`, `CONFIRMED` (after payment) and `EXPIRED` (when the hold
time runs out). A declined payment does not release the seat: the customer can
retry within the hold time.

All state changes use atomic operations of `ConcurrentHashMap`:

- `putIfAbsent` claims a seat only if it is free, in a single indivisible step.
- `computeIfPresent` changes a reservation's state atomically, so concurrent
  confirmation and expiration cannot both succeed.
- Seats are released with a conditional remove, which only frees the seat if it
  still belongs to the given reservation.

A scheduled task periodically expires holds whose time has run out and releases
their seats. Confirmation also checks the clock, so a hold that has already
passed its expiration time is never confirmed, even before the task runs.

## Consequences

- A seat can never be held by two reservations at the same time.
- Abandoned reservations free their seats automatically.
- The hold duration can be changed without code changes, for example to shorten
  it in chaos scenarios or tests.
- Expired holds become a business signal: a rising expiration rate may indicate
  customers abandoning the purchase or slow payments.
- The time is read through an injectable `Clock`, so expiration behavior can be
  tested without waiting.