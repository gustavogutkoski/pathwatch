# 15. Expose business metrics with a shared convention

Date: 2026-11-22

## Status

Accepted

## Context

HTTP metrics provided by Spring describe technical behavior: request counts,
status codes and latency. They answer "is the service healthy?", but not
"is the business working?". Journey monitoring (ADR 3) needs signals in
business terms: how many reservations were held, confirmed or expired, and why
attempts failed.

Without a convention, each service would name and label these metrics
differently, making journey-wide dashboards and alerts inconsistent.

## Decision

Each service exposes business metrics through Micrometer, following these rules:

- **Names** follow `<domain>.<entity>[.<event>]`, such as
  `booking.reservations` or `booking.reservations.confirmed`.
- **Counters** record business events. When an event can end in different
  ways, a single counter uses an `outcome` label instead of one metric per
  result.
- **Outcome values** make the failure type explicit: expected business
  outcomes (such as `seat_unavailable`) are distinct from technical failures
  (such as `catalog_unavailable`).
- **Gauges** report current state, such as the number of active seat holds.
- **Labels** have a small, bounded set of values. Identifiers such as event,
  reservation or seat IDs are never used as labels.

## Consequences

- Dashboards and SLIs can be built in business terms and combined across
  services into journey views.
- Availability SLIs count only technical-failure outcomes, so expected
  business outcomes do not burn the error budget.
- Business metrics cannot be broken down by event or reservation. Questions
  at that level are answered by logs, not metrics.
- Services must record outcomes explicitly in code, which adds a small amount
  of instrumentation to the business logic.