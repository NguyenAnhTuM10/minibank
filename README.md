# Mini Bank

A core banking REST API built with Java 21 and Spring Boot: accounts,
deposits, withdrawals, transfers, and a simulated interbank network, with a
focus on getting money right (transactions, concurrency, idempotency and a
double-entry ledger).

> Work in progress. See [docs/PLAN.md](docs/PLAN.md) for the roadmap.

## Tech stack

Java 21 · Spring Boot 4 · Spring Security · Spring Data JPA · PostgreSQL 17 ·
Flyway · JUnit 5 · Testcontainers · Docker · GitHub Actions

## Run locally

Requirements: JDK 21 and Docker.

```bash
./mvnw spring-boot:run   # starts PostgreSQL from compose.yaml, then the app
./mvnw verify            # builds and runs all tests against a real PostgreSQL
```

Health check: <http://localhost:8080/actuator/health>

## Architecture

Modular monolith with layered feature modules; boundaries are enforced by
ArchUnit tests on every build. See [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md)
for diagrams, module rules and the design patterns used, each with the problem
it solves. Decisions are recorded in [docs/adr](docs/adr).
