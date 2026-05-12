# Petstore System Architecture

High-level overview of the Petstore MVP system design, data flow, and key architectural patterns.

**Status**: Phase 1 - Foundation  
**Last Updated**: 2026-05-05

## 📊 System Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                     Frontend (React 18 + Vite)                   │
│  ┌─────────────┐ ┌──────────┐ ┌──────────┐ ┌─────────────────┐  │
│  │  Landing    │ │  Browse  │ │  Seller  │ │ Authentication  │  │
│  │  Page       │ │  Pets    │ │ Dashboard│ │    Pages        │  │
│  └─────────────┘ └──────────┘ └──────────┘ └─────────────────┘  │
│                                                                  │
│  State: AuthContext | CartContext | UserContext                │
│  HTTP Client: Axios with JWT interceptors                      │
└────────────────────────┬───────────────────────────────────────┘
                         │ HTTPS/TLS
                         │ REST API
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│                  Backend (Spring Boot 3.x)                       │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │  Spring Security (JWT Auth, CORS, HTTPS)               │   │
│  └──────────────────────────────────────────────────────────┘   │
│  ┌──────────────┐ ┌──────────┐ ┌──────────────────────────┐     │
│  │ Controllers  │ │Services  │ │   Repositories (JPA)    │     │
│  │ (REST)       │ │(Business)│ │ (Hibernate)             │     │
│  └──────────────┘ └──────────┘ └──────────────────────────┘     │
│                                                                  │
│  External: Stripe (Payments) | SendGrid (Email)                │
└────────────────────────┬───────────────────────────────────────┘
                         │ JDBC
                         │ SQL
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│              Database (PostgreSQL 14+)                           │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────────────────┐   │
│  │  Users      │ │ Pets        │ │ Orders                  │   │
│  ├─────────────┤ ├─────────────┤ ├─────────────────────────┤   │
│  │ Sellers     │ │ CartItems   │ │ Payments                │   │
│  └─────────────┘ └─────────────┘ └─────────────────────────┘   │
│                                                                  │
│  Migrations: Flyway | Indexes on FK, status, seller_id          │
└─────────────────────────────────────────────────────────────────┘
```

## 🏗️ Layered Architecture

```
┌─────────────────────────────────────────────┐
│        REST Controllers (HTTP Layer)        │
│  - Handle HTTP requests/responses
│  - Route to services
│  - Error handling
│  - OpenAPI/Swagger documentation
└────────────────────┬────────────────────────┘
                     │ DTOs
                     ▼
┌─────────────────────────────────────────────┐
│        Services (Business Logic Layer)      │
│  - Validate business rules
│  - Coordinate operations
│  - Handle authentication/authorization
│  - Commission calculations
│  - Payout processing
└────────────────────┬────────────────────────┘
                     │ Domain Models
                     ▼
┌─────────────────────────────────────────────┐
│   Repositories (Data Access Layer)          │
│  - Spring Data JPA repositories
│  - Query optimization
│  - Lazy/eager loading strategies
│  - Transaction management
└────────────────────┬────────────────────────┘
                     │ JDBC/SQL
                     ▼
┌─────────────────────────────────────────────┐
│       JPA/Hibernate (ORM Layer)             │
│  - Entity mapping
│  - Relationship management
│  - Lazy loading
└────────────────────┬────────────────────────┘
                     │ SQL
                     ▼
┌─────────────────────────────────────────────┐
│        PostgreSQL Database                  │
│  - ACID transactions
│  - Foreign keys and constraints
│  - Indexes for query optimization
└─────────────────────────────────────────────┘
```

## 🔐 Security Architecture

### Authentication Flow

```
User Credentials (email + password)
           │
           ▼
    Spring Security
    ├─ CustomUserDetailsService
    ├─ PasswordEncoder (bcrypt)
    └─ JwtProvider (token generation)
           │
           ▼
    Access Token (15 min) + Refresh Token (7 days)
           │
           ▼
    Every Request: "Authorization: Bearer <token>"
           │
           ▼
    JWT Validation (RS256)
    ├─ Signature verification
    ├─ Expiration check
    └─ User extraction
           │
           ▼
    @PreAuthorize checks role-based access
```

### Authorization Model

- **ROLE_CUSTOMER**: Can browse, purchase, review
- **ROLE_SELLER**: Can list pets, fulfill orders, manage earnings (after email verification)
- **ROLE_ADMIN**: Can manage users, view analytics (future)

### Data Isolation

```
Each Order/Pet has seller_id → Multi-seller data separation
Query: SELECT * FROM Orders WHERE seller_id = ? AND user_id = ?
```

## 📡 API Design

### RESTful Principles

- **Resource-Oriented**: `/api/pets`, `/api/orders`, `/api/sellers`
- **HTTP Methods**: GET (read), POST (create), PUT (update), DELETE (delete)
- **Status Codes**: 200 (OK), 201 (Created), 400 (Bad Request), 401 (Unauthorized), 404 (Not Found), 500 (Server Error)
- **JSON Responses**: Standardized `{ data, meta, error }` structure

### Nested Resources

```
GET  /api/pets                      # List all pets
GET  /api/pets/:id                  # Get pet details
POST /api/pets/:id/reviews          # Add review to pet
GET  /api/sellers/:id/pets          # Get seller's pets
GET  /api/orders/:id/items          # Get order items
```

### Query Parameters

```
GET /api/pets?category=dogs&price_max=500&sort=price_asc&page=1&limit=20
    ├─ Filtering: category, price_min/max
    ├─ Sorting: sort, order
    └─ Pagination: page, limit
