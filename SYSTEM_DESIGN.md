# System Design — Appointment Scheduler

## 1. Overview

### Purpose

Replace manual dealership appointment booking with a reliable API that checks real-time resource availability and prevents double-booking of technicians and service bays.

### Main Features

| Feature | Description |
|---------|-------------|
| Appointment Booking | Create appointments with customer, vehicle, service type, and dealership |
| Availability Checking | Preview whether a time slot can be fulfilled before booking |
| Resource Allocation | Automatically assign a skilled technician and a free service bay |
| Staff Management | JWT-secured APIs for advisors, managers, and admins |
| Status Lifecycle | PENDING → CONFIRMED → COMPLETED / CANCELLED; reschedule supported |

### Scope

This repository is the **backend API** (`appointment-api`). Clients (web/mobile) consume REST endpoints. Persistence is **MySQL** with schema/seed managed by **Liquibase**.

---

## 2. Functional Requirements

| ID | Requirement | How it is met |
|----|-------------|----------------|
| **FR-1** | Book appointment | `POST /api/v1/public/appointments` validates input, allocates resources, persists appointment as `PENDING` |
| **FR-2** | Check technician availability | Availability and booking paths filter `AVAILABLE` technicians, match required skills, and reject time overlaps |
| **FR-3** | Check service bay availability | Same date/time window must not overlap existing `PENDING`/`CONFIRMED` bay bookings |
| **FR-4** | Create confirmed / trackable appointment | Booking creates a durable appointment record; staff can confirm, complete, cancel, or reschedule via private APIs |
| **FR-5** | Customer & vehicle registry | Public create/list customers and vehicles for booking prerequisites |
| **FR-6** | Master data | Dealerships, service types, skills, technicians, bays — seed + staff CRUD |
| **FR-7** | Authentication & authorization | Login by `employeeId` + password; JWT + role-based access (`ADMIN`, `MANAGER`, `ADVISOR`, `TECHNICIAN`) |

---

## 3. Non-Functional Requirements

| Area | Target | Design choice |
|------|--------|---------------|
| **Consistency** | No double booking | `@Transactional` + `PESSIMISTIC_WRITE` locks on technician/bay rows during allocate + insert |
| **Performance** | Availability ideally &lt; 500ms | Indexed FK lookups, skill graphs loaded once per request, read-only transaction for check-availability |
| **Scalability** | Multiple dealerships | `dealerships` table; appointments reference dealership; further sharding is a future step |
| **Security** | Authenticated staff APIs | Spring Security + JWT; public booking endpoints remain open for kiosk/web booking UX |
| **Maintainability** | Clear layering | Controllers → Services → Repositories; Liquibase for schema; MapStruct for DTOs |
| **Auditability** | Who/when changed | `AbstractAuditingEntity` (`created_by`, `created_date`, …) |

---

## 4. Architecture

```text
┌─────────────┐     HTTPS/JSON      ┌──────────────────────┐
│  Browser /  │ ──────────────────► │  Spring Boot REST    │
│  Mobile FE  │ ◄────────────────── │  Controllers         │
└─────────────┘                     │         │            │
                                    │  Services            │
                                    │  (booking, auth…)    │
                                    │         │            │
                                    │  JPA Repositories    │
                                    └─────────┬────────────┘
                                              │
                                              ▼
                                    ┌──────────────────────┐
                                    │  MySQL 8             │
                                    │  Liquibase migrations│
                                    └──────────────────────┘
```

**Layering**

1. **API** — `controller/publics` (booking/auth), `controller/privates` (staff)
2. **Domain services** — booking orchestration, validation, concurrency
3. **Persistence** — Spring Data JPA + Liquibase changelogs
4. **Security** — JWT filter, role rules in `WebSecurityConfig`

OpenAPI UI: `/swagger`.

---

## 5. ER Diagram

```text
┌──────────┐       1:N      ┌──────────┐
│ Customer │───────────────►│ Vehicle  │
└────┬─────┘                └────┬─────┘
     │                           │
     │         ┌─────────────────┘
     │         │
     │    N:1  │  N:1
     ▼         ▼
┌──────────────────────────────────────────────┐
│                 Appointment                    │
│  date, start/end, status                       │
└───┬──────────┬──────────┬──────────┬─────────┘
    │          │          │          │
    │ N:1      │ N:1      │ N:1      │ N:1
    ▼          ▼          ▼          ▼
┌────────┐ ┌────────┐ ┌──────────┐ ┌─────────────┐
│Techni- │ │Service │ │Dealership│ │ServiceType  │
│cian    │ │Bay     │ └──────────┘ └──────┬──────┘
└───┬────┘ └────────┘                     │
    │ M:N                                 │ M:N
    ▼                                     ▼
┌────────┐                         ┌────────────┐
│ Skill  │◄──── technician_skills  │service_type│
└────────┘     service_type_skills └────────────┘
```

**Staff / auth (orthogonal)**

`user` → `login_session`, `password_history`

Skills link technicians to service types so allocation only picks technicians who can perform the requested service.

---

## 6. Sequence Diagrams

### 6.1 Check availability (read-only)

