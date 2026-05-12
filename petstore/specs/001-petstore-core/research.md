# Research Findings: Full Stack Petstore Core Platform

**Phase**: Phase 0 - Technical Research & Best Practices  
**Date**: 2026-05-05  
**Status**: Complete - Ready for Phase 1 Design

---

## 1. Spring Boot Security: JWT & OAuth2

### Decision: JWT with Refresh Token Rotation

**Why this approach**: 
- Stateless authentication suitable for distributed systems and horizontal scaling
- JWT tokens contain user roles/permissions, reducing authorization lookups
- Refresh token rotation improves security (short-lived access tokens, rotated refresh tokens)
- Industry standard for REST APIs and SPAs

**Rationale**:
- Spring Security 6.x provides native JWT support
- No session storage required (stateless = horizontally scalable)
- CORS-friendly for SPA frontend
- Supports multi-role authorization (User, Seller, Admin)

**Implementation Details**:
- Access token: 15-minute expiration; refresh token: 7-day expiration
- JWT signing: RS256 (asymmetric) with private/public key pair (for future microservices)
- Token storage (frontend): Access token in memory; refresh token in httpOnly cookie (secure)
- Spring Security configuration: Resource server with JWT validation filter
- Custom UserDetailsService to load permissions from database
- Endpoint protection: @PreAuthorize annotations on controllers for role-based access

**Dependencies**:
- `spring-security-oauth2-resource-server`
- `spring-security-oauth2-jose` (for JWT signing/validation)
- `io.jsonwebtoken:jjwt` (alternative JWT library for additional flexibility)

---

## 2. React State Management: Context API for MVP

### Decision: React Context + localStorage for Cart Persistence

**Why this approach**:
- Context API is built-in, no additional dependencies for MVP
- Suitable for 5-7 context providers (Auth, Cart, User, Notifications, etc.)
- localStorage provides persistence without backend session management
- Upgrade path to Redux/Zustand for future complexity

**Rationale**:
- MVP doesn't require complex global state mutations
- Context API avoids prop drilling while keeping bundle size small (~10KB additional)
- localStorage works offline; rehydration on app load

**Implementation Details**:
- CartContext: useReducer pattern for predictable state updates
  - Actions: ADD_TO_CART, REMOVE_FROM_CART, UPDATE_QUANTITY, CLEAR_CART
  - State: { items: [], subtotal, tax, total }
  - Persistence: JSON.stringify to localStorage; hydrate on mount
- AuthContext: User profile, authentication status, token management
  - Token refresh logic: Axios interceptor catches 401, requests new token via refresh endpoint
- UserContext: Profile data, addresses (could merge with Auth for MVP)

**Testing Strategy**:
- Context providers wrap component trees in test setup
- useContext hooks tested with custom render utilities
- localStorage mock in Jest for deterministic testing

**Future Considerations**:
- If state mutations grow complex (optimistic updates, undo/redo), migrate to Redux
- Consider Zustand for simpler syntax if Redux feels heavyweight
- Evaluate MobX if class-based reactive patterns preferred

---

## 3. PostgreSQL Marketplace Schema Design

### Decision: Single Schema with Seller-Aware Tables

**Why this approach**:
- Single schema simplifies deployment and backup/restore
- Seller_id foreign keys enable row-level security (RLS) in future
- Clearer than separate schemas for MVP
- Scales to 10k+ users and petabytes of data with proper indexing

**Rationale**:
- Seller_id on Pet table identifies multi-seller inventory
- Admin-owned pets: seller_id IS NULL or special admin_seller_id
- Queries filter by seller_id for data isolation
- Indexes on frequently queried columns (seller_id, category, price)

**Schema Highlights**:
- Core entities: User, Seller, Pet, Order, OrderItem, Payment, Review, Cart, CartItem, Wishlist, WishlistItem
- Seller table: Links to User, tracks verification status, ratings, response time
- SellerEarnings: Tracks revenue, commission calculations, payout history
- Payout table: Seller fund transfers, status tracking, completion dates
- Audit logging: Created_at, updated_at on all tables; audit table for sensitive changes

**Indexing Strategy**:
- Primary keys: All entities
- Foreign keys: Indexes on user_id, seller_id, order_id, pet_id
- Query optimization: Indexes on (seller_id, category), (status), (created_at) for filtering/sorting
- Full-text search: GIN index on pet descriptions for efficient search

**Security**:
- Row-level security (RLS) policies: Sellers see only their own data
- Encryption: Passwords hashed in application; sensitive data (PII) encrypted at DB level (optional post-MVP)
- No raw SQL; all queries via Spring Data JPA or parameterized statements

---

## 4. Payment Integration: Stripe/PayPal with Webhook Handlers

### Decision: Stripe as Primary Payment Processor

