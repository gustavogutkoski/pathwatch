# 9. Instrumentation follows journey needs

Date: 2026-11-17

## Status

Accepted

## Context

A complete observability setup includes metrics, logs and traces. It
would be tempting to fully instrument every service before building any
monitoring. However, in real organizations, monitoring as code usually
sits on top of instrumentation that already exists, and the value of
Pathwatch is in the monitoring layer, not in the instrumentation itself.

Instrumenting everything up front would also delay the core of the
project and increase the risk of never reaching it.

## Decision

Each observability signal is added when a journey story needs it:

- **Metrics** are the foundation and grow with each service.
- **Logs** (Loki) are added once multiple services interact and
  terminal logs stop being practical.
- **Traces** (OpenTelemetry) are added with the asynchronous ticket
  issuance step, where following a request across a message queue is
  essential to understand a broken journey.

The journey monitoring module is built before tracing is introduced.

## Consequences

- The core value of the project is demonstrated early.
- Every new signal arrives with a concrete problem that justifies it,
  which also makes for better articles.
- Some services will be partially instrumented for a while.
- Tracing can be added later at low cost, since the OpenTelemetry Java
  agent provides automatic instrumentation with little or no code
  change.