# Database Changelog (Liquibase)

## Master

[`db-changelog-master.xml`](db-changelog-master.xml)

## Schema

- `2025081300012-create-user-table.xml`
- `2025081300004-create-login-session-table.xml`
- `2025081300011-create-refresh-token-table.xml`
- `2025091210000-create-password-history.xml`
- `2026071500001-create-customers-vehicles-tables.xml`
- `2026071500002-create-master-data-tables.xml`
- `2026071500003-create-appointments-table.xml`
- `2026071500005-create-skills-tables.xml`

## Seed

One Liquibase handler loads many SQL files:

- Handler: [`2026071500008-insert-seed-data.xml`](changes/2026071500008-insert-seed-data.xml)
- SQL:
  - [`seed/01-auth-users.sql`](changes/seed/01-auth-users.sql)
  - [`seed/02-master-data.sql`](changes/seed/02-master-data.sql)
  - [`seed/03-skills.sql`](changes/seed/03-skills.sql)
  - [`seed/04-demo-data.sql`](changes/seed/04-demo-data.sql)

Staff password: `Admin@123` (`admin01`, `mgr01`, `adv01`, `tech01`)