**Why this approach**:
- PCI-DSS compliance handled by Stripe (avoids storing card data in database)
- Webhook handlers for asynchronous payment events
- Support for multiple payment methods (card, PayPal, Apple Pay)
- Extensive documentation and SDKs

**Rationale**:
- Stripe Elements SDK for secure card tokenization in frontend
- Backend never sees raw card data; only receives Stripe payment intents
- Webhook handlers (server-to-server) confirm payment success/failure
- Idempotency keys prevent duplicate charges on retry

**Implementation Details**:
- Frontend: Stripe Elements on checkout page for card input
  - Card element collects input; Stripe returns token
  - Token sent to backend; backend creates payment intent
- Backend: Stripe Java SDK manages payment lifecycle
  - POST /api/payments/create-intent: Returns client secret
  - POST /api/payments/confirm: Frontend calls with confirmation method
  - Webhook endpoint: Listens for payment_intent.succeeded, payment_intent.payment_failed
- Order status flow:
  - Cart → Pending (user reviews order)
  - Payment intent created → Processing (awaiting confirmation)
  - Webhook confirms → Order confirmed, inventory reserved
  - Webhook failure → Payment failed, order cancelled

**Seller Payouts**:
- SellerEarnings table tracks gross amount, commission, seller net
- Scheduled job (daily/weekly) aggregates seller earnings; creates Payout records
- Stripe Connect (future): Direct transfers to seller bank accounts; platform retains commission

**Webhook Security**:
- Verify webhook signatures using Stripe secret key
- Idempotent webhook handlers (same webhook processed multiple times = same result)
- Dead-letter queue for failed webhook processing

---

## 5. Testing Strategy: Layered Architecture (Unit → Integration → E2E)

### Decision: JUnit 5 + Mockito (backend), Jest + React Testing Library (frontend), Playwright (E2E)

**Why this approach**:
- Layered testing validates each architectural layer independently
- Unit tests: Fast, isolated, high coverage (~80%)
- Integration tests: API contracts, database interactions, cross-layer flows
- E2E tests: Critical user workflows (few, comprehensive, slow but high confidence)

**Backend Testing**:
- Unit tests (Services, Utils):
  - Mock repositories with Mockito
  - Test business logic in isolation
  - Target: 80%+ coverage
  - Example: OrderServiceTest mocks PetRepository, PaymentService; tests order creation, inventory reservation
- Integration tests (Controllers, Repositories, API Contracts):
  - Use TestContainers for PostgreSQL instances
  - @SpringBootTest with WebTestClient
  - Test full request-response cycles
  - Example: OrderControllerIntegrationTest performs POST /api/orders with real DB
  - Contract testing: Verify JSON response matches OpenAPI schema
- Performance tests:
  - Database query benchmarks
  - API response time measurements under load (optional post-MVP)

**Frontend Testing**:
- Unit tests (Components, Utils, Hooks):
  - Jest + React Testing Library
  - Render components; simulate user interactions; assert outcomes
  - Mock API calls with MSW (Mock Service Worker)
  - Example: CartItem component renders item details; click remove → calls onRemove callback
  - Coverage: Functional components, custom hooks, context consumers (~80%)
- Integration tests (Page flows):
  - Test multiple components together
  - Mock API responses with realistic data
  - Example: CartPage → add item → view cart → update quantity → checkout
- E2E tests (Playwright):
  - Test against real backend (staging environment)
  - Critical workflows: User signup → browse → add to cart → checkout → order confirmation
  - Visual regression testing (optional)

**CI/CD Integration**:
- GitHub Actions or similar: Run all test suites on every PR
- Fail build if coverage drops below 80%
- Fail build if E2E tests fail on staging

**Test Data & Fixtures**:
- Seeder scripts: Populate test database with sample pets, sellers, users
- Factory patterns: Create test objects with sensible defaults

---

## 6. Frontend Build & Deployment: Vite for Fast Development, Static SPA for Production

### Decision: Vite for Development; Docker Multi-Stage Build for Production

**Why this approach**:
- Vite: 10-100x faster dev server startup; instant HMR (hot module replacement)
- Production: Pre-built static assets (HTML, JS, CSS) served by lightweight web server
- Reduces deployment complexity; no Node.js runtime needed in production container

**Rationale**:
- Vite uses native ES modules during development (faster rebuilds)
- Production build generates optimized chunks, code splitting, minification
- SPA served via lightweight Nginx container; backend API separate
- Supports environment-based configuration (dev, staging, prod APIs)

**Implementation Details**:
- vite.config.js:
  - Development: Proxy /api to http://backend:8080 (Docker Compose or dev env)
  - Production: Minify, tree-shake, code splitting
  - Entry point: index.html (loads main.jsx)
