# AI Battle — Score Rubric

> **Total: 100 points** | Evaluated across 4 categories + penalty deductions

---

## Category 1 — Functional Correctness (50 pts)

> Prerequisite: the generated code must compile, run, and satisfy the business logic.

| # | Criterion | Points |
|---|-----------|-------:|
| 1.1 | **Pass Acceptance Criteria** — All test cases pass: correct HTTP status codes, user created successfully, password hashed with BCrypt, JWT + Refresh Token issued in the expected format | 40 |
| 1.2 | **Stateful vs Stateless token design** — Access Token is stateless (never persisted to DB); Refresh Token is stateful (stored in DB or Redis). AI that implements a stateless JWT Refresh Token loses full marks here | 10 |
| | **Subtotal** | **50** |

---

## Category 2 — Architecture & Clean Code (20 pts)

> Running code that is messy is unusable in a real project.

| # | Criterion | Points |
|---|-----------|-------:|
| 2.1 | **Respect project structure** — Uses existing classes (`SignupRequest`, `User` entity) without duplicating them; clear layer separation (Controller only handles request/response; all hashing, token generation, and DB logic lives in Service) | 10 |
| 2.2 | **Modern Java 21 features** — Correct use of `record` for DTOs (as specified in README), `switch` expressions for complex branching, and `var` where it improves readability | 5 |
| 2.3 | **Dependency Injection** — Constructor Injection (or Lombok's `@RequiredArgsConstructor`) used throughout; no `@Autowired` field injection (which is a recognised bad practice in Spring) | 5 |
| | **Subtotal** | **20** |

---

## Category 3 — Security, Validation & Exception Handling (15 pts)

> This category separates junior-level AI output from senior-level output.

| # | Criterion | Points |
|---|-----------|-------:|
| 3.1 | **Input Validation** — DTOs annotated with `@NotBlank`, `@Email`, `@Size(min=6)`, etc.; invalid requests are rejected before reaching the Service or Database layer | 7 |
| 3.2 | **Global Exception Handling** — Wrong credentials or duplicate email returns a structured JSON error body (e.g. `{"error": "Email already exists"}`); raw stack traces must never reach the client (leaks system internals) | 8 |
| | **Subtotal** | **15** |

---

## Category 4 — Observability & Developer Experience (15 pts)

> Measures how maintainable the code is for a team after AI generates it.

| # | Criterion | Points |
|---|-----------|-------:|
| 4.1 | **API Documentation** — SpringDoc OpenAPI annotations (`@Tag`, `@Operation`, `@ApiResponses`) added to `/public/auth/login` and `/public/auth/signup` so the Scalar UI is immediately usable by the frontend team | 8 |
| 4.2 | **Logging** — `log.info()` / `log.warn()` placed at sensitive points: new user registration, failed login (brute-force signal), token expiry. Passwords in plain text must **never** appear in logs | 7 |
| | **Subtotal** | **15** |

---

## Penalty System

> Violations below are deducted directly from the **total score**.

| # | Violation | Penalty |
|---|-----------|--------:|
| P1 | **Out-of-scope file modifications** — Changing Java version in `pom.xml`, altering database config, or deleting base project files | **−15 pts** |
| P2 | **Library hallucination** — Importing libraries not present in `pom.xml` (e.g. Google GSON when Jackson is already available via Spring Boot) | **−10 pts** |
| P3 | **Hardcoded secrets** — JWT secret key written directly into `SecurityConfig` or `JwtService` instead of being read from environment variables via `@Value("${jwt.secret}")` | **−20 pts** |

---

## Score Summary Table

| Category | Max Points | Score |
|----------|-----------:|------:|
| 1. Functional Correctness | 50 | /50 |
| 2. Architecture & Clean Code | 20 | /20 |
| 3. Security, Validation & Exception Handling | 15 | /15 |
| 4. Observability & Developer Experience | 15 | /15 |
| **Subtotal** | **100** | **/100** |
| P1 — Out-of-scope modifications | — | −__ |
| P2 — Library hallucination | — | −__ |
| P3 — Hardcoded secrets | — | −__ |
| **Final Score** | **100** | **/100** |

---

## Quick Reference — Common AI Failure Patterns

| Pattern | Category hit | Typical deduction |
|---------|-------------|-------------------|
| Stateless Refresh Token (lazy JWT) | 1.2 | −10 |
| `@Autowired` field injection everywhere | 2.3 | −5 |
| No `record` keyword — uses plain class for DTO | 2.2 | −5 |
| Stack trace returned as HTTP response body | 3.2 | −8 |
| JWT secret hardcoded as string literal | P3 | −20 |
| Imports library outside `pom.xml` | P2 | −10 |
| Modifies `pom.xml` Java version / DB config | P1 | −15 |
