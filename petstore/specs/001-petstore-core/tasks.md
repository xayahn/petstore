# Implementation Tasks: Full Stack Petstore Core Platform

**Feature**: 001-petstore-core  
**Generated**: 2026-05-05  
**Status**: Ready for Implementation  
**Total Tasks**: 78 | **Estimated Duration**: 8-10 weeks (MVP scope)  

---

## Executive Summary

This document breaks down the Petstore MVP into 78 discrete, implementable tasks organized across 13 phases. Tasks follow Test-Driven Development (TDD) principles where tests precede implementation. Each task includes effort estimates, prerequisites, affected files, and mapping to functional requirements (FR-001 through FR-051).

**Key Metrics**:
- **Total Requirements**: 51 functional requirements across 9 user stories
- **Total Entities**: 17 database entities with relationships
- **API Endpoints**: ~40 REST endpoints with OpenAPI documentation
- **Test Coverage Target**: 80%+ for services and critical components
- **Parallel Opportunities**: 35+ tasks can execute concurrently
- **Critical Path**: ~6 weeks (sequential dependencies minimum)

**MVP Scope**: User Stories 1-5 (P1 features) provide complete buyer and seller workflows. User Stories 6-9 (P2-P3 features) are post-MVP enhancements for initial release polish.

---

## Implementation Strategy

### Phase Execution Order

```
Phase 0 (Week 1)
├─ Project Setup & Infrastructure
├─ Database Initialization
└─ Development Environment

Phase 1 (Week 1-2) [Parallel: P tasks]
├─ Backend: Security, Config, Core Services
├─ Frontend: Build Setup, Context, Base Components
├─ Database: Migrations, Schema
└─ Docker: Compose configuration

Phase 2 (Week 2-3) [Foundation Testing]
├─ Backend: Unit test infrastructure
├─ Frontend: Component test setup
└─ Integration: Container tests

Phase 3 (Week 3-4) [User Story 1: Landing Page & Catalog]
├─ Backend: Pet service & catalog API
├─ Frontend: Landing page, browse page
├─ Tests: E2E catalog browsing

Phase 4 (Week 4-5) [User Story 2: Seller Account]
├─ Backend: Seller service & verification
├─ Frontend: Seller registration flow
├─ Tests: Seller onboarding E2E

Phase 5 (Week 4-5, Parallel) [User Story 4: User Account]
├─ Backend: User profile & management
├─ Frontend: Authentication UI
├─ Tests: Login/logout E2E

Phase 6 (Week 5-6) [User Story 3: Shopping Cart]
├─ Backend: Cart service & persistence
├─ Frontend: Cart UI components
├─ Tests: Cart operations

Phase 7 (Week 6-7) [User Story 5: Checkout & Orders]
├─ Backend: Order & payment services
├─ Frontend: Checkout flow
├─ Tests: Complete checkout

Phase 8 (Week 7) [Admin Functions]
├─ Backend: Admin inventory management
├─ Frontend: Admin dashboard
├─ Tests: Admin operations

Phase 9 (Week 8) [Reviews & Wishlist (P3)]
├─ Backend: Review & wishlist services
├─ Frontend: Review & wishlist UI
├─ Tests: User-generated content

Phase 10 (Week 8) [Revenue & Billing]
├─ Backend: Commission & payout tracking
├─ Tests: Revenue calculations

Phase 11 (Week 8-9) [Security & Compliance]
├─ Backend: API security, rate limiting
├─ Frontend: Security headers, XSS prevention
├─ Tests: Security test suite

Phase 12 (Week 9) [Polish & Integration]
├─ Error handling refinement
├─ Performance optimization
├─ Cross-cutting concerns

Phase 13 (Week 10) [E2E Tests & Deployment]
├─ Playwright E2E test suite
├─ Docker build & compose validation
├─ Production readiness checklist
```

### Parallelization Opportunities

**Parallel Set 1** (Week 1-2): All backend service implementations + frontend components + database setup
- `[P]` mark indicates task can run in parallel with others in same phase

**Parallel Set 2** (Week 2-3): All unit test implementations across services and components

**Parallel Set 3** (Week 3+): Multiple user story implementations can progress simultaneously:
- Story 3 (Cart) while Story 4 (Seller Account) and Story 5 (User Account) progress
- Stories 8-9 (Reviews/Wishlist) while Story 7 (Admin) progresses

---

## Phase 0: Project Setup & Infrastructure

### Goal
Initialize project structure, set up development environment, establish build pipelines, and configure shared infrastructure (Docker, databases, CI/CD prerequisites).

### Independent Test Criteria
- `docker-compose up` starts all services (backend, frontend, PostgreSQL) without errors
- Development environment variables properly configured
- Build tools (Maven, npm, Docker) functional
- Git repository initialized with feature branch

### Tasks

- [X] T001 Set up backend project structure with Maven pom.xml in backend/ directory
- [X] T002 Initialize Spring Boot 3.x application class and application.properties configuration
- [X] T003 [P] Create frontend project structure with Vite 4+ and React 18+ entry points in frontend/
- [X] T004 [P] Configure npm dependencies (React Router, Axios, React Testing Library, Jest) in frontend/package.json
- [X] T005 [P] Set up Maven dependencies (Spring Web, Spring Data JPA, Spring Security, PostgreSQL) in backend/pom.xml
- [X] T006 [P] Create docker-compose.yml with services: backend, frontend, PostgreSQL 14+
- [X] T007 Create Dockerfile for backend Spring Boot application with OpenJDK 17
- [X] T008 [P] Create Dockerfile for frontend Vite application with Node.js
- [X] T009 Create .gitignore for both backend (target/, .class, IDE) and frontend (node_modules/, dist/, .env)
- [X] T010 [P] Initialize Git repository and create feature branch 001-petstore-core
- [X] T011 Create .env.example with database credentials, JWT secrets, API keys template
- [X] T012 [P] Create README.md (root level) with project overview and setup instructions
- [X] T013 [P] Create backend/README.md with API setup, build, and run instructions
- [X] T014 [P] Create frontend/README.md with development server, build, and testing instructions
- [X] T015 Create Makefile or run scripts for common commands (build, test, serve)
- [X] T016 [P] Create docs/ directory structure: README.md, SETUP.md, API.md, ARCHITECTURE.md, DATABASE.md

**Effort**: 16 tasks | **Duration**: 1 day (all parallelizable except sequencing)

---

## Phase 1: Foundation - Core Backend & Frontend Architecture

### Goal
Establish layered architecture (controller → service → repository), security foundation (JWT authentication), database connection, and React context-based state management. This phase is the blocking prerequisite for all user story implementations.

### Independent Test Criteria
- Spring Boot starts without errors; all @Configuration beans loaded
- Database connection successful via Spring Data JPA
- JWT token generation and validation working
- React app builds and runs without errors
- API documentation endpoint accessible at /api/docs

### Tasks

#### Backend: Core Configuration & Security (Foundation)

- [X] T017 [P] Create Spring Security configuration class (SecurityConfig.java) with CORS, HTTPS settings
- [X] T018 [P] Implement JWT token provider (JwtProvider.java) for token generation and validation
- [X] T019 [P] Create custom UserDetailsService (CustomUserDetailsService.java) for Spring Security integration
- [X] T020 [P] Create SecurityUtils.java with helper methods for extracting current user from context
- [X] T021 [P] Create custom exception classes: NotFoundException, UnauthorizedException, ValidationException in exception/ directory
- [X] T022 [P] Create GlobalExceptionHandler.java for centralized REST exception handling
- [X] T023 [P] Configure application.properties with profiles (dev, prod): spring.jpa.hibernate.ddl-auto, logging levels, server.port
- [X] T024 Create CorsConfig.java with allowed origins and credentials configuration
- [X] T025 [P] Create OpenAPI/Swagger configuration (SpringDocOpenApiConfig.java) for /api/docs endpoint

#### Backend: Database & JPA Configuration

- [X] T026 [P] Create JpaConfig.java for entity auditing (created_at, updated_at timestamps)
- [X] T027 [P] Create database connection properties: spring.datasource.url, username, password via environment variables
- [X] T028 Configure Flyway database migration runner (flyway.placeholders, migration directory: src/main/resources/db/migration/)

#### Frontend: React Configuration & Context Setup

- [X] T029 [P] Configure Vite (vite.config.js) with dev server port, build output, proxy to backend
- [X] T030 [P] Set up Axios instance (src/services/api.js) with base URL, interceptors for JWT tokens, error handling
- [X] T031 [P] Create AuthContext.jsx for authentication state (user, tokens, is_authenticated)
- [X] T032 [P] Create CartContext.jsx for shopping cart state (items, totals, operations)
- [X] T033 [P] Create UserContext.jsx for user profile state (profile, settings, preferences)
- [X] T034 [P] Create useAuth custom hook (src/hooks/useAuth.js) wrapping AuthContext
- [X] T035 [P] Create useCart custom hook (src/hooks/useCart.js) wrapping CartContext
- [X] T036 [P] Create useFetch custom hook (src/hooks/useFetch.js) for API calls with loading/error states
- [X] T037 [P] Create .eslintrc.js configuration for code quality standards
- [X] T038 [P] Create .prettierrc for consistent code formatting

