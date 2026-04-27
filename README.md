# 🏥 Public Health Care Kiosk System

A self-service kiosk system for rural public hospitals that enables anonymous OPD appointment booking, real-time doctor availability, intelligent doctor recommendations, and digital public health form submissions — designed specifically for low-literacy rural environments.

---

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
- [Microservices](#microservices)
- [Tech Stack](#tech-stack)
- [Database Design](#database-design)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [API Reference](#api-reference)
- [Roadmap](#roadmap)

---

## Overview

The Public Health Care Kiosk System addresses key challenges in rural healthcare delivery:

- Long waiting times and unorganized queuing
- No self-service booking mechanism for citizens
- High administrative burden on hospital staff
- Low digital literacy among patients

The system provides a touch-first, multilingual, icon-driven kiosk interface backed by a robust Spring Boot microservices architecture — deployable on a single hospital LAN with or without internet connectivity.

---

## Features

- ✅ **Anonymous OPD booking** — no patient identity stored, session-token based
- ✅ **Real-time doctor availability** — live slot status, queue position tracking
- ✅ **Doctor recommendation engine** — weighted scoring by qualification, experience, specialization, and current load
- ✅ **Digital public health forms** — blood donation, referral requests, and more
- ✅ **Token slip generation** — printable QR-coded token for the patient
- ✅ **Leave block management** — staff can block doctor availability without modifying schedules
- ✅ **Audit trail** — every appointment status change is logged immutably
- ✅ **Multilingual support** — Assamese, Hindi, English
- ✅ **Offline resilient** — local caching with background sync for rural connectivity

---

## Architecture

The system follows a **microservices architecture** with each service owning its own database, exposed through a single API gateway, and registered with a Eureka discovery server.

```
Kiosk UI (React)
      │
      ▼
API Gateway (port 8080)
      │
      ├──▶ Appointment Service  (port 8081) ──▶ appt_db (PostgreSQL)
      ├──▶ Availability Service (port 8082) ──▶ avail_db (PostgreSQL)
      ├──▶ Recommendation Service (port 8083) ──▶ Redis
      └──▶ Forms Service        (port 8084) ──▶ forms_db (PostgreSQL)

Eureka Discovery Server (port 8761)
```

### Scoring Model

Doctor recommendation uses a weighted composite score:

```
score = w1 × qualification  (0.25)
      + w2 × experience      (0.30)
      + w3 × specialization  (0.30)
      + w4 × (1 − load)      (0.15)
```

Weights are configurable via the `scoring_weights` table without code redeployment.

---

## Microservices

| Service | Port | Responsibility |
|---|---|---|
| `kiosk-discovery` | 8761 | Eureka service registry |
| `kiosk-gateway` | 8080 | API gateway, routing, CORS |
| `kiosk-appointment-service` | 8081 | OPD booking, token generation, audit |
| `kiosk-availability-service` | 8082 | Doctor management, schedules, time slots, leave blocks |
| `kiosk-recommendation-service` | 8083 | Weighted scoring engine, Redis caching *(in progress)* |
| `kiosk-forms-service` | 8084 | Public health form submissions *(planned)* |

---

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Spring Boot 3.2.5 |
| Architecture | Microservices (Spring Cloud) |
| Service discovery | Netflix Eureka |
| API Gateway | Spring Cloud Gateway |
| ORM | Spring Data JPA + Hibernate |
| Database | PostgreSQL 16 |
| Cache | Redis 7 |
| Build tool | Maven (multi-module) |
| Containerization | Docker + Docker Compose |
| Java version | Java 21 |
| IDE | IntelliJ IDEA |

---

## Database Design

Each microservice owns an isolated database. No cross-service foreign key constraints.

### `appt_db` — Appointment Service

| Table | Purpose |
|---|---|
| `appointments` | OPD booking records, anonymous via session token |
| `departments` | Hospital department reference data |
| `appointment_audit` | Immutable log of every status change |

### `avail_db` — Availability Service

| Table | Purpose |
|---|---|
| `doctors` | Doctor profiles with qualification and specialization |
| `schedules` | Weekly recurring schedule per doctor |
| `time_slots` | Individual bookable slots generated from schedules |
| `leave_blocks` | One-off unavailability windows that block slots |

### `forms_db` — Forms Service *(planned)*

| Table | Purpose |
|---|---|
| `form_definitions` | Dynamic form schemas with multilingual labels |
| `form_submissions` | Submitted form data as JSONB |
| `form_attachments` | File references for scanned documents |

### Redis — Recommendation Service

| Key pattern | Type | TTL |
|---|---|---|
| `doctor:scores:{dept}` | JSON | 5 min |
| `doctor:load:{doctorId}` | Integer | 2 min |
| `recommend:{dept}:{symptom}` | JSON | 3 min |

---

## Project Structure

```
healthcare-kiosk/
├── pom.xml                          ← parent POM
├── docker-compose.yml
│
├── kiosk-discovery/                 ← Eureka server
├── kiosk-gateway/                   ← Spring Cloud Gateway
│
├── kiosk-appointment-service/
│   └── src/main/java/com/kiosk/appointment/
│       ├── model/                   ← Appointment, Department, AppointmentAudit
│       ├── repository/
│       ├── dto/                     ← BookingRequest, AppointmentResponse
│       ├── service/                 ← AppointmentService
│       ├── controller/              ← AppointmentController, DepartmentController
│       └── exception/               ← GlobalExceptionHandler
│
├── kiosk-availability-service/
│   └── src/main/java/com/kiosk/availability/
│       ├── model/                   ← Doctor, Schedule, TimeSlot, LeaveBlock
│       ├── repository/
│       ├── dto/
│       ├── service/                 ← DoctorService, ScheduleService, TimeSlotService
│       ├── controller/              ← DoctorController, ScheduleController, TimeSlotController
│       └── exception/
│
├── kiosk-recommendation-service/    ← in progress
└── kiosk-forms-service/             ← planned
```

---

## Getting Started

### Prerequisites

- Java 21
- Maven 3.9+
- PostgreSQL 16
- Redis 7
- Docker (optional)

### Database setup

```sql
CREATE DATABASE appt_db;
CREATE DATABASE avail_db;
CREATE DATABASE forms_db;
```

### Running locally (without Docker)

Start services in this exact order:

```bash
# 1. Discovery server
cd kiosk-discovery
mvn spring-boot:run

# 2. API Gateway
cd kiosk-gateway
mvn spring-boot:run

# 3. Appointment service
cd kiosk-appointment-service
mvn spring-boot:run

# 4. Availability service
cd kiosk-availability-service
mvn spring-boot:run
```

Verify all services are registered at `http://localhost:8761`

### Running with Docker

```bash
mvn clean package -DskipTests
docker-compose up --build
```

---

## API Reference

All endpoints are accessed through the gateway on port `8080`.

### Appointment Service `/api/appointments`

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/book` | Book an OPD appointment |
| `GET` | `/{id}` | Get appointment by ID |
| `GET` | `/session/{token}` | Get appointment by session token |
| `GET` | `/department/{code}` | List appointments by department |
| `PATCH` | `/{id}/status` | Update appointment status |
| `DELETE` | `/{id}/cancel` | Cancel an appointment |
| `GET` | `/departments` | List all active departments |

**Book appointment — example request:**
```json
POST /api/appointments/book
{
  "departmentCode": "CARDIO",
  "doctorId": "uuid-here",
  "slotId": "uuid-here"
}
```

**Response:**
```json
{
  "id": "uuid",
  "tokenNumber": "CARDIO-001",
  "sessionToken": "random-uuid",
  "departmentCode": "CARDIO",
  "status": "BOOKED",
  "createdAt": "2026-04-24T09:00:00"
}
```

---

### Availability Service `/api/availability`

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/doctors` | Register a new doctor |
| `GET` | `/doctors` | List all active available doctors |
| `GET` | `/doctors/{id}` | Get doctor by ID |
| `GET` | `/doctors/department/{code}` | Get doctors by department |
| `PATCH` | `/doctors/{id}/toggle-availability` | Toggle doctor on/off duty |
| `DELETE` | `/doctors/{id}` | Deactivate a doctor |
| `POST` | `/schedules` | Create a weekly schedule for a doctor |
| `GET` | `/schedules/doctor/{doctorId}` | Get schedules for a doctor |
| `GET` | `/slots/doctor/{doctorId}/open` | Get open slots for a doctor |
| `GET` | `/slots/{slotId}/is-open` | Check if a slot is available |
| `PATCH` | `/slots/{slotId}/status` | Update slot status |
| `POST` | `/slots/block-leave` | Block doctor availability for a period |
| `GET` | `/slots/doctor/{doctorId}/load` | Get current booked slot count |

---

## Roadmap

### ✅ Completed
- [x] Multi-module Maven project setup
- [x] Eureka discovery server
- [x] Spring Cloud Gateway with routing
- [x] Appointment service — full CRUD, anonymous booking, audit trail
- [x] Availability service — doctor management, schedule management, slot generation, leave blocking

### 🔄 In Progress
- [ ] Recommendation service — weighted scoring engine
- [ ] Redis caching layer for scores

### 📋 Planned
- [ ] Forms service — dynamic form definitions and submissions
- [ ] Notification service — token slip printing, SMS alerts
- [ ] Admin panel — staff management interface
- [ ] Docker Compose — full stack orchestration
- [ ] Kiosk UI — React touch-first frontend
- [ ] Multilingual support — Assamese, Hindi, English
- [ ] Offline mode — local cache with background sync
- [ ] Monitoring — Prometheus + Grafana dashboard
- [ ] Integration tests — Testcontainers + JUnit 5

---

## Privacy & Security

- No personally identifiable information (PII) is stored
- All bookings are tied to a random `session_token` generated per kiosk session
- Tokens are not linked to any patient identity
- Appointment data is purged after a configurable retention period
- Audit logs are immutable

---

## Contributing

This project is under active development. To contribute:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/your-feature`)
3. Commit your changes
4. Push and open a Pull Request

---

## Contributor(s)

- Sonjyoti Rabha
- Project Type: Internship Project(WBL, NIELIT Kokrajhar EC)
- Domain: Distributed systems / Backend engineering
