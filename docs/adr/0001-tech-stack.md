# ADR 0001: Tech stack

- Status: Accepted
- Date: 2026-11-13

## Context

The project targets fresher/junior backend roles in Vietnam's banking and
fintech sector in 2026. Job descriptions in that sector (for example MB Bank
and banking software vendors) repeatedly ask for Java, Spring Boot,
Spring Data JPA, Spring Security with JWT, SQL, REST APIs and Git, with
Docker, CI/CD and microservices as pluses.

## Decision

- Java 21 (LTS) and Spring Boot 4
- PostgreSQL 17, schema managed by Flyway migrations
- Tests with JUnit 5 and Testcontainers against a real PostgreSQL
- One main application plus one small simulator service for interbank
  transfers, instead of a full microservice architecture

## Consequences

- Matches the most common banking JD stack, so the project doubles as
  interview preparation.
- Testing against real PostgreSQL catches locking and transaction behaviour
  that an in-memory database would hide, but tests need Docker.
- Skipping full microservices keeps every part explainable; the simulator
  still demonstrates service integration, timeouts and retries.
