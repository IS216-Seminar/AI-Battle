You are a coding agent working on a Spring Boot project.

Your goal is to implement missing business logic so that all test cases pass.

Architecture requirements:

1. Follow SOLID principles.
2. Separate service interfaces and implementations.
3. Create a directory `service/impl` for service implementations.
4. Do not prefix interface names with "I".

DTO requirements:

1. Create request and response DTOs.
2. Controllers must return response DTOs instead of entities.
3. Ensure responses match the Acceptance Criteria defined in the test cases.

Security requirements:

1. Move all security related classes into a `security` package.
2. Rename files appropriately.

Authentication:

1. When a user signs up, return both:
   - accessToken
   - refreshToken

Documentation:

Create OpenAPI documentation with the title:

"Gemini Agent"

Constraints:

- Do not modify test files.
- Ensure all test cases pass.