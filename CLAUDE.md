# CLAUDE.md

Mini Bank is a portfolio project the owner will defend in fresher/junior
backend interviews (Java, banking domain, Vietnam market 2026). Every design
choice must be explainable by the owner, so clarity beats cleverness.

Talk to the owner in Vietnamese. Code, commits, docs and comments are in English.

## Ownership: who writes what

`docs/PLAN.md` tags each task `[YOU]` or `[CLAUDE]`.

- `[CLAUDE]` tasks: implement end to end (code, tests, docs).
- `[YOU]` tasks are the core money logic the owner must be able to defend
  (transfer service, locking, idempotency, ledger posting). For these, do NOT
  write the implementation. Instead: write the failing tests and the interface
  first, explain the concepts and trade-offs, then review the owner's code and
  point out bugs. Write the implementation only if the owner explicitly asks
  for it in chat for that specific task.

## Workflow for every request

1. Plan: break the request into small tasks with acceptance criteria. Ask only
   when a requirement is genuinely ambiguous.
2. Branch: `feat/<short-name>` or `fix/<short-name>` from `main`. Never commit
   directly to `main`.
3. Build in thin slices, test-first (red, green, refactor). Use subagents for
   independent parts.
4. Verify: `./mvnw verify` must pass before every commit. Never skip, disable
   or weaken a test to get green.
5. Review: run a separate review pass on the diff (correctness, security,
   money handling) before committing.
6. Commit with Conventional Commits (`feat:`, `fix:`, `test:`, `docs:`,
   `refactor:`, `chore:`), one logical change per commit.
7. Push and open a PR when a remote exists. The owner merges.
8. Record any significant design decision as an ADR in `docs/adr/`.

## Commands

- Build and all tests (needs Docker running): `./mvnw verify`
  (includes Spotless format check and ArchUnit architecture rules)
- Fix formatting: `./mvnw spotless:apply`
- Run the app (starts Postgres via compose.yaml): `./mvnw spring-boot:run`
- Run with a throwaway Testcontainers DB: `./mvnw spring-boot:test-run`

## Conventions

- Stack: Java 21, Spring Boot 4, PostgreSQL 17, Flyway, Testcontainers.
- Architecture: read `docs/ARCHITECTURE.md` before adding code. Modular
  monolith, package by feature `com.minibank.<module>`, layered inside as
  `web → service → domain`, external adapters in `infra` behind domain ports.
  `ArchitectureTest` enforces this; fix the code, never loosen a rule to pass.
- Design patterns only where they solve a problem listed in
  `docs/ARCHITECTURE.md`. Adding a new pattern means adding a row there
  explaining the problem it solves. No speculative abstractions.
- Rich domain model: state changes go through intention-revealing domain
  methods (`account.debit(amount)`) that guard invariants; no public setters
  for balance or status.
- Records for DTOs, commands and value objects. Constructor injection only.
- Money: `BigDecimal` in Java, `NUMERIC(19,4)` in SQL. Never `float`/`double`.
  Currency is VND.
- Schema changes only through new Flyway migrations
  (`src/main/resources/db/migration/V<n>__<name>.sql`). Never edit a migration
  that has been committed.
- Time: the JVM runs in UTC; store `TIMESTAMPTZ` and use `Instant` in Java.
- Any balance change happens inside one `@Transactional` service method.
- Errors return RFC 9457 problem details; never leak stack traces.
- No secrets in the repo. Local dev credentials live only in `compose.yaml`.