```

## 💳 Payment Flow

```
1. User adds pets to cart
                │
                ▼
2. User proceeds to checkout
                │
                ▼
3. Backend creates Stripe PaymentIntent
   POST /api/payments/create-intent
                │
                ▼
4. Frontend collects card via Stripe Elements
                │
                ▼
5. Frontend confirms payment
   POST /api/payments/confirm
                │
                ▼
6. Backend processes webhook from Stripe
   Stripe → /webhooks/stripe
                │
                ├─ payment_intent.succeeded
                │  ├─ Update order status → PROCESSING
                │  ├─ Create SellerEarnings record
                │  ├─ Reserve inventory
                │  └─ Send confirmation email
                │
                └─ payment_intent.payment_failed
                   ├─ Update order status → FAILED
                   ├─ Restore inventory
                   └─ Send failure notification
```

## 📊 State Management (Frontend)

```
AuthContext
├─ isAuthenticated
├─ user { id, email, first_name, is_seller }
├─ tokens { access_token, refresh_token }
└─ Methods: login(), logout(), updateUser()

CartContext
├─ items [ { pet_id, price, quantity } ]
├─ calculateTotals() → { subtotal, tax, shipping, total }
└─ Methods: addItem(), removeItem(), updateQuantity(), clearCart()

UserContext
├─ profile { address, preferences }
└─ preferences { theme, notifications }
```

## 🗄️ Database Design

**17 Core Entities** with relationships:

```
Users (buyer/seller base)
 ├─ Sellers (business profile)
 │  ├─ Pets (inventory)
 │  ├─ Orders (received)
 │  ├─ SellerEarnings (commissions)
 │  ├─ Payouts (withdrawals)
 │  └─ Reviews (seller feedback)
 │
 ├─ Carts (user shopping)
 │  └─ CartItems (pet + qty)
 │
 ├─ Orders (purchases)
 │  ├─ OrderItems (pet snapshot)
 │  ├─ Payments
 │  ├─ ShippingAddress
 │  └─ Reviews (pet feedback)
 │
 └─ Wishlists (future)
    └─ WishlistItems
```

## 🚀 Scalability Considerations

### Current MVP Design (100+ concurrent users)

- Single PostgreSQL instance
- Stateless JWT auth (no session DB)
- Connection pooling (HikariCP)
- Basic caching (future: Redis)

### Future Scaling

1. **Database**: Read replicas, sharding by seller_id
2. **Caching**: Redis for pet catalog, session cache
3. **API Gateway**: Nginx/Kong for rate limiting, routing
4. **Message Queue**: RabbitMQ/Kafka for async tasks (emails, notifications)
5. **Microservices**: Separate payment, notification, analytics services
6. **CDN**: CloudFront/Cloudflare for static assets

## 🔄 Key Workflows

### Buyer Workflow

```
1. Browse public catalog (no auth)
2. Create account / Login
3. Add pets to cart
4. Checkout (shipping address, payment)
5. Stripe processes payment
6. Order created + email confirmation
7. Wait for seller to ship
8. Receive tracking info
9. Review pet + seller
```

### Seller Workflow

```
1. Create account
2. Verify email (link from inbox)
3. Update seller profile
4. List first pet (name, price, images, description)
5. Monitor orders in dashboard
6. Ship order (add tracking)
7. View earnings accumulated
8. Request payout (transferred in 3-5 days)
9. Monitor ratings
```

### Admin Workflow (Future)

```
1. View analytics dashboard
2. Suspend problematic sellers
3. Process disputes
4. Generate reports
5. Manage system settings
```

## 🔍 Monitoring & Observability

### Metrics to Track

- API response times (percentiles: p50, p95, p99)
- Database query performance
- Authentication success/failure rates
- Payment success/failure rates
- Seller onboarding funnel
- Cart abandonment rate

### Logging Strategy

- Request/response logging (framework level)
- Application events (user actions, transactions)
- Error tracking with stack traces
- Payment webhook audit trail

### Health Checks

```
GET /actuator/health           # Liveness check
GET /actuator/health/readiness # Readiness (includes DB)
```

## 📈 Performance Optimization

### Database
- Indexes on foreign keys, status fields, seller_id
- Query pagination (max 100 items)
- Lazy loading for relationships

### API
- Response caching headers (ETags, Cache-Control)
- Gzip compression
- JSON response optimization (minimal fields)

### Frontend
- Code splitting (vendor + app chunks)
- Image optimization
- CSS-in-JS or CSS modules
- Service worker for offline support (future)

---

## 🔗 Related Documents

- [DATABASE.md](./DATABASE.md) - Detailed schema and migrations
- [DEPLOYMENT.md](./DEPLOYMENT.md) - Production architecture
- [ADR-003: Database Schema](./adr/ADR-003-database-schema.md)
- [ADR-004: API Design](./adr/ADR-004-api-design.md)

---

**Last Updated**: 2026-05-05  
**Architecture Version**: 1.0.0 (MVP)
