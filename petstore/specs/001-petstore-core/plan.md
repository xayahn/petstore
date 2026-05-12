# Implementation Plan: Full Stack Petstore Core Platform

**Branch**: `001-petstore-core` | **Date**: 2026-05-05 | **Spec**: [spec.md](spec.md)  
**Input**: Full stack e-commerce pet marketplace with Vite + React (frontend), Java Spring Boot (backend), PostgreSQL (database)

## Summary

Full Stack Petstore is a peer-to-peer e-commerce marketplace enabling customers to both buy and sell pets. The platform features a public landing page with hero section and featured pets, seller account management with email verification, shopping cart with persistence, secure checkout, and seller earnings tracking with commission-based revenue model (10-15% platform commission). MVP supports core buyer workflow (browse → cart → checkout → order) and core seller workflow (account → verify email → list pets → manage orders → track earnings).

## Technical Context

**Language/Version**: Java 17 LTS (backend), JavaScript ES2021+ (frontend)  
**Primary Dependencies**: 
- Backend: Spring Boot 3.x, Spring Data JPA, Spring Security, Spring Validation
- Frontend: React 18+, Vite 4+, Axios, React Router

**Storage**: PostgreSQL 14+ with JSONB support for flexible attributes  
**Testing**: 
- Backend: JUnit 5, Mockito, Spring Test, Testcontainers
- Frontend: Jest 29+, React Testing Library, Vitest

**Target Platform**: Linux/Docker (server), Modern browsers (Chrome, Firefox, Safari, Edge - last 2 versions)  
**Project Type**: Web service with React SPA frontend and REST API backend  
**Performance Goals**: 
- Catalog browsing: <2 second page load, response <500ms
- Checkout: complete in <5 minutes
- Search: return results <500ms, 90% relevance in top 5
- System: 100+ concurrent users without degradation, 1000 concurrent carts

**Constraints**: 
- PCI DSS compliance for payment processing (delegated to Stripe/PayPal)
- HTTPS/TLS required for all communications
- 80%+ code coverage for unit/integration tests
- Secure password hashing with bcrypt/Argon2
- All API endpoints must have OpenAPI/Swagger documentation

**Scale/Scope**: 
- Users: 10k+ (MVP), expandable architecture
- Features: 9 user stories, 51 functional requirements
- Data entities: 16 core entities (User, Seller, Pet, Order, Payment, Review, etc.)
- Endpoints: ~40 REST endpoints (CRUD + custom actions)
- Admin functions: inventory management, order fulfillment, seller verification

## Technical Context

**Language/Version**: Java 17 LTS (backend), JavaScript ES2021+ (frontend)  
**Primary Dependencies**: 
- Backend: Spring Boot 3.x, Spring Data JPA, Spring Security, Spring Validation
- Frontend: React 18+, Vite 4+, Axios, React Router

**Storage**: PostgreSQL 14+ with JSONB support for flexible attributes  
**Testing**: 
- Backend: JUnit 5, Mockito, Spring Test, Testcontainers
- Frontend: Jest 29+, React Testing Library, Vitest

**Target Platform**: Linux/Docker (server), Modern browsers (Chrome, Firefox, Safari, Edge - last 2 versions)  
**Project Type**: Web service with React SPA frontend and REST API backend  
**Performance Goals**: 
- Catalog browsing: <2 second page load, response <500ms
- Checkout: complete in <5 minutes
- Search: return results <500ms, 90% relevance in top 5
- System: 100+ concurrent users without degradation, 1000 concurrent carts

**Constraints**: 
- PCI DSS compliance for payment processing (delegated to Stripe/PayPal)
- HTTPS/TLS required for all communications
- 80%+ code coverage for unit/integration tests
- Secure password hashing with bcrypt/Argon2
- All API endpoints must have OpenAPI/Swagger documentation

