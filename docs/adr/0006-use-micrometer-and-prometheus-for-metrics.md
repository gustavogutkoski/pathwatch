# 6. Use Micrometer and the Prometheus format for metrics

Date: 2026-11-17

## Status

Accepted

## Context

The services need to expose technical and business metrics. Two main
options were considered:

- **Micrometer with the Prometheus registry**, native to Spring Boot
  and exposed through Actuator with minimal configuration.
- **OpenTelemetry**, vendor-neutral and covering metrics, traces and
  logs, but requiring additional components (SDK or agent, and usually
  a collector).

## Decision

For the first versions, services expose metrics through Micrometer in
the Prometheus format, scraped directly by Prometheus. OpenTelemetry
will be introduced together with distributed tracing (see ADR 9).

## Consequences

- Metrics are available almost for free, and the local stack stays
  small.
- Instrumentation is coupled to the Prometheus format for now. Moving
  to OpenTelemetry later is a planned migration, not an accident.
- Default Spring metrics do not include latency histograms. Percentiles
  (p95, p99) require enabling histogram publication explicitly, which
  will be done when journey SLIs are defined.