```text
Client                Appointment API           AppointmentService              DB
  │                         │                          │                        │
  │  POST check-availability│                          │                        │
  │────────────────────────►│                          │                        │
  │                         │  checkAvailability()     │                        │
  │                         │─────────────────────────►│  load service type     │
  │                         │                          │───────────────────────►│
  │                         │                          │  list AVAILABLE techs  │
  │                         │                          │───────────────────────►│
  │                         │                          │  overlap queries       │
  │                         │                          │───────────────────────►│
  │                         │  AvailabilityResponse    │                        │
  │                         │◄─────────────────────────│                        │
  │◄────────────────────────│                          │                        │
```

No row locks: safe for UI “preview” traffic.

### 6.2 Create appointment (write + lock)

```text
Client           API              AppointmentService              DB
  │               │                      │                         │
  │ POST book     │                      │                         │
  │──────────────►│  createAppointment() │                         │
  │               │─────────────────────►│  BEGIN TX               │
  │               │                      │  SELECT techs           │
  │               │                      │  FOR UPDATE ───────────►│  ◄── PESSIMISTIC_WRITE
  │               │                      │  SELECT bays            │
  │               │                      │  FOR UPDATE ───────────►│
  │               │                      │  pick first free +      │
  │               │                      │  skill-matched          │
  │               │                      │  INSERT appointment ───►│
  │               │                      │  COMMIT                 │
  │               │  201 + DTO           │                         │
  │◄──────────────│◄─────────────────────│                         │
```

Concurrent bookers serialize on the locked technician/bay rows; only one booking succeeds for the contested slot.

---

## 7. Database Design

Primary entities (see [docs/DATABASE.md](docs/DATABASE.md) for columns):

| Table | Role |
|-------|------|
| `customers` | End customers |
| `vehicles` | Customer vehicles (VIN, plate, make/model/year) |
| `appointments` | Booking core |
| `technicians` | Workforce + status |
| `technician_skills` | M:N technician ↔ skill |
| `service_bays` | Physical bays + status |
| `service_types` | Service catalog + duration |
| `service_type_skills` | Required skills per service |
| `skills` | Skill catalog |
| `dealerships` | Locations |
| `user` / sessions | Staff auth |

IDs are `BIGINT` auto-increment. Schema changes go through Liquibase only (`ddl-auto: none`).

---

## 8. Booking Flow

```text
Create Appointment
        │
        ▼
 Validate (past time? customer? vehicle ownership? entities exist?)
        │
        ▼
 Lock AVAILABLE technicians (PESSIMISTIC_WRITE)
        │
        ▼
 Find first technician with required skills + no time overlap
        │
        ▼
 Lock AVAILABLE service bays (PESSIMISTIC_WRITE)
        │
        ▼
 Find first bay with no time overlap
        │
        ▼
 Persist Appointment (status = PENDING)
        │
        ▼
 Return AppointmentResponseDto
```

`checkAvailability` follows the same selection rules **without** locks and **without** insert — useful for UX but not a reservation.

Initial status is **PENDING** (staff may set CONFIRMED). Challenge wording “confirmed appointment” is interpreted as a durable, non-overlapping booking record; confirmation is an explicit status step for advisors/managers.

---

## 9. Concurrency

### Problem

Two clients book the same technician (or bay) for overlapping times.

### Solution

1. Wrap create/reschedule in `@Transactional`
2. Load candidates with `@Lock(LockModeType.PESSIMISTIC_WRITE)` → SQL `SELECT … FOR UPDATE`
3. Re-check overlap against `PENDING`/`CONFIRMED` appointments
4. Insert only if still free; otherwise throw conflict (`ERR_NO_AVAILABLE_TECHNICIAN` / `ERR_NO_AVAILABLE_SERVICE_BAY`)

```text
User A & User B book same slot
            │
            ▼
     Each starts a transaction
            │
            ▼
   Second waiter blocks on FOR UPDATE
            │
            ▼
   First commits appointment
            │
            ▼
   Second resumes, sees overlap → conflict error
```

**Why not optimistic locking?** Double-booking must fail closed under high contention; pessimistic locks give a simpler correctness story for scarce resources (few technicians/bays).

**Why not SERIALIZABLE isolation for everything?** Locking only the resource rows keeps read paths cheaper while protecting the critical section.

---

## 10. Future Improvements

| Idea | Notes |
|------|-------|
| Notifications | Email/SMS on book/confirm/remind |
| Waiting list | Queue when no capacity; auto-offer cancellations |
| Recurring appointments | Fleet / corporate service plans |
| Calendar integration | ICS / Google Calendar for advisors |
| Per-dealership capacity | Constrain tech/bay pools by dealership |
| Soft hold / TTL reservation | Bridge gap between check-availability and book |
| Observability | Metrics on conflict rate and booking latency |
| Frontend app | React SPA consuming this API |

---

## References

- [README.md](README.md) — run instructions
- [docs/API.md](docs/API.md) — endpoint contract for FE
- [docs/DATABASE.md](docs/DATABASE.md) — tables & migrations
- [postman/](postman/) — sample requests