**Scale/Scope**: 
- Users: 10k+ (MVP), expandable architecture
- Features: 9 user stories, 51 functional requirements
- Data entities: 16 core entities (User, Seller, Pet, Order, Payment, Review, etc.)
- Endpoints: ~40 REST endpoints (CRUD + custom actions)
- Admin functions: inventory management, order fulfillment, seller verification

## Constitution Check

**Constitution Version**: 1.0.0 | **Ratified**: 2026-05-05

**Gate: Compliance Verification** ✅ **PASS**

### Principle I: Layered Architecture & Separation of Concerns
**Status**: ✅ COMPLIANT
- Backend: Spring Boot controllers → services → repositories pattern enforced
- Frontend: React component hierarchy with hooks-based state management
- API contracts: REST endpoints with OpenAPI/Swagger (defined in contracts phase)
- No frontend business logic; all decisions via backend APIs

### Principle II: Security-First Design (NON-NEGOTIABLE)
**Status**: ✅ COMPLIANT
- HTTPS/TLS: Docker/Nginx enforces in production; development allows localhost HTTP only
- Spring Security: Authentication/authorization on all endpoints (OAuth2/JWT)
- SQL injection: JPA parameterized queries, no raw SQL
- Password hashing: bcrypt in Spring Security config
- Frontend XSS: React auto-escapes; CSP headers via backend
- Secrets: Environment variables for DB credentials, API keys, JWT secrets
- CORS: Whitelist origin in Spring config

### Principle III: Complete Documentation (NON-NEGOTIABLE)
**Status**: ✅ COMPLIANT
- Inline comments: Javadoc for backend services; JSDoc for frontend utils
- OpenAPI/Swagger: Generated from Spring Boot annotations; accessible at /api/docs
- ADRs: Architecture decisions documented in docs/ directory
- README files: Backend, frontend, root level setup instructions
- Database: ER diagrams and schema documentation in docs/database.md
- Config guide: Environment variables documented in .env.example

### Principle IV: Test-Driven Development
**Status**: ✅ COMPLIANT
- Unit tests: Services tested with Mockito; components with React Testing Library
- Integration tests: Spring TestContainers for DB; end-to-end with Playwright (post-MVP)
- Coverage: Target 80%+ for all services and critical components
- TDD workflow: Tests written first; Red-Green-Refactor enforced in code review

### Principle V: Technology Stack Adherence & Consistency
**Status**: ✅ COMPLIANT
- Backend: Java 17 LTS + Spring Boot 3.x + PostgreSQL ✅
- Frontend: React 18 + Vite 4 + JavaScript (ES2021+) ✅
- Dependency management: Maven (backend), npm (frontend) ✅
- No conflicting frameworks (e.g., no Next.js SSR conflicting with React SPA) ✅

**Gate Resolution**: ✅ ALL PRINCIPLES COMPLIANT - No violations or justifications needed. Plan may proceed to Phase 0 research and Phase 1 design.

## Project Structure

### Documentation (this feature)

```text
specs/001-petstore-core/
├── plan.md              # This file (implementation plan)
├── research.md          # Phase 0 research findings (generated)
├── data-model.md        # Phase 1 database schema and entities (generated)
├── quickstart.md        # Phase 1 developer quickstart guide (generated)
├── contracts/           # Phase 1 REST API contracts (generated)
│   ├── pet-api.md
│   ├── order-api.md
│   ├── seller-api.md
│   └── auth-api.md
└── checklists/
    └── requirements.md  # Specification quality checklist
```

### Source Code (repository root)

