# 1. Record architecture decisions

Date: 2026-11-17

## Status

Accepted

## Context

Pathwatch is built incrementally, and many decisions (tools, trade-offs,
scope cuts) will be made along the way. Without a record, the reasoning
behind them gets lost and is hard to explain later — to contributors,
to readers, or to my future self.

## Decision

We will use Architecture Decision Records (ADRs), as described by
Michael Nygard, stored in `docs/adr/` and numbered sequentially.

## Consequences

- Every significant decision gets a short, reviewable document.
- Superseded decisions are kept and marked as such, preserving history.
- The ADRs double as source material for technical articles.