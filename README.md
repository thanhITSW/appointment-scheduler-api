# Appointment Scheduler API

Backend for dealership appointment booking: availability checks, technician/bay allocation **per dealership**, JWT staff APIs.

**Challenge:** Scenario A — Unified Service Scheduler · **Service layer focus:** this backend (`mvn test`) · **Companion UI:** `appointment-scheduler-ui`

**System design:** [SYSTEM_DESIGN.md](SYSTEM_DESIGN.md) · **API:** [docs/API.md](docs/API.md) · **Database:** [docs/DATABASE.md](docs/DATABASE.md)

---

## Tech stack

| Layer | Choice |
|-------|--------|
| Runtime | Java 17 |
| Framework | Spring Boot 3.5 |
| Security | Spring Security + JWT |
| Persistence | Spring Data JPA, Liquibase |
| Database | MySQL 8 |
| API docs | springdoc OpenAPI (`/swagger`) |
| Build | Maven |

---

## Features

- Staff login (`employeeId` + password) and token refresh
- Public appointment booking and availability check (creates `CONFIRMED` appointments)
- Automatic technician (skill-matched) + service bay allocation **scoped to dealership**
- Pessimistic locking to prevent double booking
- Customer / vehicle registry
- Private APIs for appointments, master data, and user admin (role-based)
- Dev seed data (staff, dealerships, skills, demo appointments)

---

## Prerequisites

- JDK 17+
- Maven 3.9+
- Docker (optional, for MySQL via Compose)
- MySQL 8 if not using Docker

---

## Quick start

### 1. Environment

```bash
cp .env.example .env
# edit DB_* and JWT_SECRET
```

### 2. MySQL (Docker)

```bash
docker compose up -d
```

This starts MySQL on `localhost:3306` with database `appointment`.

### 3. Run API

```bash
mvn spring-boot:run
```

- API: http://localhost:8080  
- Swagger: http://localhost:8080/swagger  

### Seed staff (dev Liquibase context)

| employeeId | Role | Password |
|------------|------|----------|
| `admin01` | ADMIN | `Admin@123` |
| `mgr01` | MANAGER | `Admin@123` |
| `adv01` | ADVISOR | `Admin@123` |
| `tech01` | TECHNICIAN | `Admin@123` |

---

## Sample API

```http
POST /api/v1/public/auth/login
POST /api/v1/public/appointments/check-availability
POST /api/v1/public/appointments
GET  /api/v1/public/appointments/{id}
GET  /api/v1/public/service-types
GET  /api/v1/public/dealerships
GET  /api/v1/private/appointments   (Bearer token)
```

Full contract: [docs/API.md](docs/API.md)  
Postman: [postman/Appointment-API.postman_collection.json](postman/Appointment-API.postman_collection.json)

---

## Folder structure

```text
appointment-scheduler-api/
├── README.md
├── SYSTEM_DESIGN.md
├── docker-compose.yml
├── pom.xml
├── docs/
│   ├── API.md
│   └── DATABASE.md
├── postman/
│   └── Appointment-API.postman_collection.json
└── src/
    ├── main/java/com/appointment/   # application code
    ├── main/resources/              # application.yml, Liquibase
    └── test/java/com/appointment/   # unit + API tests
```

This repository is **backend service layer**. A React advisor UI lives in `appointment-scheduler-ui` and calls these REST APIs.

---

## Tests

```bash
mvn test
```

Coverage includes:

- `AppointmentServiceImplTest` — availability, booking rules, cancel
- `AuthServiceImplTest` — login / refresh
- `UserServiceImplTest` / `UserQueryServiceImplTest` — user admin
- `AppointmentPublicControllerTest` — booking/availability HTTP layer
- `UserManagementPrivateControllerTest` — private user APIs (MockMvc)

---

## Documentation map

| Doc | Content |
|-----|---------|
| [SYSTEM_DESIGN.md](SYSTEM_DESIGN.md) | Why the system is designed this way |
| [docs/API.md](docs/API.md) | FE integration guide |
| [docs/DATABASE.md](docs/DATABASE.md) | Schema & migrations |
| [src/main/resources/db/changelog/README.md](src/main/resources/db/changelog/README.md) | Changelog index |

---

## License

Internal / challenge project — adjust as needed for distribution.
