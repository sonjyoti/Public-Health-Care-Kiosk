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
- [Privacy & Security](#privacy--security)
- [Contributing](#contributing)
- [License](#license)

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
- ✅ **Redis caching** — recommendation results cached with configurable TTL
- ✅ **Symptom-to-specialization mapping** — resolves citizen symptom input to the right specialist
- ✅ **Configurable scoring weights** — hospital admins can tune the recommendation formula via database without code changes
- ✅ **Recommendation audit log** — every recommendation made is persisted for analysis
- ✅ **Token slip generation** — printable QR-coded token for the patient
- ✅ **Leave block management** — staff can block doctor availability without modifying schedules
- ✅ **Appointment audit trail** — every status change is logged immutably
- ✅ **Digital public health forms** — blood donation, referral requests 
- ✅ **Multilingual support** — Assamese, Hindi, English 
- 📋 **Offline resilient** — local caching with background sync *(planned)*

---

## Architecture

The system follows a **microservices architecture** with each service owning its own database, exposed through a single API gateway, and registered with a Eureka discovery server.

```
Kiosk UI (React)
      │
      ▼
API Gateway :8080
      │
      ├──▶ Appointment Service    :8081  ──▶  appt_db  (PostgreSQL)
      ├──▶ Availability Service   :8082  ──▶  avail_db (PostgreSQL)
      ├──▶ Recommendation Service :8083  ──▶  rec_db   (PostgreSQL) + Redis
      └──▶ Forms Service          :8084  ──▶  forms_db (PostgreSQL)  [planned]

Eureka Discovery Server :8761
```

### Recommendation Scoring Model

Doctor recommendation uses a weighted composite score formula:

```
score = w1 × qualification   (default 0.25)
      + w2 × experience       (default 0.30)
      + w3 × specialization   (default 0.30)
      + w4 × (1 − load)       (default 0.15)
```

Weights are stored in the `scoring_weights` table and are fully configurable at runtime without redeployment. Doctor load is fetched live from the availability service via Feign client and cached in Redis with a 2-minute TTL. Full recommendation results are cached for 3 minutes per department + symptom combination.

### Service Communication

```
Recommendation Service
      │
      └──▶ Availability Service (via Feign + Eureka)
                │
                ├── GET /api/availability/doctors/department/{code}
                └── GET /api/availability/slots/doctor/{id}/load
```

---

## Microservices

| Service | Port | Status | Responsibility |
|---|---|---|---|
| `kiosk-discovery` | 8761 | ✅ Complete | Eureka service registry |
| `kiosk-gateway` | 8080 | ✅ Complete | API gateway, routing, CORS |
| `kiosk-appointment-service` | 8081 | ✅ Complete | OPD booking, token generation, audit trail |
| `kiosk-availability-service` | 8082 | ✅ Complete | Doctor management, schedules, time slots, leave blocks |
| `kiosk-recommendation-service` | 8083 | ✅ Complete | Weighted scoring engine, Redis caching, recommendation logs |
| `kiosk-forms-service` | 8084 | 📋 Planned | Public health form submissions |

---

## Tech Stack

| Layer | Technology                   |
|---|------------------------------|
| Backend | Spring Boot 3.2.5            |
| Architecture | Microservices (Spring Cloud) |
| Service discovery | Netflix Eureka               |
| API Gateway | Spring Cloud Gateway         |
| Inter-service communication | Spring Cloud OpenFeign       |
| ORM | Spring Data JPA + Hibernate  |
| Database | PostgreSQL 16                |
| Cache | Redis 7                      |
| Build tool | Maven (multi-module)         |
| Containerization | Docker + Docker Compose      |
| Java version | Java 17                      |
| IDE | IntelliJ IDEA                |

---

## Database Design

Each microservice owns an isolated database. No cross-service foreign key constraints exist — services communicate only through APIs.

### `appt_db` — Appointment Service

| Table | Purpose |
|---|---|
| `appointments` | OPD booking records, anonymous via session token |
| `departments` | Hospital department reference data |
| `appointment_audit` | Immutable log of every appointment status change |

### `avail_db` — Availability Service

| Table | Purpose |
|---|---|
| `doctors` | Doctor profiles with qualification and specialization |
| `schedules` | Weekly recurring schedule per doctor |
| `time_slots` | Individual bookable slots generated from schedules |
| `leave_blocks` | One-off unavailability windows that block time slots |

### `rec_db` — Recommendation Service

| Table | Purpose |
|---|---|
| `doctor_profiles` | Local read-optimised copy of doctor data synced from availability |
| `symptom_specialty_map` | Maps citizen symptom keywords to doctor specializations |
| `scoring_weights` | Configurable w1–w4 weights for the scoring formula |
| `recommendation_log` | Audit trail of every recommendation made |

### `forms_db` — Forms Service *(planned)*

| Table | Purpose |
|---|---|
| `form_definitions` | Dynamic form schemas with multilingual labels |
| `form_submissions` | Submitted form data stored as JSONB |
| `form_attachments` | File references for scanned documents |

### Redis — Recommendation Cache

| Key pattern | Type | TTL | Purpose |
|---|---|---|---|
| `recommend:{dept}:{symptom}` | JSON | 3 min | Cached top-3 ranked doctor list |
| `doctor:load:{doctorId}` | Integer | 2 min | Current booked queue size |
| `doctor:scores:{dept}` | JSON | 5 min | Pre-computed department scores |

---

## Project Structure

```
healthcare-kiosk/
├── pom.xml                               ← parent POM (dependency management)
├── docker-compose.yml
│
├── kiosk-discovery/                      ← Eureka server
│   └── src/main/
│       ├── java/.../DiscoveryApplication.java
│       └── resources/application.yml
│
├── kiosk-gateway/                        ← Spring Cloud Gateway
│   └── src/main/resources/application.yml
│
├── kiosk-appointment-service/
│   └── src/main/java/com/kiosk/appointment/
│       ├── model/                        ← Appointment, Department, AppointmentAudit, AppointmentStatus
│       ├── repository/                   ← AppointmentRepository, DepartmentRepository, AuditRepository
│       ├── dto/                          ← BookingRequest, AppointmentResponse, StatusUpdateRequest
│       ├── service/                      ← AppointmentService
│       ├── controller/                   ← AppointmentController, DepartmentController
│       └── exception/                    ← GlobalExceptionHandler, custom exceptions
│
├── kiosk-availability-service/
│   └── src/main/java/com/kiosk/availability/
│       ├── model/                        ← Doctor, Schedule, TimeSlot, LeaveBlock, SlotStatus
│       ├── repository/                   ← DoctorRepository, ScheduleRepository, TimeSlotRepository, LeaveBlockRepository
│       ├── dto/                          ← DoctorRequest/Response, ScheduleRequest/Response, TimeSlotResponse, LeaveBlockRequest
│       ├── service/                      ← DoctorService, ScheduleService, TimeSlotService
│       ├── controller/                   ← DoctorController, ScheduleController, TimeSlotController
│       └── exception/                    ← GlobalExceptionHandler, custom exceptions
│
├── kiosk-recommendation-service/
│   └── src/main/java/com/kiosk/recommendation/
│       ├── model/                        ← DoctorProfile, SymptomSpecialtyMap, ScoringWeight, RecommendationLog
│       ├── repository/                   ← DoctorProfileRepository, SymptomSpecialtyMapRepository, ScoringWeightRepository, RecommendationLogRepository
│       ├── dto/                          ← RecommendationRequest, RecommendationResponse, DoctorScore
│       ├── engine/                       ← ScoringEngine (weighted scoring logic)
│       ├── service/                      ← RecommendationService
│       ├── controller/                   ← RecommendationController
│       ├── client/                       ← AvailabilityClient (Feign), DoctorResponse
│       ├── config/                       ← RedisConfig
│       └── exception/                    ← GlobalExceptionHandler
│
└── kiosk-forms-service/                  ← planned
```

---

## Getting Started

### Prerequisites

- Java 17
- Maven 3.9+
- PostgreSQL 16
- Redis 7
- Docker

### Database setup

```sql
CREATE DATABASE appt_db;
CREATE DATABASE avail_db;
CREATE DATABASE rec_db;
CREATE DATABASE forms_db;
```

### Seed recommendation weights

```sql
INSERT INTO scoring_weights (id, weight_name, weight_value, description)
VALUES
  (gen_random_uuid(), 'W_QUALIFICATION',  0.25, 'Doctor qualification score weight'),
  (gen_random_uuid(), 'W_EXPERIENCE',     0.30, 'Years of experience weight'),
  (gen_random_uuid(), 'W_SPECIALIZATION', 0.30, 'Specialization match weight'),
  (gen_random_uuid(), 'W_LOAD',           0.15, 'Inverse load weight');
```

### Seed symptom mappings

```sql
INSERT INTO symptom_specialty_map (id, symptom_keyword, specialization, match_weight)
VALUES
  (gen_random_uuid(), 'chest pain',  'CARDIO',  1.0),
  (gen_random_uuid(), 'fever',       'GENERAL', 1.0),
  (gen_random_uuid(), 'fracture',    'ORTHO',   1.0),
  (gen_random_uuid(), 'skin rash',   'DERMA',   1.0),
  (gen_random_uuid(), 'eye problem', 'OPHTHA',  1.0);
```

### Running Redis

```bash
docker run -d -p 6379:6379 --name redis redis:7-alpine
```

### Running locally

Start services in this exact order:

```bash
# 1. Discovery server — wait for port 8761
cd kiosk-discovery && mvn spring-boot:run

# 2. API Gateway
cd kiosk-gateway && mvn spring-boot:run

# 3. Appointment service
cd kiosk-appointment-service && mvn spring-boot:run

# 4. Availability service
cd kiosk-availability-service && mvn spring-boot:run

# 5. Recommendation service
cd kiosk-recommendation-service && mvn spring-boot:run
```

Verify all services are registered at `http://localhost:8761`

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

**Book appointment — example:**
```json
POST /api/appointments/book
Content-Type: application/json

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
  "createdAt": "2026-04-29T09:00:00"
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
| `PATCH` | `/slots/{slotId}/status` | Update a slot status |
| `POST` | `/slots/block-leave` | Block doctor availability for a period |
| `GET` | `/slots/doctor/{doctorId}/load` | Get current booked slot count |

---

### Recommendation Service `/api/recommend`

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/` | Get ranked doctor recommendations |

**Request:**
```json
POST /api/recommend
Content-Type: application/json

{
  "departmentCode": "CARDIO",
  "symptomKeyword": "chest pain"
}
```

**Response:**
```json
{
  "departmentCode": "CARDIO",
  "symptomKeyword": "chest pain",
  "rankedDoctors": [
    {
      "doctorId": "uuid",
      "doctorName": "Dr. Ramesh Borah",
      "qualification": "MD",
      "specialization": "CARDIO",
      "departmentCode": "CARDIO",
      "score": 0.87
    }
  ],
  "fromCache": false
}
```

> `fromCache: true` — result served from Redis, no scoring computation performed.
> `fromCache: false` — scores freshly computed and result cached for subsequent requests.
> Top 3 doctors returned by default, ranked highest score first.

---

## Roadmap

### ✅ Completed
- [x] Multi-module Maven project setup
- [x] Eureka discovery server
- [x] Spring Cloud Gateway with routing and CORS
- [x] Appointment service — anonymous booking, token generation, status management, audit trail
- [x] Availability service — doctor management, schedule management, slot generation, leave blocking
- [x] Recommendation service — weighted scoring engine, Feign client, Redis caching, symptom mapping, recommendation logs

### 📋 Planned
- [ ] Forms service — dynamic form definitions and JSONB submissions
- [ ] Notification service — token slip printing, SMS alerts
- [ ] Admin panel — staff management interface with RBAC
- [ ] Docker Compose — full stack orchestration
- [ ] Kiosk UI — React touch-first frontend
- [ ] Multilingual support — Assamese, Hindi, English
- [ ] Offline mode — local cache with background sync
- [ ] Monitoring — Prometheus + Grafana dashboard
- [ ] Integration tests — Testcontainers + JUnit 5
- [ ] Security — Spring Security RBAC for admin endpoints

---

## Privacy & Security

- No personally identifiable information (PII) is stored
- All bookings are tied to a random `session_token` generated per kiosk session
- Session tokens are not linked to any patient identity
- Appointment data is purged after a configurable retention period
- Audit logs are immutable — status changes are append-only
- Recommendation logs store no citizen data — only doctor IDs, scores, and rankings

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
