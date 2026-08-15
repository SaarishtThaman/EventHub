# EventHub

Event ticket booking platform — users browse and search events, book seats
for a venue, and pay for them; admins create and manage events.

> **Status: in development.** Architecture below reflects the current design;
> not all components are implemented yet.

## Architecture

```
Client (React)
   |
API Gateway  — JWT validation, routing, rate limiting
   |
   +-- Auth/User service
   +-- Event service (catalog, venues, seat maps, search indexing)
   +-- Booking service (seat hold, saga orchestration)
   +-- Payment service (mocked gateway + webhook)
   +-- Notification service (Kafka consumer)

Elasticsearch — event search (synced via outbox from the Event service)
Redis         — seat lock with TTL (temporary holds during checkout)
Kafka         — event backbone for booking outcomes and notifications
PostgreSQL    — shared by Auth/User, Event, and Booking services
```

### Seat locking

Seat holds use a Redis atomic lock with a TTL (`SET ... NX EX`), giving
instant, race-free rejection when two users attempt the same seat, with
holds expiring automatically if checkout is abandoned. Redis is a fast,
best-effort gate, not the correctness guarantee — the actual guarantee is a
conditional atomic update in Postgres at booking confirmation time
(`UPDATE ... WHERE status = 'AVAILABLE'`), so a lock failure degrades
performance, not correctness.

### Booking & payment

The booking record is only created once payment is confirmed via webhook —
there's no persisted "pending" state to reconcile if a user abandons
checkout, since the Redis hold expiring on its own is sufficient. Booking
and payment are coordinated with a TCC-style saga: a seat hold (Try), a
webhook-confirmed booking + seat update (Confirm), and an expired hold with
no durable side effects (Cancel).

### Search

Event search is served by Elasticsearch, kept in sync with Postgres via an
outbox pattern rather than a dual write.

### Database

A single shared PostgreSQL database backs Auth/User, Event, and Booking —
these are tightly coupled and transactionally related, so splitting them
into separate databases would only introduce distributed-transaction
complexity without a corresponding benefit. Payment is the one genuine
external boundary in the system.

## Tech stack

- **Backend:** Spring Boot (Java)
- **Frontend:** React
- **Datastores:** PostgreSQL, Redis, Elasticsearch
- **Messaging:** Kafka
- **Infra:** API Gateway, Docker Compose (local)

## Roadmap / not yet built

- Virtual waiting queue for high-demand events (Redis sorted set + SSE for
  queue position, batched admission)
- Distributed tracing / correlation IDs across services
