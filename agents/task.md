# Execution Roadmap & Tasks

## Task 1: Environment & Persistence
- [ ] **Dependencies:** Add `jjwt` (api, impl, jackson) and `spring-boot-starter-validation` to `pom.xml`.
- [ ] **Entities:** Implement `User` (UUID, email, password, timestamps) and `RefreshToken` (token, expiryDate, User relationship).
- [ ] **Repositories:** Create `UserRepository` and `RefreshTokenRepository`.

## Task 2: Security Infrastructure
- [ ] **JwtService:** Logic for generating/extracting 15-minute stateless tokens.
- [ ] **RefreshTokenService:** Logic for 7-day stateful token management (save to DB, rotate, invalidate).
- [ ] **JwtFilter:** Implement `OncePerRequestFilter` to validate Bearer tokens on every protected request.
- [ ] **SecurityConfig:** Configure `SecurityFilterChain` to be stateless, add JWT filter, and permit public routes.

## Task 3: API & Business Logic
- [ ] **Signup Logic:** Validate password match (400) -> Check email exists (409) -> Hash & Save.
- [ ] **Login Logic:** Authenticate via `AuthenticationManager` -> Generate Access + Refresh tokens -> Return 200 OK.
- [ ] **Profile Logic:** Fetch current authenticated user from `SecurityContext` -> Return 200 OK.

## Expected Behaviour (Success Criteria)
- `POST /public/auth/signup`: 201 Created (Success) | 400 (Mismatch) | 409 (Duplicate).
- `POST /public/auth/login`: 200 OK + Tokens | 401 (Wrong credentials).
- `GET /user/profile`: 200 OK + User Data | 401 (Invalid/Missing Token).