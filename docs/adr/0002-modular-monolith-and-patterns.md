# ADR 0002: Modular monolith with enforced boundaries

- Status: Accepted
- Date: 2026-11-14

## Context

The project should show clean code, sound architecture and appropriate use of
design patterns, and every part must be explainable by one fresher in an
interview. Money movements need ACID transactions across accounts and the
ledger.

Options considered:

1. Plain layered app (`controller`, `service`, `repository` packages at the
   top level). Familiar, but features blur together as the app grows.
2. Microservices per feature. Shows distributed systems, but splits one money
   movement across services, requiring sagas and eventual consistency that the
   project cannot test or defend well.
3. Modular monolith: package by feature, layers inside each feature,
   boundaries checked by tests.

## Decision

Option 3. One application and one database, modules `auth`, `account`,
`transfer`, `ledger`, `interbank`, `audit`, `common`, each layered as
`web → service → domain`, with `infra` adapters behind domain ports.

The rules are executable: an ArchUnit test fails the build when a layer or
module boundary is crossed, so the architecture cannot silently erode.
Formatting is enforced by Spotless, and coverage is reported by JaCoCo.

Design patterns are introduced only when a feature needs them; the mapping of
pattern to problem lives in `docs/ARCHITECTURE.md`.

## Consequences

- A transfer stays one local database transaction, which keeps correctness
  simple and testable.
- Modules could later be extracted into services along their existing
  boundaries; the interbank simulator already shows integration over HTTP.
- ArchUnit rules start with no classes to check; they are configured to allow
  empty matches until the modules exist, and become effective as code lands.
- Contributors (human or AI) get fast, objective feedback when they break a
  rule, instead of relying on review alone.
