# NSS VIIT Pune — Event Polling & Blood Donation Platform

[![Java](https://img.shields.io/badge/Java-17-orange?logo=openjdk)](https://www.java.com/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2-brightgreen?logo=spring)](https://spring.io/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue?logo=postgresql)](https://www.postgresql.org/)
[![React](https://img.shields.io/badge/React-18-61DAFB?logo=react)](https://react.dev/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Live Demo](https://img.shields.io/badge/Live-Render.com-brightgreen)](https://nss-platform.onrender.com/)

> **Every Drop Counts.**  
> Maharashtra's student-powered blood donation awareness and event coordination platform.  
> Built by NSS VIIT Pune (2023–24) — engaging **3,500+ students** and increasing blood donation participation by **25%**.

---

## Architecture Highlights

- **Backend-first design** — layered architecture (Controller → Service → Repository) with Spring Boot 3.2
- **JWT auth via HttpOnly cookies** — CSRF-safe, XSS-resistant authentication with BCrypt(12) password hashing
- **Flyway migrations** — version-controlled DB schema evolution across DEV/PROD
- **Analytics via raw SQL** — aggregated dashboards using Spring Data JPA `@Query` with CTEs and window functions
- **Containerized** — single Docker image serving both SPA (via React build) and REST API on port 8080

---

## Impact

| Metric | Value |
|--------|-------|
| Users engaged | **3,500+** students across Maharashtra |
| Blood donation increase | **25%** via targeted outreach |
| Build-to-deploy time | **48 hours** (hackathon to production) |
| Uptime | **99.9%+** (Docker auto-restart + health checks) |

## Features

- 🩸 **Blood Requests** — Submit and discover urgent blood needs across Maharashtra, with OpenStreetMap location pins
- 🗳️ **Event Polls** — Vote on NSS events and drives
- 📊 **Live Analytics** — Real-time platform stats with SQL-aggregated dashboards
- 🛡️ **Admin Dashboard** — Create events, polls, manage requests, view analytics
- 🔒 **Secure Auth** — JWT in HttpOnly cookies, BCrypt(12) password hashing, role-based access

## Tech Stack

| Layer      | Tech                                    |
|------------|------------------------------------------|
| Backend    | Java 17 · Spring Boot 3.2 · Maven        |
| Frontend   | React 18 · Vite · Vanilla CSS            |
| Auth       | Spring Security · JWT (HttpOnly cookie)  |
| Database   | PostgreSQL (Railway managed)             |
| Migrations | Flyway V1–V4                             |
| Map        | Leaflet.js + OpenStreetMap (free, open)  |
| Deploy     | Docker · Railway                         |

---

## Local Development

### Prerequisites
- Java 17+, Maven 3.9+, Node.js 20+, PostgreSQL 15+

```bash
git clone <repo-url> && cd nss-platform

# Start local Postgres
docker run -d --name nssdb -e POSTGRES_DB=nssdb \
  -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 postgres:15-alpine

# Run backend (skip frontend build for speed)
mvn spring-boot:run "-Dskip.frontend=true" \
  -Dspring-boot.run.jvmArguments="-DDB_URL=jdbc:postgresql://localhost:5432/nssdb -DDB_USERNAME=postgres -DDB_PASSWORD=postgres"

# Run frontend (hot reload)
cd frontend && npm install && npm run dev   # http://localhost:5173
```

**Seed admin credentials:** `admin@nssviit.ac.in` / `Admin@12345`

---

## Railway Deployment

### 1. Create project on Railway
```bash
railway login
railway init          # or connect GitHub repo via dashboard
```

### 2. Add PostgreSQL service
- Dashboard → **New** → **Database** → **PostgreSQL**
- Railway auto-creates `PGHOST`, `PGPORT`, `PGDATABASE`, `PGUSER`, `PGPASSWORD`

### 3. Set environment variables (Dashboard → Variables)

| Variable                 | Value                                                       |
|--------------------------|-------------------------------------------------------------|
| `DB_URL`                 | `jdbc:postgresql://${{Postgres.PGHOST}}:${{Postgres.PGPORT}}/${{Postgres.PGDATABASE}}` |
| `DB_USERNAME`            | `${{Postgres.PGUSER}}`                                      |
| `DB_PASSWORD`            | `${{Postgres.PGPASSWORD}}`                                  |
| `JWT_SECRET`             | *(generate: `node -e "console.log(require('crypto').randomBytes(64).toString('hex'))"`)* |
| `SPRING_PROFILES_ACTIVE` | `prod`                                                      |
| `CORS_ORIGINS`           | `https://<your-app>.up.railway.app`                         |

> **PORT** is injected by Railway automatically — do not set it.

### 4. Deploy
```bash
railway up            # builds Dockerfile and deploys
```
Or push to GitHub and enable Railway's GitHub auto-deploy.

### Health check
`GET https://<your-app>.up.railway.app/actuator/health` → `{"status":"UP"}`

---

## API Reference

| Method | Endpoint                           | Auth     |
|--------|------------------------------------|----------|
| POST   | /api/auth/register                 | Public   |
| POST   | /api/auth/login                    | Public   |
| GET    | /api/events                        | Public   |
| GET    | /api/blood-requests                | Public   |
| GET    | /api/blood-requests/map            | Public   |
| GET    | /api/blood-requests/:id            | Public   |
| POST   | /api/blood-requests                | Auth     |
| POST   | /api/blood-requests/:id/interest   | Public   |
| PATCH  | /api/blood-requests/:id/fulfill    | Admin    |
| GET    | /api/admin/analytics/overview      | Admin    |
| POST   | /api/admin/events                  | Admin    |

---

## Project Structure

```
nss-platform/
├── src/main/java/com/nssplatform/
│   ├── auth/        # JWT, BCrypt, register/login
│   ├── blood/       # BloodRequest, DonorInterest
│   ├── events/      # Event CRUD
│   ├── polls/       # Poll + options
│   ├── votes/       # Vote submission & results
│   ├── analytics/   # SQL aggregated stats
│   ├── shared/      # Exceptions, global handler
│   └── config/      # Security, WebMVC, CORS
├── src/main/resources/db/migration/
│   ├── V1__initial_schema.sql
│   ├── V2__seed_data.sql
│   ├── V3__blood_requests.sql
│   └── V4__seed_blood_requests.sql   ← Maharashtra seed data
├── frontend/src/
│   ├── pages/       # Home, Events, BloodRequests, Admin, Auth
│   ├── components/  # Navbar, Footer, LiveCounter
│   ├── context/     # AuthContext
│   └── utils/       # constants (enum mirrors)
├── Dockerfile
└── railway.toml
```

---

*Developed by [Ritesh Godse](https://github.com/godseritesh) · NSS VIIT Pune (2023–24)*

---

<p align="center">
  <i>Built with ❤️ for the NSS community. Every drop counts.</i>
</p>
