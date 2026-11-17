# 4. Use Spring Boot for the demo services

Date: 2026-11-17

## Status

Accepted

## Context

The demo services exist only to be monitored: they are the target of
Pathwatch, not its subject. They should be quick to write, realistic
enough to produce meaningful signals, and should not steal the focus
from the monitoring work.

## Decision

We will build the demo services with Spring Boot and Java 21.

## Consequences

- Spring Boot Actuator and Micrometer expose HTTP, JVM and system
  metrics out of the box, with no custom instrumentation code.
- Spring Boot is widely used in production, so the signals and their
  quirks resemble what teams see in real systems.
- Images are heavier and startup is slower than with lighter stacks
  such as Go. JVM warm-up is visible in the dashboards as higher
  latency on the first requests after startup — which is a realistic
  behavior worth observing rather than a problem to hide.