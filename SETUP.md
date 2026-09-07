# Setup

## Everything at once

```
docker compose up --build
```

Builds and runs all five services plus Postgres and Redis.

| Service | Port |
|---|---|
| frontend | 5173 |
| auth-service | 8081 |
| event-service | 8082 |
| booking-service | 8083 |
| payment-service | 8084 |

## Infra only (running a service yourself in IntelliJ)

```
docker compose up -d postgres redis
```

Each service's `application.properties` targets `localhost` by default, so
this still works unchanged — the Docker Compose file only overrides
datasource/Redis/inter-service URLs to container hostnames when a service is
itself run via Compose.

## Seed admin account

Created automatically via Flyway (`auth-service` `V2__seed_admin_user.sql`).

```
email:    admin@eventhub.com
password: AdminPass123!
```

## DB access

```
docker exec -it eventhub-postgres psql -U eventhub -d eventhub
```

Schemas: `auth`, `event`, `booking`, `payment`.