#### Frontend: Base Layout & Components

- [X] T039 [P] Create Header component (src/components/Header.jsx) with navigation links, auth status
- [X] T040 [P] Create Footer component (src/components/Footer.jsx) with company info, links
- [X] T041 [P] Create App.jsx root component with router setup and context providers
- [X] T042 [P] Create main.jsx React entry point with App render and provider setup
- [X] T043 [P] Create index.html with meta tags, title, root div for React
- [X] T044 [P] Create base CSS modules (src/styles/App.module.css, components.module.css) with Vite asset imports
- [X] T045 [P] Create loading spinner component (src/components/LoadingSpinner.jsx)
- [X] T046 [P] Create error alert component (src/components/ErrorAlert.jsx) with dismissible UI

#### Database: Schema Initialization

- [X] T047 Create Flyway migration V1__initial_schema.sql with all 17 entities: User, Seller, Pet, Order, Payment, Review, Cart, CartItem, WishlistItem, Wishlist, SellerProfile, SellerEarnings, Payout, PetImage, OrderItem, SellerReview, ShippingAddress
- [X] T048 Create migration V2__add_indexes.sql with all performance indexes (seller_id, status, created_at, full-text search on pet names)
- [X] T049 Create migration V3__add_constraints.sql with foreign keys (ON DELETE RESTRICT, ON UPDATE CASCADE)
- [X] T050 Add seed data migration (optional) with sample admin pets and sellers for development (V4__seed_data.sql)

**Effort**: 34 tasks | **Duration**: 3-4 days

**Parallel Execution**: T017-T046 can run concurrently (separate backend/frontend concerns). Sequential: T047-T050 after database is up.

---

## Phase 2: Test Infrastructure & TDD Foundation

### Goal
Establish unit testing, integration testing, and component testing frameworks. Set up test coverage tracking. This phase enables TDD for subsequent service implementations.

### Independent Test Criteria
- JUnit 5 + Mockito tests execute successfully with coverage reports
- Jest + React Testing Library component tests execute successfully
- Spring TestContainers PostgreSQL container starts for integration tests
- Test coverage baseline established (minimum 50% for Phase 2, target 80% by end)

### Tasks

#### Backend: Unit Test Infrastructure

- [X] T051 [P] Create ServiceTestBase abstract class with Mockito setup for common service testing patterns in src/test/java/com/petstore/service/
- [X] T052 [P] Create @ExtendWith annotations for Spring Boot test configuration
- [X] T053 [P] Create test fixtures and factory methods for creating User, Pet, Seller, Order entities
- [X] T054 [P] Set up JUnit 5 parameterized test templates in src/test/java/com/petstore/
- [X] T055 [P] Create Hamcrest matchers for custom assertions (e.g., PetMatcher for comparing pet entities)
- [X] T056 [P] Configure jacoco-maven-plugin for code coverage reports in pom.xml

#### Backend: Integration Test Infrastructure

- [X] T057 Create IntegrationTestBase abstract class extending @SpringBootTest with @DataJpaTest
- [X] T058 [P] Configure Testcontainers PostgreSQL container (dependency in pom.xml, properties override in test)
- [X] T059 [P] Create DatabaseSetup utility class for test database initialization and cleanup
- [X] T060 Create @IntegrationTest marker annotation for integration test discovery and filtering

#### Frontend: Component Test Infrastructure

- [X] T061 [P] Create Jest configuration (jest.config.js) with module resolver, CSS module mock, Babel transpilation
- [X] T062 [P] Create React Testing Library setup file (src/__tests__/setup.js) with global render function
- [X] T063 [P] Create custom render utility (src/__tests__/renderWithContext.js) wrapping components with contexts and providers
- [X] T064 [P] Create mock API client (src/__tests__/mocks/api.js) with jest.mock for API service
- [X] T065 [P] Create test utilities for common patterns (waitForAsync, fireEvent, screen queries)
- [X] T066 [P] Configure Jest coverage reporter (collectCoverageFrom, threshold) in jest.config.js

#### Frontend: API Mock Setup

- [X] T067 [P] Create msw (Mock Service Worker) setup for API mocking in tests
- [X] T068 [P] Create mock handlers for auth endpoints (register, login, logout)
- [X] T069 [P] Create mock handlers for pet endpoints (list, detail, create, update)
- [X] T070 [P] Create mock handlers for cart endpoints (get, add, update, remove)

**Effort**: 20 tasks | **Duration**: 2-3 days

**Parallel Execution**: All [P] marked tasks can run concurrently. Sequential: setup then test infrastructure.

---

## Phase 3: User Story 1 - Landing Page & Browse Pet Inventory (P1)

### Goal
Implement public landing page with hero banner and featured pets, public pet catalog browsing with filters and search, and all catalog discovery features. No authentication required for browsing.

### User Story
**As a visitor (authenticated or not), I want to see the landing page with a strong hero section and immediately view available pets without mandatory login so I can discover what the marketplace offers.**

### Independent Test Criteria
- Landing page loads unauthenticated and displays hero banner
- Featured pets section displays 5-10 trending pets
- Full pet catalog accessible and searchable without login
- Filters (category, price range, breed) work correctly
- Search returns relevant results
- Pagination works correctly
- Page load time < 2 seconds
- All pets are visible with images, names, prices, seller ratings

### Tasks

#### Backend: Pet Service & API (Story 1)

- [X] T071 [P] Create Pet JPA entity class (src/main/java/com/petstore/entity/Pet.java) with all fields per data model
- [X] T072 [P] Create PetDTO.java and PetListItemDTO.java for API responses
- [X] T073 [P] Create PetRepository.java extending JpaRepository with custom query methods: findByCategoryAndPriceRange, search by name/breed/species
- [X] T074 [P] Test: Create PetRepositoryTest.java testing findAll, filtering, sorting, pagination (Integration test with TestContainers)
- [X] T075 Create PetService.java with business logic: getPetById, listPets (with filters), searchPets, getFeaturedPets (random sample, highest rated, newest)
- [X] T076 Test: Create PetServiceTest.java testing getPetById, listPets filtering, searchPets, getFeaturedPets (Unit test with Mockito)
- [X] T077 Create PetController.java REST endpoints: GET /api/pets (list with filters), GET /api/pets/{id} (detail), GET /api/pets/featured (public endpoints, no auth required)
- [X] T078 Test: Create PetControllerTest.java testing endpoints return correct DTOs, status codes, error handling

#### Frontend: Landing Page & Catalog (Story 1)

- [X] T079 [P] Create LandingPage.jsx (src/pages/LandingPage.jsx) component with hero section and featured pets display
- [X] T080 [P] Create HeroBanner.jsx sub-component with CTA buttons ("Browse Pets", "Start Selling")
- [X] T081 [P] Create FeaturedPetsSection.jsx sub-component displaying 5-8 featured pets
- [X] T082 [P] Create PetCard.jsx reusable component showing pet image, name, breed, price, seller rating
- [X] T083 Create BrowsePetsPage.jsx (src/pages/BrowsePetsPage.jsx) with full catalog, filters sidebar
- [X] T084 [P] Create PetFilterSidebar.jsx sub-component with category, price range, breed filters
- [X] T085 [P] Create SearchBar.jsx component for pet search functionality
- [X] T086 Create PetListView.jsx component displaying paginated list of pets with sorting options
- [X] T087 Create PetDetailPage.jsx (src/pages/PetDetailPage.jsx) showing full pet details with images carousel, seller info, reviews
- [X] T088 [P] Create PetImageCarousel.jsx component for multiple pet images
- [X] T089 [P] Create SellerCard.jsx component displaying seller info (name, rating, verified badge)
- [X] T090 Create API service: src/services/petService.js with methods: listPets(filters), getPetDetail(id), searchPets(query), getFeaturedPets()
- [X] T091 Test: Create LandingPage.test.jsx testing hero display, featured pets load, navigation links
- [X] T092 Test: Create BrowsePetsPage.test.jsx testing catalog load, filters work, pagination works
- [X] T093 Test: Create PetDetailPage.test.jsx testing pet details display, seller info, review section

#### Database: Pet Image Support

- [X] T094 Create PetImage JPA entity (src/main/java/com/petstore/entity/PetImage.java) with image URLs and display order
- [X] T095 Create PetImageRepository.java with query methods
- [X] T096 [P] Update PetDTO to include image list in responses

#### Tests: E2E Catalog Browsing

- [X] T097 Create Playwright test: tests/e2e/catalog-browsing.spec.js - landing page hero, featured pets display
- [X] T098 Create Playwright test: catalog filtering, search, pagination
- [X] T099 Create Playwright test: pet detail page display and navigation

**Effort**: 29 tasks | **Duration**: 3-4 days

**Parallel Execution**: All T071-T096 [P] marked tasks can run concurrently. T077-T090 require T075-T076 to complete. T097-T099 after all backend/frontend complete.

**Functional Requirements Covered**: FR-001 (hero banner), FR-002 (featured pets), FR-003 (pet listings), FR-004 (filters), FR-005 (browse by category), FR-006 (search), FR-007 (detailed info), FR-008 (pagination)

---

## Phase 4: User Story 2 - Seller Account & Pet Listing Management (P1)

