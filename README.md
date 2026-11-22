# Pathwatch

> Business-journey monitoring as code with Terraform, Grafana and Prometheus.

🚧 **Work in progress**

## The problem

Many teams still create monitors, dashboards and alerts by clicking
through a UI. It works until it doesn't: no history, no review,
no consistency between services, and painful bulk changes.

Pathwatch explores a different approach: describe a **business journey**
once, and generate its monitoring (dashboards, alerts, SLOs) as code.

## The demo

A fictional online ticketing company, **Encore Tickets**, with a
ticket-purchase journey spanning four services:
event catalog → seat booking → payment → ticket issuance.

## Quick start

Requirements: Docker, Make and Terraform.

```bash
make up        # start the local stack
make tf-init   # first time only
make tf-apply  # create Grafana resources
make traffic   # generate sample traffic
```

Then open Grafana at http://localhost:3000 (admin/admin).
Run `make` to see all available commands.

## Local endpoints

| Component     | URL                    |
|---------------|------------------------|
| Grafana       | http://localhost:3000  |
| Prometheus    | http://localhost:9090  |
| event-catalog | http://localhost:8081  |
| booking-service | http://localhost:8082  |

## Design decisions

Key architectural decisions are recorded as ADRs in [`docs/adr`](docs/adr).

## Roadmap

- [x] Walking skeleton
- [ ] Booking and payment services, first business metrics
- [ ] Centralized logs with Loki
- [ ] Journey monitoring module
- [ ] Ticket issuance (async) and distributed tracing with OpenTelemetry
- [ ] Chaos scenarios
- [ ] SLOs as code

## Articles

- https://gustavoandrgutkoski.substack.com/p/de-clickops-para-observability-as