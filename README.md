# AI Battle — Authentication Service

## Prerequisites

| Tool | Version | Notes |
|------|---------|-------|
| Docker | ≥ 24.x | Required to run the full stack |
| make | any | Convenience wrapper around `docker compose`. Can be skipped by running the raw commands below |

---

## Tech Stack (given — do **not** change)

| Layer | Technology |
|-------|-----------|
| Language | Java 21 |
| Framework | Spring Boot 4.0.3 |
| Security | Spring Security 6 (stateless JWT for access token, stateful for refresh token) |
| ORM | Spring Data JPA + Hibernate |
| Database | PostgreSQL 17 |
| API Docs | SpringDoc OpenAPI + Scalar UI (`/scalar`) |
| Build | Maven 3.9 |
| Containerisation | Docker + Docker Compose v2 |

---

## Battle Overview

> You play the role of a **prompt / instruction engineer**.
> Your task is to write instruction files that guide an AI coding agent to implement the missing business logic so that **all test cases pass**.

Place your instruction files inside the `./agents/` folder (create it if it does not exist).
The AI agent will read those instructions and implement the code; you are **not** expected to write Java yourself.

---

## Project Structure

```
.
├── agents/                  ← 📝 YOUR INSTRUCTION FILES GO HERE (or it can be .github/instructions/...)
├── src/
│   ├── main/java/uit/is216/ai/battle/demo/
│   │   ├── configs/
│   │   │   └── SecurityConfig.java       # Spring Security filter chain (modify if needed)
│   │   ├── controllers/
│   │   │   ├── AuthController.java       # ⚠️ endpoints are NOT_IMPLEMENTED — must be completed
│   │   │   └── UserController.java       # ⚠️ endpoint is NOT_IMPLEMENTED — must be completed
│   │   ├── dtos/
│   │   │   ├── LoginRequest.java         # record { email, password }
│   │   │   ├── LoginResponse.java        # record { accessToken, refreshToken }
│   │   │   ├── SignupRequest.java        # record { fullName, email, password, confirmPassword }
│   │   │   └── UserProfileResponse.java  # record { id, fullName, email, createdAt, updatedAt }
│   │   ├── entities/
│   │   │   └── User.java                 # JPA entity → table "users"
│   │   └── services/                     # ← implement service classes here
│   │   └── repositories/                 # ← implement repository interfaces here
│   └── test/                             # test cases live here
├── docker-compose.yaml
├── Dockerfile
├── Makefile
├── pom.xml
└── .env                                  # environment variables (edit ports / credentials here)
```

---

## Endpoints to Implement

All endpoints below currently return `501 NOT_IMPLEMENTED`. You must implement the full logic.

### 1. `POST /public/auth/signup`

**Access**: Public (no authentication required)

**Request Body** (`SignupRequest`):
```json
{
  "fullName": "Nguyen Van A",
  "email": "a@example.com",
  "password": "secret123",
  "confirmPassword": "secret123"
}
```

**Expected behaviour**:
- Validate that `password` equals `confirmPassword`; return `400 Bad Request` if they do not match.
- Validate that the `email` is not already registered; return `409 Conflict` if it exists.
- Hash the password with **BCrypt** (`BCryptPasswordEncoder` is already configured).
- Persist the new `User` entity into the `users` table.
- Return **`201 Created`** with an empty body on success.

---

### 2. `POST /public/auth/login`

**Access**: Public (no authentication required)

**Request Body** (`LoginRequest`):
```json
{
  "email": "a@example.com",
  "password": "secret123"
}
```

**Response Body** (`LoginResponse`):
```json
{
  "accessToken": "<jwt>",
  "refreshToken": "<opaque-or-jwt>"
}
```

**Expected behaviour**:
- Look up the user by `email`; return `401 Unauthorized` if not found.
- Verify the provided password against the stored BCrypt hash; return `401 Unauthorized` if it does not match.
- Issue a **stateless JWT access token** (short-lived, e.g. 15 minutes) signed with a secret key.
- Issue a **stateful refresh token** (long-lived, e.g. 7 days) — store it server-side (recommended: Redis, but a `refresh_tokens` database table is also acceptable).
- Return **`200 OK`** with both tokens.

---

### 3. `GET /user/profile`

**Access**: **Authenticated** (requires a valid Bearer access token)