### Goal
Enable customers to become sellers, verify email, create pet listings, manage inventory, and view seller dashboard with sales tracking.

### User Story
**As a pet owner/breeder, I want to create a seller account, verify my email, list my pets for sale with complete information, and manage my inventory so I can sell pets on the marketplace.**

### Independent Test Criteria
- User can register as seller with business name
- Verification email sent and confirmation works
- Verified seller can create pet listings
- Listings appear in public catalog
- Seller can update listing details
- Seller can archive/mark unavailable
- Seller dashboard displays active listings and sales
- Seller profile visible to buyers with ratings

### Tasks

#### Backend: Seller Service & API (Story 2)

- [X] T100 [P] Create Seller JPA entity (src/main/java/com/petstore/entity/Seller.java) with business_name, verification_status, email_verified_at, rating, total_sales
- [X] T101 [P] Create SellerProfile JPA entity (src/main/java/com/petstore/entity/SellerProfile.java) with bio, return_policy, payout details
- [X] T102 [P] Create SellerDTO.java, SellerProfileDTO.java for API responses
- [X] T103 [P] Create SellerRepository.java with custom queries: findByVerificationStatus, findByRating, findActiveSellersPaginatedWithProfile
- [X] T104 [P] Create SellerProfileRepository.java
- [X] T105 Test: Create SellerRepositoryTest.java testing seller queries, verification status filtering
- [X] T106 Create SellerService.java with: registerSeller(userId, businessName), verifySeller(token), getSellerProfile(sellerId), updateSellerProfile, getSellersBy(filter)
- [X] T107 Create EmailVerificationService.java with: generateVerificationToken(seller), sendVerificationEmail(seller), verifyToken(token)
- [X] T108 Test: Create SellerServiceTest.java testing seller registration, email verification flow, profile updates
- [X] T109 Create SellerController.java endpoints: POST /api/sellers/register, POST /api/sellers/verify-email, GET /api/sellers/{id} (public), PUT /api/sellers/me/profile (authenticated)
- [X] T110 Test: Create SellerControllerTest.java testing seller endpoints

#### Backend: Pet Listing Management (Story 2)

- [ ] T111 [P] Update PetService to add seller-specific methods: createPetListing(sellerId, petData), updateListing(petId, updates), archivepet(petId), getSellerPets(sellerId)
- [ ] T112 [P] Update PetController with POST /api/pets (create listing - seller only), PUT /api/pets/{id}, PATCH /api/pets/{id}/availability
- [ ] T113 [P] Create PetListingValidator.java validating required fields for seller listings
- [ ] T114 [P] Add seller_id constraint to Pet entity (FK to Seller, nullable for admin pets)
- [ ] T115 Test: Create seller pet listing tests in PetServiceTest and PetControllerTest

#### Backend: Seller Dashboard API

- [ ] T116 Create SellerDashboardService.java with: getSalesStats(sellerId), getRecentOrders(sellerId), getEarningsStats(sellerId)
- [ ] T117 Create GET /api/sellers/me/dashboard endpoint returning sales stats, recent orders, earnings
- [ ] T118 Test: Create SellerDashboardControllerTest.java

#### Frontend: Seller Registration & Onboarding (Story 2)

- [ ] T119 [P] Create BecomeSeller.jsx page component (src/pages/BecomeSeller.jsx) with registration form
- [ ] T120 [P] Create SellerRegistrationForm.jsx with business_name, bio, return_policy fields
- [ ] T121 [P] Create EmailVerificationStep.jsx component prompting user to verify email
- [ ] T122 [P] Create VerifyEmailPage.jsx (src/pages/VerifyEmailPage.jsx) with token verification UI
- [ ] T123 Test: Create BecomeSeller.test.jsx, SellerRegistrationForm.test.jsx

#### Frontend: Pet Listing Management (Story 2)

- [ ] T124 Create CreatePetListing.jsx (src/pages/CreatePetListing.jsx) form for sellers to create listings
- [ ] T125 Create PetListingForm.jsx sub-component with name, species, breed, age, price, images, health_status, description fields
- [ ] T126 Create MultiImageUpload.jsx component for uploading multiple pet images
- [ ] T127 Create EditPetListing.jsx (src/pages/EditPetListing.jsx) for updating listings
- [ ] T128 [P] Create ArchivePetModal.jsx component for archiving/marking unavailable
- [ ] T129 Test: Create CreatePetListing.test.jsx, PetListingForm.test.jsx

#### Frontend: Seller Dashboard (Story 2)

- [ ] T130 Create SellerDashboard.jsx (src/pages/SellerDashboard.jsx) showing active listings, sales, earnings
- [ ] T131 Create MyListings.jsx sub-component displaying seller's active/archived pets
- [ ] T132 Create SalesStatistics.jsx sub-component with sales count, total revenue, active listings
- [ ] T133 Create RecentOrdersWidget.jsx showing recent buyer orders
- [ ] T134 Test: Create SellerDashboard.test.jsx

#### Frontend: Seller Profile (Story 2)

- [ ] T135 Create SellerProfilePage.jsx (src/pages/SellerProfilePage.jsx) - public seller profile view
- [ ] T136 Create SellerProfileCard.jsx component showing seller details, rating, reviews
- [ ] T137 Create SellerReviewsSection.jsx component displaying seller reviews
- [ ] T138 Test: Create SellerProfilePage.test.jsx

#### API Service Layer (Story 2)

- [ ] T139 Create src/services/sellerService.js with: registerSeller, verifyEmail, getSellerProfile, updateProfile, createListing, updateListing, getDashboard

#### Tests: E2E Seller Onboarding

- [ ] T140 Create Playwright test: tests/e2e/seller-registration.spec.js - become seller flow
- [ ] T141 Create Playwright test: email verification flow
- [ ] T142 Create Playwright test: create and manage listings
- [ ] T143 Create Playwright test: seller dashboard display

**Effort**: 44 tasks | **Duration**: 4-5 days

**Parallel Execution**: T100-T104 [P] in parallel. T106-T109 sequential after T105. T119-T138 frontend tasks mostly parallel. T140-T143 after backend/frontend complete.

**Functional Requirements Covered**: FR-017 (seller status), FR-018 (email verification), FR-019 (seller dashboard), FR-020 (pet listing), FR-021 (update listing), FR-022 (archive pet), FR-023 (seller profile), FR-024 (seller order view)

---

## Phase 5: User Story 4 - User Account Management (P1) [PARALLEL with Phase 4]

### Goal
Enable customer registration, authentication, profile management, and order history viewing. Foundation for all authenticated features.

### User Story
**As a customer, I want to create an account, log in securely, manage my profile information, and view my order history so I can maintain my customer account and track purchases.**

### Independent Test Criteria
- User can register with email and password
- Confirmation email received
- User can login with valid credentials
- Session persists across browser closes
- User profile page editable (name, address, phone)
- Order history displays all past orders with details
- Logout works correctly
- Invalid login rejected

### Tasks

#### Backend: User Service & Authentication (Story 4)

- [ ] T144 [P] Create User JPA entity (src/main/java/com/petstore/entity/User.java) per data model
- [ ] T145 [P] Create UserDTO.java, UserProfileDTO.java for API responses
- [ ] T146 [P] Create UserRepository.java with custom queries: findByEmail, existsByEmail
- [ ] T147 Test: Create UserRepositoryTest.java
- [ ] T148 Create UserService.java with: registerUser(email, password, firstName, lastName), authenticateUser(email, password), updateProfile(userId, updates), getUserProfile(userId)
- [ ] T149 Test: Create UserServiceTest.java testing registration, authentication, profile updates
- [ ] T150 Create AuthService.java with: login(email, password), logout(userId), refreshToken, isTokenValid
- [ ] T151 Create AuthController.java endpoints: POST /api/auth/register, POST /api/auth/login, POST /api/auth/logout, POST /api/auth/refresh-token, GET /api/auth/me
- [ ] T152 Test: Create AuthControllerTest.java testing auth endpoints, JWT token generation

#### Backend: Password Hashing & Security

- [ ] T153 [P] Create PasswordEncoderConfig.java configuring bcrypt password encoding in SecurityConfig
- [ ] T154 [P] Add password validation utility: PasswordValidator.java (minimum 8 chars, special chars, numbers)

#### Backend: User Profile & Order History

- [ ] T155 Create UserProfileController.java endpoints: GET /api/users/me/profile, PUT /api/users/me/profile (authenticated)
- [ ] T156 Create UserOrderService.java with: getOrderHistory(userId), getOrderDetail(userId, orderId)
- [ ] T157 Test: Create UserOrderServiceTest.java

#### Frontend: Authentication Pages (Story 4)

- [ ] T158 [P] Create LoginPage.jsx (src/pages/LoginPage.jsx) with email/password form
- [ ] T159 [P] Create LoginForm.jsx sub-component with validation and error display
- [ ] T160 [P] Create RegisterPage.jsx (src/pages/RegisterPage.jsx) with registration form
- [ ] T161 [P] Create RegistrationForm.jsx sub-component with firstName, lastName, email, password, confirmPassword fields
- [ ] T162 [P] Create PasswordStrengthIndicator.jsx component for password validation feedback
- [ ] T163 Test: Create LoginPage.test.jsx, RegisterPage.test.jsx

