# 3. Model monitoring around business journeys

Date: 2026-11-17

## Status

Accepted

## Context

Monitoring is usually organized around technical components: one set of
dashboards and alerts per service, host or database. This answers the
question "is this service healthy?", but not the question the business
actually cares about: "can customers complete what they came to do?"

A journey can be broken while every service looks healthy. For example,
if the ticket issuance step silently stops processing, the catalog,
booking and payment services all report normal error rates and latency,
while customers pay and never receive their tickets.

## Decision

The unit of monitoring in Pathwatch is the **business journey**, not the
service. A journey is described once (its steps, the services behind
each step, and its objectives) and its dashboards, alerts and SLOs are
generated from that description.

## Consequences

- Technical metrics alone are not enough: each step must also expose
  business metrics (reservations created and expired, payments by
  outcome, tickets issued).
- SLIs must distinguish technical failures from expected business
  outcomes. A sold-out seat or a declined card is not an incident.
- Journey-level indicators (conversion between steps, paid orders
  without a ticket) become first-class monitoring signals.
- The monitoring module takes a journey as its input, which makes adding
  a new journey a matter of configuration rather than new dashboards.