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

After completing a task, write a log entry to the file `agent_log.md`.

Use the following Markdown structure:

## Task
Describe the task you attempted.

## Actions Taken
List the steps you performed.

## Tools Used
List any tools, APIs, or commands used.

## Result
Describe the outcome of the task.

## Issues
Describe any problems encountered.

## Next Step
Suggest the next action if needed.