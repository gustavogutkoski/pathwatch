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

## Roadmap

- [ ] Walking skeleton: one service, Prometheus, Grafana, first dashboard via Terraform
- [ ] Full ticket-purchase journey
- [ ] Journey monitoring module
- [ ] Chaos scenarios
- [ ] SLOs as code