```text
petstore/
├── backend/                          # Java Spring Boot REST API
│   ├── pom.xml                      # Maven configuration
│   ├── src/main/java/com/petstore/
│   │   ├── PetstoreApplication.java # Main Spring Boot class
│   │   ├── config/                  # Spring configuration
│   │   │   ├── SecurityConfig.java
│   │   │   ├── CorsConfig.java
│   │   │   └── JpaConfig.java
│   │   ├── entity/                  # JPA entities (domain models)
│   │   │   ├── User.java
│   │   │   ├── Pet.java
│   │   │   ├── Seller.java
│   │   │   ├── Order.java
│   │   │   ├── Payment.java
│   │   │   ├── Review.java
│   │   │   └── (more entities...)
│   │   ├── dto/                     # Data Transfer Objects (API contracts)
│   │   │   ├── UserDTO.java
│   │   │   ├── PetDTO.java
│   │   │   └── (more DTOs...)
│   │   ├── repository/              # Spring Data JPA repositories
│   │   │   ├── UserRepository.java
│   │   │   ├── PetRepository.java
│   │   │   └── (more repositories...)
│   │   ├── service/                 # Business logic services
│   │   │   ├── UserService.java
│   │   │   ├── PetService.java
│   │   │   ├── OrderService.java
│   │   │   ├── SellerService.java
│   │   │   ├── PaymentService.java
│   │   │   └── (more services...)
│   │   ├── controller/              # REST API controllers
│   │   │   ├── AuthController.java
│   │   │   ├── PetController.java
│   │   │   ├── OrderController.java
│   │   │   ├── SellerController.java
│   │   │   └── (more controllers...)
│   │   ├── security/                # Security utilities
│   │   │   ├── JwtProvider.java
│   │   │   ├── CustomUserDetailsService.java
│   │   │   └── SecurityUtils.java
│   │   └── exception/               # Custom exceptions
│   │       ├── NotFoundException.java
│   │       ├── UnauthorizedException.java
│   │       └── ValidationException.java
│   ├── src/main/resources/
│   │   ├── application.properties    # Spring configuration
│   │   ├── application-dev.properties
│   │   ├── application-prod.properties
│   │   └── db/migration/            # Flyway DB migrations
│   │       ├── V1__initial_schema.sql
│   │       └── (more migrations...)
│   ├── src/test/java/com/petstore/  # Unit and integration tests
│   │   ├── service/
│   │   ├── controller/
│   │   ├── repository/
│   │   └── integration/
│   └── Dockerfile                   # Docker configuration
│
├── frontend/                         # React + Vite SPA
│   ├── package.json                 # npm dependencies
│   ├── vite.config.js              # Vite configuration
│   ├── jest.config.js              # Jest testing configuration
│   ├── .eslintrc.js                # ESLint configuration
│   ├── .prettierrc                 # Prettier code formatting
│   ├── index.html                  # HTML entry point
│   ├── src/
│   │   ├── main.jsx                # React entry point
│   │   ├── App.jsx                 # Root component
│   │   ├── pages/                  # Page components
│   │   │   ├── LandingPage.jsx
│   │   │   ├── BrowsePetsPage.jsx
│   │   │   ├── PetDetailPage.jsx
│   │   │   ├── CartPage.jsx
│   │   │   ├── CheckoutPage.jsx
│   │   │   ├── OrderHistoryPage.jsx
│   │   │   ├── SellerDashboard.jsx
│   │   │   ├── LoginPage.jsx
│   │   │   └── RegisterPage.jsx
│   │   ├── components/             # Reusable React components
│   │   │   ├── Header.jsx
│   │   │   ├── Footer.jsx
│   │   │   ├── PetCard.jsx
│   │   │   ├── CartItem.jsx
│   │   │   ├── OrderSummary.jsx
│   │   │   ├── ReviewForm.jsx
│   │   │   └── (more components...)
│   │   ├── services/               # API client services
│   │   │   ├── api.js             # Axios instance & interceptors
│   │   │   ├── authService.js
│   │   │   ├── petService.js
│   │   │   ├── orderService.js
│   │   │   ├── sellerService.js
│   │   │   └── (more services...)
│   │   ├── context/                # React Context for state mgmt
│   │   │   ├── AuthContext.jsx
│   │   │   ├── CartContext.jsx
│   │   │   └── UserContext.jsx
│   │   ├── hooks/                  # Custom React hooks
│   │   │   ├── useAuth.js
│   │   │   ├── useCart.js
│   │   │   └── useFetch.js
│   │   ├── styles/                 # CSS modules
│   │   │   ├── App.module.css
│   │   │   ├── pages.module.css
│   │   │   └── components.module.css
│   │   ├── utils/                  # Utility functions
│   │   │   ├── validation.js
│   │   │   ├── formatting.js
│   │   │   └── constants.js
│   │   └── __tests__/              # Component and integration tests
│   │       ├── pages/
│   │       ├── components/
│   │       └── services/
│   └── Dockerfile                  # Docker configuration
│
├── docs/                           # Documentation
│   ├── README.md                   # Project overview
│   ├── SETUP.md                    # Development setup
│   ├── API.md                      # API reference
│   ├── ARCHITECTURE.md             # Architecture overview
│   ├── DATABASE.md                 # Database schema & ERD
│   ├── DEPLOYMENT.md               # Deployment guide
│   └── adr/                        # Architecture Decision Records
│       └── (ADR files...)
│
├── .env.example                     # Environment variables template
├── docker-compose.yml              # Multi-container setup
├── .gitignore
├── README.md                        # Root README
└── Makefile                        # Development shortcuts (optional)
```

