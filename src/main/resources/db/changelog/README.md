# Database Changelog (Liquibase)

Schema migrations for **appointment-api**.

## Master changelog

[`db-changelog-master.xml`](db-changelog-master.xml)

## Included changes

### Auth
- `2025081300012-create-user-table.xml` — users (roles: ADVISOR, TECHNICIAN, MANAGER, ADMIN)
- `2025081300004-create-login-session-table.xml`
- `2025081300011-create-refresh-token-table.xml`
- `2025091210000-create-password-history.xml`
- `2026071500000-insert-auth-users.sql` — seed staff users (dev)

### Appointment domain
- `2026071500001-create-customers-vehicles-tables.xml`
- `2026071500002-create-master-data-tables.xml` — technicians, service_bays, service_types, dealerships
- `2026071500003-create-appointments-table.xml`
- `2026071500004-insert-appointment-master-data.sql`
- `2026071500005-create-skills-tables.xml`
- `2026071500006-insert-skills-seed.sql`

## Sample users (dev context)

Password for all: `Admin@123`

| employeeId | Role |
|---|---|
| admin01 | ADMIN |
| mgr01 | MANAGER |
| adv01 | ADVISOR |
| tech01 | TECHNICIAN |
