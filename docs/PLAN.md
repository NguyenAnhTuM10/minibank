# Mini Bank roadmap

Tags: `[CLAUDE]` Claude implements, `[YOU]` the owner implements with Claude
writing tests first and reviewing. Each task ends with `./mvnw verify` green.

## Phase 0: Project setup

- [x] [CLAUDE] Spring Boot skeleton, Flyway, Testcontainers, Docker Compose
- [x] [CLAUDE] CLAUDE.md, README, roadmap, first ADR
- [x] [CLAUDE] GitHub Actions CI running `./mvnw verify`
- [ ] [YOU] Create the GitHub repository and push `main`

## Phase 1: Core banking

- [ ] [CLAUDE] Users table, registration, login with Spring Security + JWT,
      BCrypt password hashing
- [ ] [CLAUDE] Global error handling (problem details) and request validation
- [ ] [CLAUDE] Accounts: open account, get balance, list own accounts;
      users can only see their own accounts
- [ ] [YOU] Deposit and withdraw inside one transaction, reject overdraft
- [ ] [YOU] Internal transfer between two accounts (atomic debit + credit)
- [ ] [CLAUDE] Transaction history with pagination
- [ ] [CLAUDE] OpenAPI / Swagger UI

## Phase 2: Getting money right (interview core)

- [ ] [CLAUDE] Concurrency test: 100 parallel transfers, total money conserved
- [ ] [YOU] Make that test pass: row locking (`SELECT ... FOR UPDATE`),
      consistent lock ordering to avoid deadlock
- [ ] [YOU] Idempotency key on transfer (double click returns the same result)
- [ ] [YOU] Double-entry ledger: every transfer writes balanced entries;
      balance can be rebuilt from the ledger
- [ ] [CLAUDE] Audit log of sensitive actions

## Phase 3: Interbank and QR

- [ ] [CLAUDE] `napas-simulator`: separate mock service with random delay,
      timeout and failure
- [ ] [YOU] Interbank transfer: PENDING state, async call, timeout and retry,
      final SUCCESS / FAILED with refund
- [ ] [CLAUDE] Reconciliation job comparing local records with the simulator
- [ ] [CLAUDE] VietQR-style payment QR: generate and pay by QR

## Phase 4: Ship it

- [ ] [CLAUDE] Dockerfile and full `docker compose up` stack
- [ ] [CLAUDE] Deploy a public demo
- [ ] [CLAUDE] README: architecture diagram, demo link, how to run
- [ ] [YOU] Read every ADR and rehearse explaining each decision
