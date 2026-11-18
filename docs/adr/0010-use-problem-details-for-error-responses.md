# 10. Use Problem Details for error responses

Date: 2026-11-18

## Status

Accepted

## Context

Each service needs to report errors to its clients: unknown resources, invalid
requests, business rule violations and unexpected failures. Without a shared
convention, every service would return errors in its own shape, and Spring's
default error body is neither consistent nor documented as a contract.

The options considered were:

- A custom error format defined by the project.
- Spring's default error response.
- **Problem Details** (RFC 9457), an IETF standard for HTTP API errors,
  natively supported by Spring through `ProblemDetail`.

## Decision

All services return error responses using Problem Details. Each service has a
global exception handler (`@RestControllerAdvice`) that translates domain
exceptions into Problem Details, and extends `ResponseEntityExceptionHandler`
so that framework errors use the same format.

## Consequences

- Clients get the same error structure from every service: `type`, `title`,
  `status`, `detail`, `instance` and context-specific properties.
- Controllers throw domain exceptions instead of building error responses,
  keeping error handling in one place per service.
- Unexpected errors return a generic message to clients, while full details
  are logged server-side only, so internal information does not leak.
- Expected client errors (such as an unknown event) are not logged as errors,
  keeping error logs meaningful.
- Problem types use the reserved `.example` domain, since Encore Tickets is
  fictional.