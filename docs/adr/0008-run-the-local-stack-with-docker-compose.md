# 8. Run the local stack with Docker Compose

Date: 2026-11-17

## Status

Accepted

## Context

The services, Prometheus and Grafana need to run locally. A local
Kubernetes cluster (kind, minikube) would be closer to how these
systems run in production, but it adds setup steps, resource usage and
concepts unrelated to monitoring.

## Decision

We will run the local stack with Docker Compose, wrapped by a Makefile.

## Consequences

- One command starts everything, on any machine with Docker.
- Services reach each other by name on the Compose network, which
  keeps configuration simple.
- Kubernetes-specific signals and resources (pods, deployments,
  kube-state-metrics) are not demonstrated. Running Pathwatch on
  Kubernetes remains a possible future evolution.
- Choosing not to use Kubernetes is deliberate: it keeps the project
  focused on journey monitoring rather than cluster operations.