**Response Body** (`UserProfileResponse`):
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "fullName": "Nguyen Van A",
  "email": "a@example.com",
  "createdAt": "2025-01-01T00:00:00",
  "updatedAt": "2025-01-01T00:00:00"
}
```

**Expected behaviour**:
- Extract the authenticated user's identity from the JWT (via Spring Security `SecurityContextHolder`).
- Fetch the `User` entity from the database.
- Return **`200 OK`** with the profile data.
- Return **`401 Unauthorized`** if the token is missing or invalid.

---

## Security Requirements

| Requirement | Detail |
|-------------|--------|
| Access token | Stateless JWT — verified on every request via a custom `OncePerRequestFilter` |
| Refresh token | Stateful — stored server-side; must be invalidated on logout or rotation |
| Password storage | BCrypt (encoder bean already provided in `SecurityConfig`) |
| Public routes | `/public/**`, `/v3/api-docs/**`, `/scalar`, `/scalar/**` — already configured in `SecurityConfig` |
| Protected routes | Everything else requires a valid Bearer token |

> **Hint**: Replace the existing `httpBasic` authentication in `SecurityConfig` with a JWT filter chain.

---

## User Entity Reference

Table: `users`

| Column | Type | Constraints |
|--------|------|-------------|
| `id` | UUID | PK, auto-generated |
| `full_name` | VARCHAR(150) | NOT NULL |
| `email` | VARCHAR(150) | NOT NULL |
| `password` | VARCHAR(255) | NOT NULL (BCrypt hash) |
| `created_at` | TIMESTAMP | NOT NULL, auto-set on insert |
| `updated_at` | TIMESTAMP | NOT NULL, auto-updated |

---

## Running the Application

### With Docker (recommended)

```bash
# Start PostgreSQL + backend (builds the image automatically)
make up

# View logs
make logs

# Stop everything
make down
```

Or without `make`:
```bash
docker compose --env-file .env up -d --build
```

### Environment Variables (`.env`)

| Variable | Default | Description |
|----------|---------|-------------|
| `POSTGRES_DB` | `ai_battle_db` | Database name |
| `POSTGRES_USER` | `ai_battle_user` | DB username |
| `POSTGRES_PASSWORD` | `ai_battle_password` | DB password |
| `DB_HOST` | `postgres` | DB host (use `postgres` inside Docker, `localhost` locally) |
| `DB_PORT` | `5435` | Host-side port mapped to container's 5432 |
| `HOST_POSTGRES_PORT` | `5435` | Port exposed on your machine for PostgreSQL |
| `HOST_SERVER_PORT` | `8081` | Port exposed on your machine for the Spring Boot app |

The backend app is accessible at **`http://localhost:8081`** (default).  
API docs / Scalar UI: **`http://localhost:8081/scalar`**

---

## Acceptance Criteria (Test Cases)

Your implementation passes the battle when all of the following are true:

- [ ] `POST /public/auth/signup` with valid data → **201 Created**
- [ ] `POST /public/auth/signup` with mismatched passwords → **400 Bad Request**
- [ ] `POST /public/auth/signup` with duplicate email → **409 Conflict**
- [ ] `POST /public/auth/login` with correct credentials → **200 OK** + both tokens in response body
- [ ] `POST /public/auth/login` with wrong password → **401 Unauthorized**
- [ ] `POST /public/auth/login` with unknown email → **401 Unauthorized**
- [ ] `GET /user/profile` with valid Bearer token → **200 OK** + correct user data
- [ ] `GET /user/profile` without token → **401 Unauthorized**
- [ ] `GET /user/profile` with expired/invalid token → **401 Unauthorized**
- [ ] Password is stored as a BCrypt hash in the database (never plain-text)
- [ ] Access token is stateless JWT (server does **not** persist it)
- [ ] Refresh token is stored server-side and can be invalidated

---

## Hints & Recommendations

- Add `io.jsonwebtoken:jjwt` (or `com.auth0:java-jwt`) to `pom.xml` for JWT handling.
- Add `spring-boot-starter-data-redis` if you choose Redis for refresh-token storage.
- Create a `JwtFilter extends OncePerRequestFilter` and register it **before** `UsernamePasswordAuthenticationFilter` in the security filter chain.
- Use `UserDetailsService` with your `UserRepository` to load users by email.
- Keep the `SecurityConfig` public permit-list intact so Scalar UI remains accessible without a token.
- Format code with `make format` before submitting.
