# 11. Use latency histograms for percentiles

Date: 2026-11-18

## Status

Accepted

## Context

Average latency hides slow requests: a small number of very slow requests can
leave the average looking healthy while some users have a poor experience.
Percentiles (p95, p99) describe the experience of most users and the realistic
worst case, and they are the basis for latency SLOs.

As anticipated in ADR 6, Spring does not publish latency histograms by default.
Micrometer offers two ways to get percentiles:

- **Client-side percentiles**, computed by each service instance and published
  as ready-made values.
- **Histograms**, where each instance publishes request counts per latency
  bucket and the percentile is computed in Prometheus.

Percentiles cannot be aggregated: the p95 of a service is not the average of
the p95 of its instances. Histogram buckets, on the other hand, can be summed
across instances before computing the percentile.

## Decision

Services publish latency histograms, and percentiles are computed in
Prometheus with `histogram_quantile`, aggregating buckets by `le` first.
Dashboards and SLOs use p95 and p99 instead of averages.

## Consequences

- Percentiles stay correct when services run with multiple instances.
- Each histogram adds one time series per bucket, increasing the number of
  series stored in Prometheus.
- Percentiles are estimates interpolated within buckets, which is precise
  enough for dashboards and SLOs.
- Quantile queries use a 5-minute window to remain stable at low traffic.