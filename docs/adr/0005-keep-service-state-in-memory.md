# 5. Keep service state in memory

Date: 2026-11-17

## Status

Accepted

## Context

The demo services need state: the event catalog, seat reservations,
payments and issued tickets. A real system would persist this in a
database, but persistence is not what Pathwatch is about, and every
extra component adds setup time, failure modes and noise.

## Decision

Each service keeps its state in memory, using plain Java collections.
There is no database in the local stack.

## Consequences

- No schema, migrations or database container to maintain.
- State is lost whenever a service restarts. For a demo this is
  acceptable, and even convenient: every run starts from a clean state.
- Services that write state concurrently (such as seat booking) must
  use thread-safe structures, which is handled in code rather than
  delegated to a database.
- Database-related signals (connection pools, slow queries) are out of
  scope. If they become relevant to a journey story, this decision can
  be revisited.