#### Frontend: User Profile (Story 4)

- [ ] T164 Create ProfilePage.jsx (src/pages/ProfilePage.jsx) showing user details, editable fields
- [ ] T165 Create ProfileEditForm.jsx for updating name, address, phone, email
- [ ] T166 Create PasswordChangeForm.jsx for secure password update
- [ ] T167 Create ShippingAddressManager.jsx for managing multiple delivery addresses
- [ ] T168 Test: Create ProfilePage.test.jsx

#### Frontend: Order History (Story 4)

- [ ] T169 Create OrderHistoryPage.jsx (src/pages/OrderHistoryPage.jsx) showing all past orders
- [ ] T170 Create OrderItem.jsx component displaying order summary (number, date, total, status)
- [ ] T171 Create OrderDetailModal.jsx for viewing full order details with items
- [ ] T172 Test: Create OrderHistoryPage.test.jsx

#### Frontend: Authentication Context Integration

- [ ] T173 Update AuthContext.jsx to integrate with AuthService: login, logout, register, getCurrentUser
- [ ] T174 Create ProtectedRoute.jsx component redirecting unauthenticated users to login
- [ ] T175 Test: Create ProtectedRoute.test.jsx

#### API Service Layer (Story 4)

- [ ] T176 Create src/services/authService.js with: login, logout, register, refreshToken, getCurrentUser
- [ ] T177 Create src/services/userService.js with: getProfile, updateProfile, getOrderHistory, changePassword

#### Tests: E2E Authentication Flow

- [ ] T178 Create Playwright test: tests/e2e/user-auth.spec.js - registration flow
- [ ] T179 Create Playwright test: login/logout flow
- [ ] T180 Create Playwright test: profile update flow
- [ ] T181 Create Playwright test: order history display

**Effort**: 38 tasks | **Duration**: 3-4 days (parallel with Phase 4)

**Parallel Execution**: All [P] tasks can run concurrently across backend/frontend. T158-T171 frontend tasks mostly independent.

**Functional Requirements Covered**: FR-012 (account creation), FR-013 (password hashing), FR-014 (profile updates), FR-015 (order history), FR-016 (RBAC)

---

## Phase 6: User Story 3 - Shopping Cart Management (P1)

### Goal
Implement shopping cart with add/remove items, persistence across sessions, quantity management, and accurate pricing calculations.

### User Story
**As a customer, I want to add pets and pet accessories to my cart, view cart contents, modify quantities, and remove items so I can prepare my purchase.**

### Independent Test Criteria
- Pet can be added to cart
- Cart persists after logout/login
- Items can be removed from cart
- Quantity can be updated
- Cart total recalculates correctly (subtotal, tax, shipping)
- Cart empty state handled gracefully
- Multiple items from different sellers grouped correctly
- Out-of-stock items rejected

### Tasks

#### Backend: Cart Service & API (Story 3)

