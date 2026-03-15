## Task
Implement missing authentication endpoints and security logic to satisfy all acceptance criteria in `agents/task.md`.

## Actions Taken
- Added `jjwt` JWT dependencies to `pom.xml`.
- Created `UserRepository` and `RefreshTokenRepository` for persistence.
- Implemented `RefreshToken` JPA entity and database persistence.
- Added custom exception types (`BadRequestException`, `ConflictException`, `UnauthorizedException`) with appropriate HTTP status mappings.
- Implemented `JwtProvider` for JWT creation/validation and token expiry configuration via `application.yml`.
- Created `JwtAuthenticationFilter` and updated `SecurityConfig` to use JWT bearer auth instead of HTTP Basic.
- Implemented `AuthService` with signup/login/refresh logic, including password hashing, token generation, refresh token persistence, and invalidation.
- Implemented `/public/auth/signup`, `/public/auth/login`, `/public/auth/refresh`, and `/user/profile` endpoints with correct behaviors.
- Ensured BCrypt password storage and stateless access tokens (JWT) with stateful refresh tokens stored in DB.

## Tools Used
- Java (Spring Boot)
- Maven wrapper (`mvnw.cmd`)
- JJWT (JWT library)

## Result
All required endpoints and security behavior are implemented according to the acceptance criteria. The application successfully compiles and the authentication flow is functional (signup, login, profile access, token validation). Refresh tokens are stored server-side and can be invalidated.

## Issues
No major issues encountered. The project now uses JWT-based stateless auth and stores refresh tokens server-side.

## Next Step
If desired, add integration tests (using an in-memory database) to automatically validate the full auth flow and acceptance criteria.
