# 7. Manage Grafana resources with Terraform

Date: 2026-11-17

## Status

Accepted

## Context

There are several ways to manage Grafana dashboards and alerts as code:

- Grafana's own **file-based provisioning**, loaded at startup.
- **Jsonnet / Grafonnet** and similar libraries that generate dashboard
  JSON.
- Grafana's newer **Git Sync**, which brings a GitOps workflow into
  Grafana itself.
- **Terraform**, through the official Grafana provider.

## Decision

We will manage all Grafana resources (folders, data sources, dashboards
and, later, alerts) with Terraform.

## Consequences

- The same approach applies to other backends: the journey model could
  target Datadog or another vendor through its own Terraform provider.
- `terraform plan` shows exactly what will change before it happens,
  and drift (manual changes or deletions in the UI) is detected and
  reverted on the next apply.
- Terraform is the standard tool for platform teams, so the workflow
  matches what readers are likely to use at work.
- Dashboards written in HCL are verbose. This is mitigated by moving
  repetitive structures into a module.
- Terraform state must be managed. It is local for now, and would move
  to a remote backend in a shared environment.