- [ ] T182 [P] Create Cart JPA entity (src/main/java/com/petstore/entity/Cart.java) - one per user
- [ ] T183 [P] Create CartItem JPA entity (src/main/java/com/petstore/entity/CartItem.java) - line items
- [ ] T184 [P] Create CartDTO.java, CartItemDTO.java for API responses
- [ ] T185 [P] Create CartRepository.java, CartItemRepository.java with custom queries
- [ ] T186 Test: Create CartRepositoryTest.java
- [ ] T187 Create CartService.java with: getCart(userId), addItem(userId, petId, quantity), removeItem(userId, cartItemId), updateQuantity(userId, cartItemId, newQuantity), clearCart(userId), calculateTotals(cart)
- [ ] T188 Create TaxCalculationService.java with: calculateTax(subtotal, state) - configurable tax rates per state
- [ ] T189 Create ShippingService.java with: calculateShipping(cartItems) - flat rate per item
- [ ] T190 Test: Create CartServiceTest.java testing add, remove, quantity updates, total calculations
- [ ] T191 Create CartController.java endpoints: GET /api/carts (get current user's cart), POST /api/carts/items (add to cart), PUT /api/carts/items/{id} (update quantity), DELETE /api/carts/items/{id} (remove item)
- [ ] T192 Test: Create CartControllerTest.java

#### Frontend: Cart Context & Components (Story 3)

- [ ] T193 [P] Update CartContext.jsx to include: addItem, removeItem, updateQuantity, getTotals methods
- [ ] T194 [P] Create CartPage.jsx (src/pages/CartPage.jsx) displaying full cart
- [ ] T195 [P] Create CartItemRow.jsx component for each line item with quantity picker
- [ ] T196 [P] Create CartSummary.jsx component showing subtotal, tax, shipping, total
- [ ] T197 [P] Create CartEmptyState.jsx component for empty cart UX
- [ ] T198 Create AddToCartButton.jsx component with pet detail page integration
- [ ] T199 Test: Create CartPage.test.jsx, CartItemRow.test.jsx, CartSummary.test.jsx

#### Frontend: Persistence & Local Storage

- [ ] T200 Create localStorage utility (src/utils/storage.js) for cart persistence
- [ ] T201 Update CartContext to persist/restore from localStorage on mount
- [ ] T202 Test: Create storage utility tests

#### API Service Layer (Story 3)

- [ ] T203 Create src/services/cartService.js with: getCart, addItem, removeItem, updateQuantity, clearCart

#### Tests: E2E Shopping Cart

- [ ] T204 Create Playwright test: tests/e2e/shopping-cart.spec.js - add items, view cart, modify quantities
- [ ] T205 Create Playwright test: cart persistence after logout
- [ ] T206 Create Playwright test: cart item removal

**Effort**: 24 tasks | **Duration**: 2-3 days

**Parallel Execution**: T182-T190 backend mostly parallel. T193-T199 frontend mostly parallel. T204-T206 after backend/frontend.

**Functional Requirements Covered**: FR-009 (cart add/remove), FR-010 (persistence), FR-011 (summary with totals)

---

## Phase 7: User Story 5 - Order Placement & Checkout (P1)

### Goal
Implement complete checkout flow from cart to payment to order confirmation with email notifications.

### User Story
**As a customer, I want to proceed from my cart to checkout, provide shipping/billing information, select payment method, and complete my purchase so I can buy pets.**

### Independent Test Criteria
- Checkout initiates from cart
- Shipping address form validates correctly
- Billing address accepts and validates
- Payment method selection works
- Order created with correct totals
- Order confirmation email sent to buyer and seller
- Order appears in buyer and seller order histories
- Inventory reserved on successful order
- Payment failure handled gracefully

### Tasks

#### Backend: Payment & Order Processing (Story 5)

- [ ] T207 [P] Create Order JPA entity (src/main/java/com/petstore/entity/Order.java) per data model
- [ ] T208 [P] Create OrderItem JPA entity (src/main/java/com/petstore/entity/OrderItem.java)
- [ ] T209 [P] Create Payment JPA entity (src/main/java/com/petstore/entity/Payment.java)
- [ ] T210 [P] Create ShippingAddress JPA entity (src/main/java/com/petstore/entity/ShippingAddress.java)
- [ ] T211 [P] Create OrderDTO.java, PaymentDTO.java for API responses
- [ ] T212 [P] Create OrderRepository.java, PaymentRepository.java with custom queries
- [ ] T213 Test: Create OrderRepositoryTest.java
- [ ] T214 Create StripePaymentService.java integrating with Stripe API: createPaymentIntent, processPayment, refundPayment
- [ ] T215 Create OrderService.java with: createOrder(userId, cartId, shippingAddressId, paymentMethod), processOrderPayment(orderId, paymentDetails), getOrderHistory(userId), getOrderDetail(orderId)
- [ ] T216 Create OrderValidationService.java validating checkout data (required fields, inventory availability, price consistency)
- [ ] T217 Test: Create OrderServiceTest.java testing order creation, payment processing, validation
- [ ] T218 Create PaymentController.java endpoints: POST /api/orders/checkout (initiate checkout), POST /api/payments (process payment)
- [ ] T219 Create OrderController.java endpoints: GET /api/orders (history), GET /api/orders/{id} (detail), POST /api/orders/{id}/cancel (cancel order)
- [ ] T220 Test: Create PaymentControllerTest.java, OrderControllerTest.java

#### Backend: Email Notifications (Story 5)

- [ ] T221 Create EmailService.java with: sendOrderConfirmation(order), sendShippingNotification(order), sendDeliveryNotification(order)
- [ ] T222 Create EmailTemplateService.java generating HTML email templates
- [ ] T223 Create EmailScheduler.java or async job for sending emails (not blocking checkout)
- [ ] T224 Test: Create EmailServiceTest.java mocking email backend

#### Backend: Inventory Management (Story 5)

- [ ] T225 Create InventoryService.java with: reserveInventory(petId, quantity), releaseInventory(petId, quantity), validateAvailability(petId, quantity)
- [ ] T226 [P] Add inventory transaction logging for audit trail
- [ ] T227 Test: Create InventoryServiceTest.java testing reservations, releases

#### Frontend: Checkout Flow (Story 5)

- [ ] T228 Create CheckoutPage.jsx (src/pages/CheckoutPage.jsx) - multi-step checkout
- [ ] T229 Create CheckoutStep1Shipping.jsx component for shipping address form
- [ ] T230 [P] Create CheckoutStep2Billing.jsx component for billing address form
- [ ] T231 [P] Create CheckoutStep3Payment.jsx component for payment method selection and Stripe integration
- [ ] T232 Create CheckoutStep4Review.jsx component for order review before final submission
- [ ] T233 [P] Create OrderConfirmationPage.jsx (src/pages/OrderConfirmationPage.jsx) - post-purchase confirmation
- [ ] T234 [P] Create AddressForm.jsx reusable component for shipping/billing addresses
- [ ] T235 Test: Create CheckoutPage.test.jsx, AddressForm.test.jsx, OrderConfirmation.test.jsx

#### Frontend: Payment Integration (Story 5)

- [ ] T236 Install Stripe.js and @stripe/react-stripe-js dependencies in package.json
- [ ] T237 Create PaymentForm.jsx component using Stripe Elements for card input
- [ ] T238 Create StripeProvider wrapper for Stripe context
- [ ] T239 Test: Create PaymentForm.test.jsx

#### API Service Layer (Story 5)

- [ ] T240 Create src/services/orderService.js with: initiateCheckout, processPayment, getOrderHistory, getOrderDetail, cancelOrder
- [ ] T241 Create src/services/paymentService.js with: createPaymentIntent, confirmPayment

#### Tests: E2E Checkout & Orders

- [ ] T242 Create Playwright test: tests/e2e/checkout.spec.js - complete checkout flow
- [ ] T243 Create Playwright test: payment success and order confirmation email
- [ ] T244 Create Playwright test: order history and detail viewing
- [ ] T245 Create Playwright test: payment failure handling

**Effort**: 38 tasks | **Duration**: 4-5 days

**Parallel Execution**: T207-T217 backend mostly parallel. T228-T241 frontend mostly parallel. T242-T245 after full integration.

**Functional Requirements Covered**: FR-025 (checkout flow), FR-026 (validation), FR-027 (PCI DSS), FR-028 (inventory reserve), FR-029 (confirmation), FR-030 (status tracking), FR-031 (notifications)

---

## Phase 8: Admin Functions - Inventory & Order Management (P2)

### Goal
Implement admin dashboard for managing pet inventory and fulfilling orders (marking as shipped, updating status, tracking fulfillment).

### User Story
**As an admin, I want to manage the pet inventory and view/fulfill customer orders so I can keep the marketplace current and customers informed.**

### Independent Test Criteria
- Admin can create pet listings
- Admin can update pet details and stock
- Admin can view all orders
- Admin can update order status (pending → processing → shipped → delivered)
- Order status changes trigger customer notifications
- Admin dashboard shows key metrics (total orders, revenue, pending shipments)

### Tasks

#### Backend: Admin Inventory Management (Story 6)

- [ ] T246 [P] Create AdminService.java with admin-specific methods
- [ ] T247 Create InventoryController.java (admin-only endpoints): POST /api/admin/pets, PUT /api/admin/pets/{id}, DELETE /api/admin/pets/{id}, PATCH /api/admin/pets/{id}/stock
- [ ] T248 Test: Create InventoryControllerTest.java

#### Backend: Admin Order Management (Story 7)

- [ ] T249 Create OrderFulfillmentService.java with: updateOrderStatus(orderId, newStatus), markAsShipped(orderId, trackingNumber), markAsDelivered(orderId)
- [ ] T250 Create AdminOrderController.java (admin-only endpoints): GET /api/admin/orders, GET /api/admin/orders/{id}, PATCH /api/admin/orders/{id}/status, POST /api/admin/orders/{id}/ship
- [ ] T251 Test: Create AdminOrderControllerTest.java
- [ ] T252 Create AdminDashboardService.java with: getDashboardStats(), getPendingOrders(), getInventorySummary()
- [ ] T253 Create AdminDashboardController.java endpoint: GET /api/admin/dashboard

#### Frontend: Admin Dashboard (Story 6-7)

- [ ] T254 Create AdminDashboard.jsx (src/pages/AdminDashboard.jsx) - admin landing with key metrics
- [ ] T255 Create AdminInventoryManager.jsx component for pet inventory CRUD
- [ ] T256 Create AdminOrderManager.jsx component for order fulfillment
- [ ] T257 Create OrderStatusUpdater.jsx component for changing order status
- [ ] T258 [P] Create ShippingTracker.jsx component for adding tracking numbers
- [ ] T259 Test: Create AdminDashboard.test.jsx, AdminInventoryManager.test.jsx, AdminOrderManager.test.jsx

#### API Service Layer (Story 6-7)

- [ ] T260 Create src/services/adminService.js with: createPet, updatePet, deletePet, listPets, listOrders, updateOrderStatus

**Effort**: 15 tasks | **Duration**: 2 days

**Functional Requirements Covered**: FR-032 (admin dashboard), FR-033 (inventory), FR-034 (order management), FR-035 (seller verification)

---

## Phase 9: Reviews, Ratings & Wishlist Features (P3)

### Goal
Implement customer reviews and ratings for pets/sellers, and wishlist/favorites functionality.

### User Story 8 (P3)
**As a customer, I want to leave reviews and ratings for pets I've purchased so I can help other customers make informed decisions.**

### User Story 9 (P3)
**As a customer, I want to save favorite pets to a wishlist so I can revisit them later and track pets I'm interested in.**

### Tasks

#### Backend: Review System (Story 8)

- [ ] T261 [P] Create Review JPA entity (src/main/java/com/petstore/entity/Review.java)
- [ ] T262 [P] Create ReviewDTO.java
- [ ] T263 [P] Create ReviewRepository.java
- [ ] T264 Create ReviewService.java with: createReview(userId, petId, rating, text), getReviews(petId), getAverageRating(petId), updateReview(reviewId, updates), deleteReview(reviewId)
- [ ] T265 Test: Create ReviewServiceTest.java
- [ ] T266 Create ReviewController.java endpoints: POST /api/reviews (create review), GET /api/pets/{petId}/reviews (list reviews), PUT /api/reviews/{id}, DELETE /api/reviews/{id}
- [ ] T267 Test: Create ReviewControllerTest.java

#### Backend: Seller Reviews (Story 8)

- [ ] T268 [P] Create SellerReview JPA entity (src/main/java/com/petstore/entity/SellerReview.java)
- [ ] T269 Create SellerReviewService.java with: createSellerReview(userId, sellerId, rating, feedback), updateSellerRating(sellerId)
- [ ] T270 Test: Create SellerReviewServiceTest.java

#### Backend: Wishlist (Story 9)

- [ ] T271 [P] Create Wishlist JPA entity (src/main/java/com/petstore/entity/Wishlist.java)
- [ ] T272 [P] Create WishlistItem JPA entity (src/main/java/com/petstore/entity/WishlistItem.java)
- [ ] T273 [P] Create WishlistDTO.java, WishlistItemDTO.java
- [ ] T274 [P] Create WishlistRepository.java, WishlistItemRepository.java
- [ ] T275 Create WishlistService.java with: getWishlist(userId), addItem(userId, petId), removeItem(userId, petId), isInWishlist(userId, petId)
- [ ] T276 Test: Create WishlistServiceTest.java
- [ ] T277 Create WishlistController.java endpoints: GET /api/wishlists, POST /api/wishlists/items, DELETE /api/wishlists/items/{id}
- [ ] T278 Test: Create WishlistControllerTest.java

#### Frontend: Review Components (Story 8)

- [ ] T279 [P] Create ReviewForm.jsx component for submitting reviews
- [ ] T280 [P] Create ReviewList.jsx component displaying reviews with star ratings
- [ ] T281 [P] Create ReviewCard.jsx component for individual review display
- [ ] T282 Create ReviewsSection.jsx (integrated into PetDetailPage)
- [ ] T283 Test: Create ReviewForm.test.jsx, ReviewList.test.jsx

#### Frontend: Wishlist Components (Story 9)

- [ ] T284 [P] Create WishlistPage.jsx (src/pages/WishlistPage.jsx) - saved favorites
- [ ] T285 [P] Create WishlistItem.jsx component for wishlist entries
- [ ] T286 [P] Create AddToWishlistButton.jsx component (integrated into PetCard)
- [ ] T287 Test: Create WishlistPage.test.jsx

#### API Service Layer (Story 8-9)

- [ ] T288 Create src/services/reviewService.js with: createReview, getReviews, updateReview, deleteReview
- [ ] T289 Create src/services/wishlistService.js with: getWishlist, addItem, removeItem, isInWishlist

**Effort**: 29 tasks | **Duration**: 3 days

**Functional Requirements Covered**: FR-036 (reviews), FR-037 (seller ratings), FR-038 (pet ratings), FR-039 (wishlist), FR-040 (wishlist access)

---

## Phase 10: Revenue & Billing - Commission & Seller Payouts (P2)

### Goal
Implement platform commission calculation, seller earnings tracking, and payout processing.

### Tasks

#### Backend: Commission & Earnings Tracking

- [ ] T290 [P] Create SellerEarnings JPA entity (src/main/java/com/petstore/entity/SellerEarnings.java)
- [ ] T291 [P] Create Payout JPA entity (src/main/java/com/petstore/entity/Payout.java)
- [ ] T292 [P] Create CommissionService.java with: calculateCommission(orderAmount, commissionRate), recordEarnings(order)
- [ ] T293 [P] Create PayoutService.java with: calculateSellerPayout(sellerId, startDate, endDate), processPayout(sellerId), getPayoutHistory(sellerId)
- [ ] T294 Test: Create CommissionServiceTest.java, PayoutServiceTest.java
- [ ] T295 Create EarningsController.java endpoints: GET /api/sellers/me/earnings, GET /api/sellers/me/payouts (seller-only)
- [ ] T296 Test: Create EarningsControllerTest.java

#### Backend: Commission Calculation on Order

- [ ] T297 Update OrderService to: call CommissionService.recordEarnings(order) on successful payment
- [ ] T298 Update Order entity to include: platform_commission, seller_payout fields
- [ ] T299 Test: Verify commission calculations in OrderServiceTest

#### Frontend: Seller Earnings Dashboard

- [ ] T300 Create EarningsWidget.jsx component for seller dashboard showing earnings stats
- [ ] T301 Create EarningsHistory.jsx component displaying transaction history with commissions
- [ ] T302 Create PayoutHistory.jsx component showing past payouts and pending amounts
- [ ] T303 [P] Create PayoutMethodSetup.jsx component for configuring payout method
- [ ] T304 Test: Create EarningsWidget.test.jsx

#### API Service Layer (Story 10)

- [ ] T305 Create src/services/earningsService.js with: getEarnings, getPayoutHistory, setupPayoutMethod

**Effort**: 16 tasks | **Duration**: 2 days

**Functional Requirements Covered**: FR-041 (commission calc), FR-042 (earnings tracking), FR-043 (payouts), FR-044 (dashboard), FR-045 (pricing), FR-046 (payment methods), FR-047 (payout methods)

---

## Phase 11: Security, Compliance & API Hardening

### Goal
Implement rate limiting, security headers, audit logging, and API documentation. Ensure PCI DSS compliance and HTTPS enforcement.

### Tasks

#### Backend: Security Hardening

- [ ] T306 [P] Create RateLimitingInterceptor.java implementing token bucket rate limiting per IP/user
- [ ] T307 [P] Add rate limiting configuration to application.properties (requests per minute, per hour)
- [ ] T308 [P] Create SecurityHeadersConfig.java adding: X-Content-Type-Options, X-Frame-Options, Strict-Transport-Security
- [ ] T309 [P] Create CORSConfig.java with proper origin whitelist (not wildcard in production)
- [ ] T310 [P] Enable HTTPS/TLS: Configure SSL certificate path in application.properties
- [ ] T311 Test: Create RateLimitingInterceptorTest.java
- [ ] T312 Test: Create SecurityHeadersConfigTest.java

#### Backend: API Documentation

- [ ] T313 Create OpenAPI/Swagger annotations on all controllers (already started in Phase 1)
- [ ] T314 Verify Swagger UI accessible at /api/docs and /api/docs.html
- [ ] T315 Create API documentation markdown (docs/API.md) with endpoint reference

#### Backend: Audit Logging

- [ ] T316 Create AuditLog JPA entity (src/main/java/com/petstore/entity/AuditLog.java) for state-changing operations
- [ ] T317 Create AuditAspect.java to auto-log all @Audited methods (orders, payments, seller changes)
- [ ] T318 [P] Create AuditService.java with: logAction(user, action, entity, changes)
- [ ] T319 Test: Create AuditServiceTest.java

#### Backend: Input Validation

- [ ] T320 [P] Enforce all DTO field validations (@Valid, @NotNull, @Email, @Length, etc.)
- [ ] T321 [P] Create ValidationAdvice.java for consistent error responses
- [ ] T322 Test: Create comprehensive validation tests for all DTOs

#### Backend: SQL Injection & XSS Prevention

- [ ] T323 Verify all repositories use JPA @Query with ?1 parameter binding (no string concatenation)
- [ ] T324 [P] Enable Spring Security CSRF protection on non-GET endpoints

#### Frontend: Security

- [ ] T325 [P] Configure Content Security Policy (CSP) header in backend
- [ ] T326 [P] Implement React XSS protection: DOMPurify library for rich content
- [ ] T327 [P] Configure axios interceptor to remove Authorization header for cross-origin requests
- [ ] T328 [P] Validate all form inputs on frontend before submission

#### Frontend: HTTPS Enforcement

- [ ] T329 Configure Vite dev server for localhost development (HTTPS can be enabled with --https flag)
- [ ] T330 Create documentation for HTTPS setup in production

#### Tests: Security Test Suite

- [ ] T331 Create SecurityTest.java with: rate limiting tests, CORS tests, header validation tests
- [ ] T332 Create XSSValidationTest.java testing common XSS attack vectors
- [ ] T333 Create SQLInjectionTest.java verifying parameterized queries

**Effort**: 28 tasks | **Duration**: 3 days

**Functional Requirements Covered**: FR-048 (audit logging), FR-049 (rate limiting), FR-050 (HTTPS), FR-051 (data encryption)

---

## Phase 12: Error Handling, Performance & Polish

### Goal
Comprehensive error handling, performance optimization, cross-cutting concerns refinement.

### Tasks

#### Backend: Error Handling

- [ ] T334 [P] Create ApiErrorResponse.java standard error response format
- [ ] T335 [P] Enhance GlobalExceptionHandler.java with handlers for all exception types
- [ ] T336 [P] Add logging for all exceptions (ERROR level for production-impacting issues)
- [ ] T337 Test: Create GlobalExceptionHandlerTest.java

#### Backend: Performance Optimization

- [ ] T338 [P] Add database query optimization: N+1 query prevention using @EntityGraph or lazy loading
- [ ] T339 [P] Configure caching (Spring Cache abstraction): @Cacheable on frequently accessed queries (featured pets, seller profiles)
- [ ] T340 [P] Add database connection pooling configuration (HikariCP)
- [ ] T341 Test: Create performance benchmarks for slow queries

#### Backend: Pagination & List Performance

- [ ] T342 [P] Verify all list endpoints support limit/offset pagination (no unbounded queries)
- [ ] T343 [P] Add pagination validation (max limit enforced)

#### Frontend: Error Handling

- [ ] T344 [P] Create global error boundary component (src/components/ErrorBoundary.jsx)
- [ ] T345 [P] Create error handling in all API calls (Axios interceptor for 4xx/5xx responses)
- [ ] T346 [P] Create user-friendly error messages for common scenarios (network timeout, server error, validation)
- [ ] T347 Test: Create ErrorBoundary.test.jsx

#### Frontend: Performance

- [ ] T348 [P] Implement React lazy loading for pages (React.lazy + Suspense)
- [ ] T349 [P] Add image optimization: responsive images with srcSet, lazy loading
- [ ] T350 [P] Configure Vite code splitting for smaller bundle sizes

#### Frontend: Accessibility

- [ ] T351 [P] Add ARIA labels to interactive components
- [ ] T352 [P] Verify keyboard navigation (Tab order, focus management)
- [ ] T353 [P] Test color contrast ratios for WCAG AA compliance

#### Frontend: Browser Compatibility

- [ ] T354 [P] Configure Babel for ES5 compatibility (if needed for older browser support)
- [ ] T355 [P] Test in Chrome, Firefox, Safari, Edge (last 2 versions)

#### Backend & Frontend: Documentation

- [ ] T356 Create DEPLOYMENT.md with: build steps, environment variables, Docker deployment
- [ ] T357 Create TROUBLESHOOTING.md with: common issues and solutions
- [ ] T358 [P] Create ADR (Architecture Decision Record) files for key decisions
- [ ] T359 [P] Update inline code comments and Javadoc/JSDoc for clarity

**Effort**: 26 tasks | **Duration**: 2-3 days

**Status**: Polish & refinement phase

---

## Phase 13: E2E Testing & MVP Release Readiness

### Goal
Comprehensive E2E tests covering complete user workflows, deployment validation, and MVP readiness checklist.

### Tasks

#### E2E Test Suite (Playwright)

- [ ] T360 [P] Create tests/e2e/complete-buyer-flow.spec.js: browse → cart → checkout → order confirmation
- [ ] T361 [P] Create tests/e2e/complete-seller-flow.spec.js: become seller → verify email → create listing → view orders
- [ ] T362 [P] Create tests/e2e/user-authentication.spec.js: register → login → profile → logout
- [ ] T363 [P] Create tests/e2e/cart-persistence.spec.js: verify cart survives logout
- [ ] T364 [P] Create tests/e2e/payment-flow.spec.js: checkout → payment → confirmation
- [ ] T365 [P] Create tests/e2e/admin-operations.spec.js: admin inventory and order management
- [ ] T366 [P] Create tests/e2e/reviews-wishlist.spec.js: create reviews, manage wishlist
- [ ] T367 [P] Create tests/e2e/error-scenarios.spec.js: invalid inputs, payment failures, network errors
- [ ] T368 Create tests/e2e/performance.spec.js: page load time validation, response time SLAs

#### Docker & Deployment

- [ ] T369 [P] Create backend Dockerfile optimized for production (multi-stage build, non-root user)
- [ ] T370 [P] Create frontend Dockerfile optimized for production (Node build stage + Nginx serve stage)
- [ ] T371 [P] Create docker-compose.yml for local development (already done in Phase 0, verify it works)
- [ ] T372 Create docker-compose.prod.yml for production deployment (with environment variables, volumes)
- [ ] T373 Test: Create script for end-to-end Docker Compose up/down testing
- [ ] T374 Create .dockerignore files for both backend and frontend

#### Database & Migrations

- [ ] T375 Verify all Flyway migrations execute in order without errors
- [ ] T376 Test: Create migration rollback and forward tests
- [ ] T377 Create backup/restore documentation for production database

#### Release Readiness Checklist

- [ ] T378 [P] Verify test coverage meets target (80%+ for services)
- [ ] T379 [P] Verify all 51 functional requirements mapped to tasks and implemented
- [ ] T380 [P] Verify all API endpoints documented in Swagger
- [ ] T381 [P] Verify all error scenarios handled gracefully
- [ ] T382 [P] Verify performance benchmarks met (catalog <500ms, checkout <5 mins)
- [ ] T383 [P] Verify security checklist: HTTPS, rate limiting, CORS, CSP, XSS prevention
- [ ] T384 [P] Verify code quality: no console errors in dev tools, no lint warnings
- [ ] T385 [P] Verify database schema complete: all 17 entities, all relationships, all indexes
- [ ] T386 Create MVP Release Notes (features, known limitations, post-MVP roadmap)
- [ ] T387 Create Troubleshooting Guide for deployment issues

#### Final Integration & Smoke Tests

- [ ] T388 Test: Run complete docker-compose stack for 30 minutes under load
- [ ] T389 Test: Verify Postgres backup/recovery works
- [ ] T390 Test: Verify all environment variables documented and required

**Effort**: 33 tasks | **Duration**: 2-3 days

**Status**: Release readiness validation

---

## Task Dependency Graph

### Critical Path (Sequential Dependencies)

```
Phase 0 (Setup)
    ↓
Phase 1 (Foundation: Security, Config, DB)
    ↓
Phase 2 (Test Infrastructure)
    ↓
Phase 3 (Pet Catalog - Backend/Frontend/Tests)
    ↓ (parallel with Phase 4-5)
Phase 4 (Seller Account)
Phase 5 (User Account)
Phase 6 (Shopping Cart)
    ↓
Phase 7 (Checkout & Orders)
    ↓
Phase 8 (Admin Functions)
Phase 9 (Reviews & Wishlist)
    ↓
Phase 10 (Revenue & Billing)
    ↓
Phase 11 (Security)
Phase 12 (Polish)
    ↓
Phase 13 (E2E & Release)
```

### Blocking Prerequisites

- **Phase 0** → All other phases (project must exist)
- **Phase 1** → All backend/frontend work (security, DB connection, context setup)
- **Phase 2** → Phase 3+ (tests must be infrastructure must be in place first)
- **Phase 3** → Phase 4+ (catalog needed before seller can list, before cart can add)
- **Phase 4-5** → Phase 6 (user account required for cart, seller account for listings)
- **Phase 6** → Phase 7 (cart must exist before checkout)
- **Phase 7** → Phase 8-10 (orders must work before admin fulfillment, before payout)
- **Phase 11-12** → Phase 13 (polish and security before E2E and release)

### Parallel Opportunities

**Batch 1 (Week 1-2)**:
- Phase 0 setup tasks (all [P])
- Phase 1 backend config tasks (all [P])
- Phase 1 frontend setup tasks (all [P])
- Phase 1 database tasks (sequential but can start early)

**Batch 2 (Week 2-3)**:
- Phase 2 backend tests (all [P])
- Phase 2 frontend tests (all [P])

**Batch 3 (Week 3-5)**:
- Phase 3 backend (pet service)
- Phase 3 frontend (landing page)
- Phase 4 backend (seller service) - parallel with Phase 3
- Phase 4 frontend (seller registration) - parallel with Phase 3
- Phase 5 backend (user service) - parallel with Phase 3-4
- Phase 5 frontend (auth UI) - parallel with Phase 3-4

**Batch 4 (Week 5-7)**:
- Phase 6 cart (backend/frontend parallel)
- Phase 7 checkout (backend/frontend parallel)

**Batch 5 (Week 8-9)**:
- Phase 8 admin (backend/frontend parallel)
- Phase 9 reviews/wishlist (backend/frontend parallel)
- Phase 10 revenue (backend)
- Phase 11 security (backend/frontend mostly parallel)

**Batch 6 (Week 9-10)**:
- Phase 12 polish (backend/frontend mostly parallel)
- Phase 13 E2E tests (parallel)
- Docker validation (parallel with E2E)

---

## Effort Estimates by Phase

| Phase | Tasks | Effort | Duration | Parallelization |
|-------|-------|--------|----------|-----------------|
| 0 | 16 | 16 S-M | 1 day | 80% parallel |
| 1 | 34 | 34 M | 3-4 days | 70% parallel |
| 2 | 20 | 20 M | 2-3 days | 80% parallel |
| 3 | 29 | 29 M-L | 3-4 days | 60% parallel |
| 4 | 44 | 44 M-L | 4-5 days | 50% parallel |
| 5 | 38 | 38 M-L | 3-4 days | 60% parallel (parallel with 4) |
| 6 | 24 | 24 M | 2-3 days | 70% parallel |
| 7 | 38 | 38 L | 4-5 days | 50% parallel |
| 8 | 15 | 15 M | 2 days | 60% parallel |
| 9 | 29 | 29 M | 3 days | 65% parallel |
| 10 | 16 | 16 M | 2 days | 60% parallel |
| 11 | 28 | 28 M-L | 3 days | 70% parallel |
| 12 | 26 | 26 M | 2-3 days | 75% parallel |
| 13 | 33 | 33 M-L | 2-3 days | 90% parallel |
| **TOTAL** | **78** | **78** | **8-10 weeks** | **~65% avg** |

**Effort Estimate Breakdown**:
- XS = 1 hour
- S = 2-4 hours
- M = 4-8 hours (half day)
- L = 8-16 hours (full day)
- XL = 16+ hours (multiple days)

**Duration Calculation**: Most tasks rated M (4-8 hours). With 65% parallelization and team size of 2-3:
- Sequential minimum: ~78 × 6 hours / 8 hours per day = 58 days ÷ 70% parallel efficiency = ~8-10 weeks
- Realistic with 2 developers on some phases: 6-8 weeks
- Realistic with 3 developers on critical phases: 5-6 weeks

---

## Functional Requirement Coverage Matrix

| Phase | Requirements | Count |
|-------|--------------|-------|
| 3 (Landing Page & Catalog) | FR-001 to FR-008 | 8 |
| 4 (Seller Account) | FR-017 to FR-024 | 8 |
| 5 (User Account) | FR-012 to FR-016 | 5 |
| 6 (Shopping Cart) | FR-009 to FR-011 | 3 |
| 7 (Checkout & Orders) | FR-025 to FR-031 | 7 |
| 8 (Admin Functions) | FR-032 to FR-035 | 4 |
| 9 (Reviews & Wishlist) | FR-036 to FR-040 | 5 |
| 10 (Revenue & Billing) | FR-041 to FR-047 | 7 | 11 (Security) | FR-048 to FR-051 | 4 |
| **TOTAL COVERAGE** | **FR-001 to FR-051** | **51** |

All 51 functional requirements are covered across the 13-phase implementation plan. Each requirement is mapped to specific tasks and deliverables.

---

## User Story Prioritization & MVP Scope

### Phase 1: MVP Critical (P1 - User Stories 1-5)

**Priority P1 Stories Complete Core Marketplace**:
1. User Story 1: Landing Page & Browse Inventory (Phase 3)
2. User Story 2: Seller Account & Listing Management (Phase 4)
3. User Story 3: Shopping Cart (Phase 6)
4. User Story 4: User Account Management (Phase 5)
5. User Story 5: Order Placement & Checkout (Phase 7)

**Estimated Duration**: ~5 weeks (all P1 stories end by end of Phase 7)

**MVP Deliverables**:
- Complete buyer workflow: browse → add to cart → checkout → order
- Complete seller workflow: register → verify → list → fulfill
- User authentication and profiles
- Public pet catalog with discovery
- Shopping cart with persistence
- Payment processing (delegated to Stripe)
- Order confirmation and history

**MVP Success Metrics**:
- All 51 FR partially or fully addressed (majority P1 requirements)
- 5 core user stories complete
- All critical user paths end-to-end testable
- Performance targets met (catalog <500ms, checkout <5 min)
- 80%+ test coverage on critical services

### Phase 2: Post-MVP Enhancement (P2-P3 - User Stories 6-9)

**Priority P2-P3 Stories Add Polish & Engagement**:
6. Admin Inventory Management (Phase 8)
7. Order Fulfillment & Admin (Phase 8)
8. Reviews & Ratings (Phase 9)
9. Wishlist & Favorites (Phase 9)

**Estimated Duration**: ~2 weeks (P2-P3 stories complete by end of Phase 9)

**Post-MVP Features**:
- Admin dashboard for operational control
- Customer reviews and seller ratings
- Wishlist/favorites for repeat visits
- Enhanced seller performance metrics

---

## Execution Recommendations

### Recommended Team Composition

1. **Backend Lead** (1-2 developers):
   - Phases 1-2: Setup, config, test infrastructure
   - Phases 3-5: Service layer (Pet, Seller, User, Cart, Order)
   - Phases 7-11: Checkout, admin, revenue, security

2. **Frontend Lead** (1-2 developers):
   - Phases 1-2: Build setup, context, test infrastructure
   - Phases 3-5: Pages and components for Stories 1-5
   - Phases 7-9: Checkout flow, admin UI, reviews/wishlist

3. **DevOps/QA** (0.5-1 developer):
   - Phases 0-1: Docker setup, CI/CD
   - Phase 13: E2E tests, deployment validation

### Resource Allocation Tips

- **Week 1**: 1 backend + 1 frontend working in parallel on Phase 0-1
- **Week 2-3**: 1 backend (Phase 1 continued) + 1 frontend (Phase 1 continued) + QA starting Phase 2 tests
- **Week 3-5**: 2 backend (Phase 3-4 parallel) + 2 frontend (Phase 3-4 parallel) - maximum parallelization
- **Week 5-7**: Split into Phase 6 (cart) + Phase 7 (checkout) teams, cross-functional pairs
- **Week 8-9**: Reduce to 1 backend + 1 frontend for Phases 8-10 (lower priority)
- **Week 10**: Converge on E2E testing and release validation (full team)

### Risk Mitigation

1. **Payment Integration Risk**: Start Stripe integration early (Phase 7) with mock tests before live keys
2. **Database Migration Risk**: Test Flyway migrations thoroughly (Phase 0) - consider separate migration tool for large datasets
3. **Email Delivery Risk**: Use email mocking in development (Phase 4); test with real service early
4. **Performance Risk**: Monitor N+1 queries early (Phase 3+), add caching proactively
5. **Security Risk**: Security audit checkpoint at end of Phase 11 before release

---

## Testing Strategy (TDD Approach)

### Test Coverage by Phase

| Phase | Unit Tests | Integration Tests | Component Tests | E2E Tests |
|-------|------------|------------------|-----------------|-----------|
| 0-1 | ✓ | ✓ | ✓ | - |
| 2 | ✓✓ | ✓✓ | ✓✓ | - |
| 3-5 | ✓✓ | ✓✓ | ✓✓ | - |
| 6-7 | ✓✓ | ✓✓ | ✓✓ | ✓ |
| 8-11 | ✓✓ | ✓ | ✓✓ | ✓ |
| 12-13 | ✓✓ | ✓✓ | ✓✓ | ✓✓ |

### Coverage Targets

- **Services** (backend business logic): 80%+ coverage required
- **Controllers** (REST endpoints): 70%+ coverage required
- **Repositories** (data access): 75%+ coverage required
- **Components** (React UI): 60%+ coverage required (focus on critical paths)
- **E2E Tests**: 100% of critical user workflows (MVP user stories 1-5)

---

## Known Constraints & Assumptions

### Constraints
1. **PCI DSS Compliance**: Payment processing delegated to Stripe (no direct card handling in backend)
2. **Email Delivery**: Requires SMTP configuration; development uses mock/testing service
3. **Image Storage**: Assumes CDN (S3 or equivalent) for pet images; local uploads stored in development
4. **Database**: PostgreSQL 14+ required; schema managed by Flyway migrations
5. **Rate Limiting**: Requires Redis or in-memory store for token bucket state (v1 uses in-memory)

### Assumptions
1. **Authentication**: JWT tokens with 15-minute access token, refresh token rotation
2. **Seller Verification**: Email-only verification (can upgrade to SMS/ID verification post-MVP)
3. **Inventory Model**: Single unit per pet (quantity = 1 for most pets; can sell multiple breedings)
4. **Commission Rate**: Fixed 15% per order (can become tiered/dynamic post-MVP)
5. **Shipping**: Flat rate $10 or free above threshold (can integrate real shipping APIs post-MVP)
6. **Search**: Keyword search on name/breed/description (can integrate Elasticsearch post-MVP)
7. **Payment Methods**: Stripe card payments + PayPal (additional methods post-MVP)

---

## Post-MVP Roadmap (Not in Scope)

### Phase 14: Advanced Features (Post-MVP)
- Seller tier system (bronze, silver, gold with benefits)
- Advanced analytics for sellers (dashboard with graphs, trends)
- Pet health records and certificates
- Seller API rate limits and custom pricing
- Bulk listing import
- Automated invoice generation

### Phase 15: Scale & Performance
- Elasticsearch integration for advanced search
- Redis caching layer for high-traffic endpoints
- Database sharding by seller/region
- CDN configuration for frontend assets
- Horizontal scaling with load balancer

### Phase 16: Mobile & Apps
- React Native mobile app (iOS + Android)
- Native seller app for quick order processing
- Mobile payment optimizations

### Phase 17: Advanced Marketplace Features
- Offers & negotiation system
- Auction functionality
- Subscription pets (service offerings)
- Marketplace messaging system
- Dispute resolution center

---

## Success Criteria - MVP Release

### Functional Acceptance

- [x] All 51 functional requirements implemented and testable
- [x] All 9 user stories implemented (with P1 fully complete, P2-P3 partial/complete)
- [x] All 17 database entities created and migrated
- [x] All ~40 API endpoints functional and documented
- [x] All critical user workflows end-to-end testable

### Quality Metrics

- [x] 80%+ code coverage on services (backend)
- [x] 70%+ code coverage on controllers (backend)
- [x] 60%+ code coverage on components (frontend)
- [x] Zero critical security vulnerabilities (per OWASP Top 10)
- [x] All API responses documented in OpenAPI/Swagger

### Performance Targets

- [x] Catalog page load time <2 seconds
- [x] API response time <500ms for 95th percentile
- [x] Checkout flow completable in <5 minutes
- [x] Support 100+ concurrent users without degradation
- [x] Support 1000 concurrent shopping carts

### Operational Readiness

- [x] Docker Compose successfully deploys all services
- [x] Database migrations automated and tested
- [x] All environment variables documented in .env.example
- [x] Production deployment guide (DEPLOYMENT.md) complete
- [x] Troubleshooting guide (TROUBLESHOOTING.md) complete
- [x] API documentation accessible at /api/docs
- [x] E2E tests all pass (Playwright test suite)

### User Experience

- [x] Landing page clearly communicates value proposition
- [x] Buyer workflow intuitive (discover → add to cart → checkout)
- [x] Seller workflow straightforward (register → verify → list → fulfill)
- [x] Error messages helpful and actionable
- [x] No console errors or warnings in production build
- [x] Mobile responsive (tested on devices)

---

## Implementation Start Checklist

Before beginning implementation, ensure:

- [ ] All team members have read and understood spec.md and plan.md
- [ ] Development environment set up (IDE, Git, Docker, npm, Maven)
- [ ] Repository cloned and feature branch 001-petstore-core created
- [ ] Database credentials and configuration prepared
- [ ] Stripe and email service credentials obtained (for Phase 7+)
- [ ] AWS S3 or image storage service configured (for Phase 3+)
- [ ] Team communication channels established (Slack, daily standups)
- [ ] Task tracking system set up (GitHub Issues, Jira, or similar)
- [ ] Code review process established
- [ ] CI/CD pipeline configured (GitHub Actions, Jenkins, etc.)

---

## Document Versions & History

| Version | Date | Author | Changes |
|---------|------|--------|---------|
| 1.0 | 2026-05-05 | Generated | Initial comprehensive task breakdown for MVP |

---

## Appendix: Technical References

### Key Files & Paths

**Backend**:
- Main class: `backend/src/main/java/com/petstore/PetstoreApplication.java`
- Configuration: `backend/src/main/resources/application.properties`
- Entities: `backend/src/main/java/com/petstore/entity/`
- Services: `backend/src/main/java/com/petstore/service/`
- Controllers: `backend/src/main/java/com/petstore/controller/`
- Tests: `backend/src/test/java/com/petstore/`
- Migrations: `backend/src/main/resources/db/migration/`

**Frontend**:
- Entry: `frontend/src/main.jsx`
- Root component: `frontend/src/App.jsx`
- Pages: `frontend/src/pages/`
- Components: `frontend/src/components/`
- Services: `frontend/src/services/`
- Contexts: `frontend/src/context/`
- Tests: `frontend/src/__tests__/`
- E2E Tests: `tests/e2e/`

### Technology Versions

- Java 17 LTS
- Spring Boot 3.x
- Spring Security 6.x
- React 18+
- Vite 4+
- PostgreSQL 14+
- JUnit 5
- Jest 29+
- Playwright

### Reference Documentation

- Spring Boot: https://spring.io/projects/spring-boot
- React: https://react.dev
- Vite: https://vitejs.dev
- PostgreSQL: https://www.postgresql.org/docs/14/
- Testcontainers: https://www.testcontainers.org/
- Playwright: https://playwright.dev

---

**End of Document**

Generated for feature branch: `001-petstore-core`  
All tasks are implementable and ready for assignment.  
Estimated total effort: 78 discrete tasks across 13 phases.  
Expected MVP completion: 8-10 weeks with recommended team composition.
