# Setup

## Infra

```
docker compose up -d postgres
```

## Run a service

Open in IntelliJ, run the `*Application` main class. Each service has its
own `application.properties` with its port.

| Service | Port |
|---|---|
| auth-service | 8081 |
| event-service | 8082 |

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

Schemas: `auth`, `event`.