**Structure Decision**: Full-stack separation with independent backend (Java Spring Boot) and frontend (React + Vite) projects. Backend serves REST API; frontend is SPA consuming API. Supports independent scaling and development workflows. Database is PostgreSQL shared by backend. Docker Compose orchestrates local development (backend, frontend, PostgreSQL).

## Complexity Tracking

**No Constitution violations identified.** Architecture aligns with all five core principles. No justifications needed.

---

# PHASE 0: Research & Problem Clarification

## Unknowns Identified from Technical Context

**Note**: All critical ambiguities resolved in specification clarification phase (Q1-Q5). Phase 0 research focuses on technical best practices and architecture patterns.

### Research Tasks

1. **Spring Boot Security Best Practices for JWT & OAuth2**
   - Recommendation: Spring Security 6.x with OAuth2 Resource Server
   - Decision: Use JWT tokens with refresh token rotation for stateless auth
   - Implementation: Spring Security filter chains, custom JWT provider

2. **React State Management for E-Commerce Cart**
   - Recommendation: React Context API for MVP; Redux for complex state (post-MVP)
   - Decision: Use Context API with localStorage persistence for MVP
   - Implementation: CartContext with useReducer for predictable updates

3. **PostgreSQL Schema Design for Peer-to-Peer Marketplace**
   - Recommendation: Multi-schema approach; separate seller/buyer concerns
   - Decision: Single schema with seller_id foreign keys for pet listings
   - Implementation: Proper indexing on seller_id, category, price for query performance

4. **Payment Integration Pattern (Stripe/PayPal)**
   - Recommendation: PCI-DSS compliance via payment gateway SDKs
   - Decision: Delegate payment processing to Stripe; store transaction IDs only
   - Implementation: Stripe API client in backend; webhook handlers for payment events

5. **Testing Strategy for Full-Stack**
   - Recommendation: Layered testing (unit → integration → E2E)
   - Decision: JUnit + Spring Test (backend), Jest + RTL (frontend), Playwright (E2E)
   - Implementation: Containerized PostgreSQL for integration tests (Testcontainers)

6. **Frontend Build & Deployment with Vite**
   - Recommendation: Vite for fast development; production build to static assets
   - Decision: Vite dev server for local development; build static SPA for Docker
   - Implementation: API proxying in vite.config.js for development

7. **API Design for Multi-Seller Marketplace**
   - Recommendation: RESTful API with clear ownership boundaries (user → seller → orders)
   - Decision: Nested routes for resource hierarchy; query params for filtering
   - Implementation: /api/sellers/{id}/pets, /api/orders with buyer/seller context

## Research Conclusions

**All research tasks provide clear, actionable decisions. No blocking unknowns remain.**

Next phase proceeds with design artifacts and data modeling.
