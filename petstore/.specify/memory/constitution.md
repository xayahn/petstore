# Petstore - Full Stack Pet Marketplace Constitution

## Core Principles

### I. Layered Architecture & Separation of Concerns
Backend (Java SpringBoot) and Frontend (React + Vite) MUST maintain clean architectural layers with:
- Clearly defined API contracts (REST endpoints with OpenAPI/Swagger documentation)
- Service layer decoupling business logic from controllers
- Data access layer abstraction via repository pattern
- Frontend component hierarchy with clear state management boundaries
- NO frontend business logic; all decisions delegated to backend APIs
- Cross-layer communication via well-documented HTTP/REST interfaces only

### II. Security-First Design (NON-NEGOTIABLE)
Every feature development MUST prioritize security:
- HTTPS/TLS required for all communications; HTTP strictly forbidden in production
- Spring Security authentication/authorization on every backend endpoint
- SQL injection prevention via parameterized queries (JPA/Hibernate native support)
- CSRF protection enabled for state-changing operations
- Password hashing with bcrypt or Argon2; NO plain text storage
- Frontend XSS protection via React's built-in escaping + Content Security Policy headers
- Secrets management: database credentials, API keys stored in environment variables or vault
- Input validation on both backend (primary) and frontend (UX enhancement)
- CORS properly configured to whitelist only authorized origins
- Regular security audit requirements before major releases

### III. Complete Documentation (NON-NEGOTIABLE)
All code, APIs, and architecture decisions MUST be documented:
- Inline code comments explaining business logic and non-obvious implementations
- Class/method Javadoc for backend (Java); JSDoc for frontend (JavaScript/React)
- OpenAPI/Swagger specification for all REST endpoints with request/response schemas
- Architecture Decision Records (ADRs) for major design choices
- README files at module level (backend, frontend, root) with setup/build instructions
- API endpoint reference guide maintained and versioned with code
- Database schema documentation with ER diagrams
- Deployment and infrastructure setup documentation
- Configuration management guide (environment variables, profiles)

### IV. Test-Driven Development
Quality assurance MUST follow TDD discipline:
- Unit tests required for all business logic (Services, Utils) with ≥80% code coverage
- Integration tests for API contracts and cross-layer communication
- End-to-end tests for critical user workflows (pet lookup, ordering, payment)
- Backend: JUnit + Mockito/Spring Test framework
- Frontend: Jest + React Testing Library for component and logic tests
- Tests written and approved before implementation
- Failing tests ensure feature development; Red-Green-Refactor cycle enforced
- Performance tests for database queries and API response times
- Continuous integration pipeline validates all tests on every commit

### V. Technology Stack Adherence & Consistency
All code MUST strictly follow the mandated stack:
- **Backend**: Java (Latest LTS version) + Spring Boot + PostgreSQL
  - Spring Data JPA for data access
  - Spring Security for authentication/authorization
  - Spring Validation for input validation
  - Maven for dependency management and builds
- **Frontend**: React with Vite + JavaScript
  - Node.js + npm for package management
  - Axios/Fetch API for HTTP communication
  - Component-based architecture with functional components and hooks
  - CSS-in-JS or modular CSS (no global styles pollution)
- **Database**: PostgreSQL with proper indexing, constraints, and migrations
- NO external monolithic frameworks that bypass these choices (e.g., no server-side rendering frameworks conflicting with React SPA)
- Version pinning in package.json and pom.xml; dependency updates logged with justification

## Security & Data Protection

All transactional data (pet inventory, customer info, orders) MUST be protected:
- Database encryption at rest (PostgreSQL native encryption or container-level)
- Row-level security (RLS) for multi-tenant scenarios if applicable
- Audit logging for all state-changing operations (creates, updates, deletes)
- PII (Personally Identifiable Information) handling: compliance with data privacy regulations
- Backup and disaster recovery procedures documented
- Access control: role-based (RBAC) with Admin, Manager, Customer roles minimum
- Session management: secure token generation, expiration, refresh mechanisms
- Error responses MUST NOT leak sensitive information (stack traces, database schema)

## Development Workflow & Code Quality

All changes MUST follow structured workflows:
- Code reviews required for all PRs; minimum one approval before merge
- Branch naming convention: `feature/feature-name`, `bugfix/bug-name`, `hotfix/issue-name`
- Commit messages: clear, descriptive format following Conventional Commits
- Linting required: ESLint for frontend, Checkstyle/Spotbugs for backend
- Code formatting enforced: Prettier for frontend, Google Java Style for backend
- Pre-commit hooks validate linting and formatting before commits
- Breaking API changes REQUIRE version bumping (Semantic Versioning: MAJOR.MINOR.PATCH)
- Deprecation periods: 2 minor versions before removal of deprecated endpoints/features

## Governance

This Constitution supersedes all other practices and guidelines for the Petstore project.

**Amendment Procedure:**
- Any principle change requires documented justification with business/technical rationale
- Amendments proposed via Pull Request with detailed explanation in PR description
- All team members review; simple majority approval required (50% + 1)
- Version bump following Semantic Versioning: MAJOR for breaking changes, MINOR for additions, PATCH for clarifications
- Migration plan required if amendment impacts existing code or workflows

**Compliance Verification:**
- All Pull Requests checked against this Constitution before merge
- Automated tools (linters, security scanners) enforce applicable principles
- Manual code reviews verify architecture, documentation, and security compliance
- Quarterly governance reviews assess adherence and identify improvement areas

**Version**: 1.0.0 | **Ratified**: 2026-05-05 | **Last Amended**: 2026-05-05
