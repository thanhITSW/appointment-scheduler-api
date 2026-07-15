# Appointment API — Frontend Integration Guide

Base URL (local): `http://localhost:8080`

- Content-Type: `application/json`
- Auth (private APIs): `Authorization: Bearer <accessToken>`
- Login also sets cookie `auth-session` (session id)
- OpenAPI UI: [http://localhost:8080/swagger](http://localhost:8080/swagger)

---

## Auth & roles

### Roles

| Role | Access |
|------|--------|
| `ADMIN` | Users + master data + appointments/customers |
| `MANAGER` | Master data (skills, technicians, bays, service types) + appointments/customers |
| `ADVISOR` | Appointments + customers |
| `TECHNICIAN` | Login only (no private management APIs yet) |

Login uses **employeeId** (not email).

### Seed accounts (dev)

Password for all: `Admin@123`

| employeeId | Role |
|------------|------|
| `admin01` | ADMIN |
| `mgr01` | MANAGER |
| `adv01` | ADVISOR |
| `tech01` | TECHNICIAN |

### Demo data (customers / appointments)

After Liquibase seed changeset `2026071500008-insert-seed-data`:

| Customer ID | Name | Phone | Vehicle examples |
|-------------|------|-------|------------------|
| 1 | An Nguyen | 0901000001 | Accent `51A-11111`, Tucson `51A-11112` |
| 2 | Binh Tran | 0901000002 | Cerato `51B-22221` |
| 3 | Chi Le | 0901000003 | Corolla `51C-33331` |
| 4 | Dung Pham | 0901000004 | Focus `51D-44441` |
| 5 | Em Hoang | 0901000005 | CX-5 `51E-55551` |

Appointments: past COMPLETED/CANCELLED + upcoming PENDING/CONFIRMED (`CURDATE()+1..+3`).  
Day `CURDATE()+5` left mostly free for live booking demos.

---

## Common enums

```text
AppointmentStatus: PENDING | CONFIRMED | COMPLETED | CANCELLED
TechnicianStatus:  AVAILABLE | OFF | BUSY
ServiceBayStatus:  AVAILABLE | OFF | BUSY
UserRole:          ADVISOR | TECHNICIAN | MANAGER | ADMIN
UserStatus:        INACTIVE | ACTIVATED | BLOCKED | DELETED
```

### Pagination (list pages)

Query: `page` (0-based), `size` (default 20), `sort` (e.g. `appointmentDate,desc`)

Response headers:
- `x-total-count` — total records
- `link` — `first` / `prev` / `next` / `last`

### Error body

```json
{
  "messageCode": "error.customer.not_found",
  "message": "..."
}
```

---

## 1. Auth (public)

### POST `/api/v1/public/auth/login`

```json
{
  "employeeId": "admin01",
  "password": "Admin@123"
}
```

**200**

```json
{
  "isAuthenticated": true,
  "jwtTokenDto": {
    "token": "<accessToken>",
    "refreshToken": "<refreshToken>",
    "expiredTime": "2026-07-15T10:00:00Z",
    "username": "admin01",
    "employeeId": "admin01",
    "userId": 1,
    "sessionId": "..."
  }
}
```

Also sets `Set-Cookie: auth-session=...`

### POST `/api/v1/public/auth/refresh`

```json
{ "refreshToken": "<refreshToken>" }
```

**200**

```json
{
  "accessToken": "...",
  "refreshToken": "...",
  "tokenType": "Bearer",
  "expiresIn": 18000000
}
```

### POST `/api/v1/public/auth/logout`

No body. Clears `auth-session` cookie. **200**

---

## 2. Booking flow (public)

Typical guest flow:

1. Search / create customer  
2. List / create vehicles  
3. Get dealerships + service types  
4. Check availability  
5. Create appointment  

### Customers

#### GET `/api/v1/public/customers?keyword=`

Search by name / phone / email.

**200** — `CustomerResponseDto[]`

```json
[
  {
    "id": 1,
    "firstName": "An",
    "lastName": "Nguyen",
    "phone": "0901234567",
    "email": "an@example.com"
  }
]
```

#### POST `/api/v1/public/customers`

```json
{
  "firstName": "An",
  "lastName": "Nguyen",
  "phone": "0901234567",
  "email": "an@example.com"
}
```

**201** — `CustomerResponseDto`

### Vehicles

#### GET `/api/v1/public/customers/{customerId}/vehicles`

**200** — `VehicleResponseDto[]`

```json
[
  {
    "id": 1,
    "customerId": 1,
    "vin": "VIN123",
    "licensePlate": "51A-12345",
    "make": "Hyundai",
    "model": "Accent",
    "year": 2022
  }
]
```

#### POST `/api/v1/public/customers/{customerId}/vehicles`

```json
{
  "vin": "VIN123",
  "licensePlate": "51A-12345",
  "make": "Hyundai",
  "model": "Accent",
  "year": 2022
}
```

**201** — `VehicleResponseDto`

### Master data

#### GET `/api/v1/public/service-types`

**200**

```json
[
  {
    "id": 1,
    "name": "Oil Change",
    "durationMinutes": 60,
    "requiredSkillIds": [1],
    "requiredSkillCodes": ["OIL"]
  }
]
```

#### GET `/api/v1/public/dealerships`

**200**

```json
[
  {
    "id": 1,
    "name": "Downtown Service Center",
    "address": "100 Main Street, Metro City"
  }
]
```

### Availability

#### POST `/api/v1/public/appointments/check-availability`

Unread lock — UI preview only. Real allocation happens on create.

```json
{
  "dealershipId": 1,
  "serviceTypeId": 1,
  "appointmentDate": "2026-07-20",
  "startTime": "09:00"
}
```

**200**

```json
{
  "available": true,
  "technicianName": "David",
  "serviceBayName": "Bay 2",
  "duration": 60,
  "endTime": "10:00"
}
```

### Create appointment

Frontend **does not** send `technicianId` / `serviceBayId`. Backend assigns under pessimistic lock.

#### POST `/api/v1/public/appointments`

```json
{
  "customerId": 1,
  "vehicleId": 1,
  "serviceTypeId": 1,
  "dealershipId": 1,
  "appointmentDate": "2026-07-20",
  "startTime": "09:00"
}
```

**201** — `AppointmentResponseDto`

```json
{
  "id": 10,
  "customerId": 1,
  "customerName": "An Nguyen",
  "vehicleId": 1,
  "vehicleLicensePlate": "51A-12345",
  "technicianId": 1,
  "technicianName": "David",
  "serviceBayId": 2,
  "serviceBayName": "Bay 2",
  "dealershipId": 1,
  "dealershipName": "Downtown Service Center",
  "serviceTypeId": 1,
  "serviceTypeName": "Oil Change",
  "appointmentDate": "2026-07-20",
  "startTime": "09:00:00",
  "endTime": "10:00:00",
  "status": "PENDING"
}
```

### Public appointment list / detail

#### GET `/api/v1/public/appointments`

Query: `date`, `customerId`, `status`, `page`, `size`, `sort`

**200** — `AppointmentResponseDto[]` + pagination headers

#### GET `/api/v1/public/appointments/{id}`

**200** — `AppointmentResponseDto`

---

## 3. Staff — Appointments (ADVISOR / MANAGER / ADMIN)

Base: `/api/v1/private/appointments`  
Header: `Authorization: Bearer <token>`

| Method | Path | Description |
|--------|------|-------------|
| GET | `/` | List (same filters as public) |
| GET | `/{id}` | Detail |
| PATCH | `/{id}/status` | Change status |
| POST | `/{id}/cancel` | Cancel (PENDING / CONFIRMED) |
| PUT | `/{id}/reschedule` | New date/time (re-locks tech/bay) |

### PATCH `/{id}/status`

```json
{ "status": "CONFIRMED" }
```

Allowed transitions:
- `PENDING` → `CONFIRMED` | `CANCELLED`
- `CONFIRMED` → `COMPLETED` | `CANCELLED`
- `COMPLETED` / `CANCELLED` = terminal

### PUT `/{id}/reschedule`

```json
{
  "appointmentDate": "2026-07-21",
  "startTime": "14:00"
}
```

---

## 4. Staff — Customers (ADVISOR / MANAGER / ADMIN)

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/v1/private/customers/{id}` | Detail |
| PUT | `/api/v1/private/customers/{id}` | Update |

```json
{
  "firstName": "An",
  "lastName": "Nguyen",
  "phone": "0901234567",
  "email": "an@example.com"
}
```

---

## 5. Staff — Master data (MANAGER / ADMIN)

### Skills — `/api/v1/private/skills`

| Method | Path | Body |
|--------|------|------|
| GET | `/` | — |
| POST | `/` | `{ "code": "OIL", "name": "Oil Change Skill" }` |

### Technicians — `/api/v1/private/technicians`

| Method | Path | Notes |
|--------|------|-------|
| GET | `/` | List |
| GET | `/{id}` | Detail |
| POST | `/` | Create |
| PUT | `/{id}` | Update name/code/status |
| PUT | `/{id}/skills` | Set skill ids |

**Create**

```json
{
  "name": "David",
  "employeeCode": "TECH-001",
  "status": "AVAILABLE",
  "skillIds": [1, 2]
}
```

**Update skills**

```json
{ "skillIds": [1, 2, 3] }
```

### Service bays — `/api/v1/private/service-bays`

| Method | Path |
|--------|------|
| GET | `/` |
| POST | `/` |
| PUT | `/{id}` |

```json
{ "name": "Bay 1", "status": "AVAILABLE" }
```

### Service types — `/api/v1/private/service-types`

| Method | Path |
|--------|------|
| GET | `/` |
| POST | `/` |
| PUT | `/{id}` |

```json
{
  "name": "Oil Change",
  "durationMinutes": 60,
  "requiredSkillIds": [1]
}
```

---

## 6. Users (ADMIN only)

Base: `/api/v1/private/users`

| Method | Path | Description |
|--------|------|-------------|
| GET | `/` | List + filters + pagination |
| POST | `/` | Create user |
| PUT | `/{id}/update` | Update employeeId / password |
| PATCH | `/{id}/delete` | Soft delete |
| PATCH | `/{id}/delete-confirm` | Delete with admin password |
| PATCH | `/change-password` | Change **current** user password |

### Create user

```json
{
  "employeeId": "adv02",
  "email": "adv02@appointment.com",
  "fullName": "Advisor Two",
  "password": "Admin@123",
  "role": "ADVISOR"
}
```

**201** empty body.

Password rules: 6–25 chars; needs upper, lower, digit, special; no spaces.

### List filters (examples)

```
GET /api/v1/private/users?role.equals=ADVISOR&status.equals=ACTIVATED&page=0&size=20
GET /api/v1/private/users?email.contains=appointment&sort=createdDate,desc
```

---

## Frontend integration tips

1. Store `jwtTokenDto.token` and send `Authorization: Bearer ...` on private routes.
2. Booking UI can stay on **public** customer/vehicle/appointment APIs (no JWT).
3. Staff console uses **private** APIs with role-based menus.
4. Always call **create appointment** after availability — availability is not reserved.
5. Concurrent bookings are safe on **create** / **reschedule** (pessimistic lock). Duplicate slot → conflict error codes:
   - `error.appointment.no_available_technician`
   - `error.appointment.no_available_service_bay`
6. Do not schedule past slots — `error.appointment.in_past`.

---

## Suggested page → API map

| Screen | APIs |
|--------|------|
| Login | `POST /public/auth/login` |
| Booking — customer | `GET/POST /public/customers` |
| Booking — vehicle | `GET/POST /public/customers/{id}/vehicles` |
| Booking — service/dealer | `GET /public/service-types`, `GET /public/dealerships` |
| Booking — slot | `POST /public/appointments/check-availability` |
| Booking — confirm | `POST /public/appointments` |
| Appointment list (guest) | `GET /public/appointments` |
| Staff appointment board | `GET/PATCH/POST/PUT /private/appointments...` |
| Staff customers | `GET/PUT /private/customers/{id}` |
| Admin users | `/private/users` |
| Manager catalog | `/private/skills`, `/technicians`, `/service-bays`, `/service-types` |
