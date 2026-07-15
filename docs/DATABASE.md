# Database Design

## Overview

- **RDBMS:** MySQL 8
- **Migrations:** Liquibase (`src/main/resources/db/changelog/`)
- **ORM:** Spring Data JPA / Hibernate (`ddl-auto: none`)
- **IDs:** `BIGINT` IDENTITY

Master changelog: `db/changelog/db-changelog-master.xml`

---

## Entity-relationship (logical)

```text
customers 1───* vehicles
customers 1───* appointments *───1 vehicles
appointments *───1 technicians
appointments *───1 service_bays
appointments *───1 dealerships
appointments *───1 service_types
technicians *───* skills          (technician_skills)
service_types *───* skills        (service_type_skills)
```

Staff auth tables (`user`, `login_session`, `refresh_token` / password history) are separate from the booking domain.

---

## Tables

### customers

| Column | Type | Notes |
|--------|------|-------|
| id | BIGINT PK | |
| first_name, last_name | VARCHAR | |
| phone | VARCHAR | |
| email | VARCHAR | nullable |
| auditing columns | | created_by, created_date, … |

### vehicles

| Column | Type | Notes |
|--------|------|-------|
| id | BIGINT PK | |
| customer_id | FK → customers | |
| vin | VARCHAR | |
| license_plate | VARCHAR | |
| make, model | VARCHAR | |
| year | INT | |

### appointments

| Column | Type | Notes |
|--------|------|-------|
| id | BIGINT PK | |
| customer_id | FK | |
| vehicle_id | FK | |
| technician_id | FK | allocated at booking |
| service_bay_id | FK | allocated at booking |
| dealership_id | FK | |
| service_type_id | FK | |
| appointment_date | DATE | |
| start_time, end_time | TIME | end = start + service duration |
| status | VARCHAR/ENUM | PENDING, CONFIRMED, COMPLETED, CANCELLED |

Overlap rules treat `PENDING` and `CONFIRMED` as blocking.

### technicians

| Column | Notes |
|--------|-------|
| id, name, employee_code | |
| status | AVAILABLE, OFF, BUSY |

### service_bays

| Column | Notes |
|--------|-------|
| id, name | |
| status | AVAILABLE, OFF, BUSY |

### service_types

| Column | Notes |
|--------|-------|
| id, name | |
| duration_minutes | used to compute `end_time` |

### skills / join tables

- `skills` — `code`, `name`
- `technician_skills` — `(technician_id, skill_id)`
- `service_type_skills` — `(service_type_id, skill_id)`

### dealerships

| Column | Notes |
|--------|-------|
| id, name, address | multi-site support |

### Auth

- `user` — staff login (`employee_id`, password hash, `role`, status flags)
- `login_session`, password history (and related token tables per changelog)

---

## Liquibase layout

### Schema (XML)

| File | Purpose |
|------|---------|
| `2025081300012-create-user-table.xml` | Users |
| `2025081300004-create-login-session-table.xml` | Sessions |
| `2025081300011-create-refresh-token-table.xml` | Tokens |
| `2025091210000-create-password-history.xml` | Password history |
| `2026071500001-create-customers-vehicles-tables.xml` | Customers / vehicles |
| `2026071500002-create-master-data-tables.xml` | Techs, bays, types, dealerships |
| `2026071500003-create-appointments-table.xml` | Appointments |
| `2026071500005-create-skills-tables.xml` | Skills + M2M |

### Seed (dev context)

Handler: `2026071500008-insert-seed-data.xml` loads:

1. `seed/01-auth-users.sql`
2. `seed/02-master-data.sql`
3. `seed/03-skills.sql`
4. `seed/04-demo-data.sql`

Seed password for staff: `Admin@123` (`admin01`, `mgr01`, `adv01`, `tech01`).

---

## Concurrency & integrity tips

- Prefer application-level pessimistic locks during booking (see [SYSTEM_DESIGN.md](../SYSTEM_DESIGN.md#9-concurrency)).
- Optional hardening: unique / exclusion constraints on (resource, date, time) are harder on MySQL for ranges; current design uses transactional overlap checks.
- Always migrate via Liquibase; do not rely on Hibernate `ddl-auto` for production schema.
