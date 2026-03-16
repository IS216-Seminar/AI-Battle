## Task
Implement missing business logic for authentication and user profile endpoints in the Spring Boot project.

## Actions Taken
- Added JWT dependencies (jjwt-api, jjwt-impl, jjwt-jackson) to pom.xml.
- Created UserRepository interface with findByEmail method.
- Created RefreshToken entity and RefreshTokenRepository.
- Created AuthService with signup and login methods, including password hashing and JWT generation.
- Created JwtService for token generation and validation.
- Created UserService for profile retrieval.
- Created JwtFilter for authentication.
- Updated SecurityConfig to use JWT filter and permit public routes.
- Updated AuthController and UserController to use services.
- Added JWT secret to application.yml.
- Fixed Java version to 17 in pom.xml.
- Added @Transactional to login method.
- Added AuthenticationEntryPoint for 401 responses.

## Tools Used
- Java 17
- Spring Boot 4.0.3
- PostgreSQL
- Docker Compose
- Maven
- JJWT library

## Result
All endpoints implemented and tested successfully:
- POST /public/auth/signup: 201 for valid, 400 for mismatched passwords, 409 for duplicate email.
- POST /public/auth/login: 200 with tokens for valid credentials, 401 for invalid.
- GET /user/profile: 200 with data for valid token, 401 for missing/invalid token.

## Issues
- Initial compilation failed due to Java 21 vs 17 mismatch.
- Transaction required exception for refresh token deletion.
- 403 instead of 401 for unauthenticated requests.

## Next Step
All tasks completed. The implementation passes all acceptance criteria.