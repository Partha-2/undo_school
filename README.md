# Undo School — Global Class Offering Booking System

A backend service for a global live-learning platform where teachers conduct online classes for students across different countries and timezones. Built with Java 17 + Spring Boot 3.4.1.

## Architecture

```
Frontend (Vercel)  ────►  Backend (Render)  ────►  PostgreSQL (Neon)
  React + Vite              Java 17 + Spring Boot      Cloud-hosted
```

## Live Demo

| Service | URL |
|---------|-----|
| **Frontend** | https://frontend-five-kappa-79.vercel.app |
| **Backend API** | https://undo-school-1.onrender.com |

## Features

### Teacher APIs
- Create courses and offerings (sections)
- Add sessions to existing offerings
- View all offerings with timezone-aware session times
- Sessions stored in UTC, displayed in any timezone

### Parent APIs
- Browse available offerings with timezone conversion
- Book entire offerings (all sessions at once)
- View booked offerings with local timezone display
- Conflict detection — overlapping sessions blocked

### Key Technical Decisions

| Decision | Implementation |
|----------|---------------|
| **Concurrency** | `PESSIMISTIC_WRITE` locks on session rows prevent double-booking |
| **Timezone handling** | Teacher provides local times → stored as UTC → viewed in parent's timezone |
| **Booking model** | Booking at offering level (1 parent-offering = 1 booking row) |
| **Conflict detection** | Half-open interval overlap check across ALL parent's bookings |
| **Error handling** | 400/404/409/500 with structured JSON responses |
| **Database** | PostgreSQL (production), H2 (local dev via profile) |

## Quick Start

### Prerequisites
- Java 17+, Maven

### Local Development

```bash
# Backend (H2 profile — no database setup needed)
cd backend
SPRING_PROFILES_ACTIVE=h2 mvn spring-boot:run
# → http://localhost:8080

# Frontend
cd frontend
npm install
npm run dev
# → http://localhost:5173
```

### Production Database (Neon PostgreSQL)

Set these env vars on Render:

| Variable | Value |
|----------|-------|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://<host>:5432/neondb?sslmode=require` |
| `SPRING_DATASOURCE_USERNAME` | Your Neon DB username |
| `SPRING_DATASOURCE_PASSWORD` | Your Neon DB password |

## API Reference

### Courses

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/courses` | List all courses |
| `POST` | `/courses` | Create a course |

### Teachers

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/teachers` | Create a teacher |
| `POST` | `/teachers/{id}/offerings` | Create an offering with sessions |
| `POST` | `/teachers/{tid}/offerings/{oid}/sessions` | Add sessions to an offering |
| `GET` | `/teachers/{id}/offerings?timezone=` | List teacher's offerings |

### Parents

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/parents` | Create a parent |
| `GET` | `/offerings?timezone=` | Browse available offerings |
| `POST` | `/parents/{id}/bookings` | Book an offering |
| `GET` | `/parents/{id}/bookings?timezone=` | View my bookings |

## Domain Model

```
Course 1───* Offering 1───* Session
                   1
                   |
                   1
               Booking *───1 Parent
```

- **Course**: A class topic (e.g. "Python Coding for Kids")
- **Offering**: A specific scheduled version of a course (e.g. "Saturday Batch")
- **Session**: An individual meeting time within an offering
- **Parent**: A student/parent who books offerings
- **Booking**: Links a parent to an offering (covers all sessions)

## Tech Stack

| Layer | Technology |
|-------|-----------|
| **Backend** | Java 17, Spring Boot 3.4.1, Spring Data JPA, Hibernate |
| **Frontend** | React 18, Vite, TailwindCSS |
| **Database** | PostgreSQL (production), H2 (development) |
| **Deployment** | Render (Docker), Vercel (static) |
