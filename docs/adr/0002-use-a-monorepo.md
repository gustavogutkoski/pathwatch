# 2. Use a monorepo

Date: 2026-11-17

## Status

Accepted

## Context

Pathwatch is a demo project: anyone who clones it should be able to
run the whole ticket-purchase journey and its monitoring with a single
command. The project contains three kinds of code: the demo services,
the local infrastructure, and the Terraform code that manages the
monitoring.

Splitting them into separate repositories would mirror how many
companies organize their code, but it would also force readers to
clone and coordinate several repositories just to see the project
working.

## Decision

We will keep the services, the local infrastructure and the Terraform
code in a single repository.

## Consequences

- One clone and one `make up` start everything.
- Changes that cross boundaries (for example, a new business metric in
  a service and the alert that uses it) land in a single commit.
- Each release represents the complete state of the project, which
  makes it easy to link articles to specific versions.
- At real-world scale, the journey monitoring module would likely live
  in its own versioned repository, consumed by many teams. Extracting
  it remains a possible future step and would be recorded in a new ADR.