- Environment variables:
  - VITE_API_BASE_URL injected at build time or runtime via script tag
  - .env.development, .env.production files
- Docker multi-stage build:
  - Stage 1: Node image; npm install, npm run build
  - Stage 2: Nginx image; copy dist/ files; serve static content
  - Stage 2 container size: ~50MB (vs 1GB+ if Node included)

**Deployment**:
- Development: npm run dev (Vite dev server on :5173)
- Production: npm run build → dist/ folder → Docker image → Kubernetes/ECS

**Optimization**:
- Code splitting: Routes loaded on-demand (lazy loading)
- Bundle analysis: Check for large dependencies (Vite plugin)
- Caching: Set Cache-Control headers for static assets

---

## 7. API Design: RESTful with Ownership & Multi-Seller Support

### Decision: Nested RESTful Routes with Query Parameters for Filtering

**Why this approach**:
- Nested routes clarify resource ownership and hierarchy
- Query parameters enable filtering without explosion of endpoints
- Consistent with HTTP/REST best practices
- OpenAPI/Swagger documentation auto-generated from Spring annotations

**Rationale**:
- Sellers own pets; pets belong in orders; resources have clear parent-child relationships
- Multi-seller marketplace requires filtering by seller; nested routes make this intuitive
- Stateless, cacheable, standard HTTP methods (GET, POST, PUT, DELETE)

**API Endpoints (Abbreviated)**:

**Authentication**:
- POST /api/auth/register
- POST /api/auth/login
- POST /api/auth/refresh-token
- POST /api/auth/logout

**Pets & Catalog**:
- GET /api/pets?category=Dogs&price_min=100&price_max=500&sort=price_asc (public, no auth)
- GET /api/pets/search?q=Golden%20Retriever (public)
- GET /api/pets/{petId} (public, shows seller info)
- POST /api/pets (seller creates listing, auth required)
- PUT /api/pets/{petId} (seller updates, ownership check)
- DELETE /api/pets/{petId} (seller archive, ownership check)

**Seller Management**:
- POST /api/sellers/register (customer becomes seller)
- GET /api/sellers/{sellerId} (public profile)
- PUT /api/sellers/{sellerId}/profile (seller updates, ownership check)
- GET /api/sellers/{sellerId}/pets (public, seller's listings)
- GET /api/sellers/{sellerId}/earnings (seller dashboard, ownership check)

**Orders & Checkout**:
- GET /api/carts (customer's cart, auth required)
- POST /api/carts/items (add to cart)
- PUT /api/carts/items/{itemId} (update quantity)
- DELETE /api/carts/items/{itemId} (remove item)
- POST /api/orders (create order from cart, auth required)
- GET /api/orders (customer's orders, auth required)
- GET /api/orders/{orderId} (order details, buyer/seller/admin can view)

**Payments**:
- POST /api/payments/create-intent (Stripe payment intent)
- POST /api/payments/confirm (confirm payment)
- POST /webhooks/stripe (Stripe webhook handler, no auth)

**Reviews**:
- POST /api/pets/{petId}/reviews (leave review, auth required, must be buyer)
- GET /api/pets/{petId}/reviews (public)

**Wishlist**:
- GET /api/wishlist (customer's wishlist, auth required)
- POST /api/wishlist/items (add to wishlist)
- DELETE /api/wishlist/items/{petId} (remove from wishlist)

**Admin** (if needed):
- GET /api/admin/orders (all orders)
- GET /api/admin/sellers (seller verification, earnings)

**Response Format** (JSON):
```json
{
  "data": { "id": 1, "name": "Max", "price": 500, ... },
  "meta": { "timestamp": "2026-05-05T10:00:00Z", "version": "v1" },
  "error": null
}
```

**Error Handling**:
- HTTP status codes: 200 (OK), 201 (created), 400 (bad request), 401 (unauthorized), 403 (forbidden), 404 (not found), 500 (server error)
- Error body: `{ "error": "Pet not found", "code": "NOT_FOUND" }`
- No sensitive info in errors (stack traces only in dev)

---

## Summary: All Research Questions Resolved

| Topic | Decision | Status |
|-------|----------|--------|
| **Auth** | JWT + refresh tokens, Spring Security | ✅ Decided |
| **State Mgmt** | React Context + localStorage | ✅ Decided |
| **Database** | PostgreSQL, single schema, seller-aware | ✅ Decided |
| **Payments** | Stripe as processor, webhooks for async | ✅ Decided |
| **Testing** | Layered: Unit (80%+) → Integration → E2E | ✅ Decided |
| **Build** | Vite dev, Docker multi-stage production | ✅ Decided |
| **API Design** | RESTful, nested routes, query filtering | ✅ Decided |

**Next Phase**: Design data model and API contracts based on these decisions.
