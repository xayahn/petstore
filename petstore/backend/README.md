# Petstore Backend - Spring Boot 3.x

REST API for the Petstore peer-to-peer pet marketplace. Built with Spring Boot 3.2+, Spring Security 6.x, and PostgreSQL.

## 🚀 Quick Start

### Prerequisites
- **Java**: 17 LTS (OpenJDK or Eclipse Temurin)
- **Maven**: 3.8+
- **PostgreSQL**: 14+ (or via Docker)

### Build & Run

```bash
# Clone and navigate to backend directory
cd backend

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run

# The API will be available at http://localhost:8080
```

### Docker

```bash
# Build Docker image
docker build -t petstore-backend:latest .

# Run container
docker run -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/petstore_dev \
           -e SPRING_DATASOURCE_USERNAME=postgres \
           -e SPRING_DATASOURCE_PASSWORD=postgres_password \
           -p 8080:8080 \
           petstore-backend:latest
```

## 📁 Project Structure

The backend uses a **layered architecture** with all code organized under the `layug` root package:

```
backend/
├── pom.xml                           # Maven configuration and dependencies
├── PACKAGE_STRUCTURE.md              # Detailed package documentation
├── src/
│   ├── main/
│   │   ├── java/layug/
│   │   │   ├── PetstoreApplication.java    # Spring Boot entry point
│   │   │   ├── rest/                 # REST Controllers (API Layer)
│   │   │   │   ├── PetController.java
│   │   │   │   ├── OrderController.java
│   │   │   │   ├── AuthController.java
│   │   │   │   └── ...
│   │   │   ├── service/              # Business Logic (Service Layer)
│   │   │   │   ├── PetService.java
│   │   │   │   ├── OrderService.java
│   │   │   │   ├── AuthService.java
│   │   │   │   └── ...
│   │   │   ├── repository/           # Data Access (JPA Layer)
│   │   │   │   ├── PetRepository.java
│   │   │   │   ├── OrderRepository.java
│   │   │   │   └── ...
│   │   │   ├── entity/               # JPA Entity Models
│   │   │   │   ├── User.java
│   │   │   │   ├── Pet.java
│   │   │   │   ├── Order.java
│   │   │   │   └── ...
│   │   │   ├── dto/                  # Request/Response DTOs
│   │   │   │   ├── request/
│   │   │   │   └── response/
│   │   │   ├── config/               # Spring Configuration
│   │   │   ├── security/             # JWT & Spring Security
│   │   │   ├── exception/            # Custom Exceptions & Handler
│   │   │   └── util/                 # Utility Classes
│   │   └── resources/
│   │       ├── application.properties          # Spring Boot config
│   │       └── db/migration/         # Flyway SQL migrations
│   └── test/
│       └── java/layug/              # JUnit 5 + Mockito tests
├── Dockerfile                        # Multi-stage Docker build
└── README.md
```

**See [PACKAGE_STRUCTURE.md](./PACKAGE_STRUCTURE.md) for detailed layer responsibilities and design patterns.**

## 🔧 Configuration

### Environment Variables

Key configuration via environment variables (see `.env.example` in project root):

```bash
# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/petstore_dev
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres_password

# JWT
JWT_SECRET_KEY=your-super-secret-key-min-256-bits
JWT_ACCESS_TOKEN_EXPIRATION=900000        # 15 minutes (ms)
JWT_REFRESH_TOKEN_EXPIRATION=604800000    # 7 days (ms)

# Stripe
STRIPE_SECRET_KEY=sk_test_xxx
STRIPE_WEBHOOK_SECRET=whsec_xxx

# CORS
CORS_ALLOWED_ORIGINS=http://localhost:5173,http://localhost:3000
```

### Application Profiles

```bash
# Development (default)
mvn spring-boot:run

# Production
mvn spring-boot:run -Dspring-boot.run.arguments='--spring.profiles.active=prod'

# Testing
mvn test
```

## 🧪 Testing

### Unit Tests

```bash
# Run all unit tests
mvn test

# Run specific test class
mvn test -Dtest=UserServiceTest

# Run tests with coverage
mvn jacoco:report
```

### Integration Tests

```bash
# Run integration tests with Testcontainers
mvn verify -P integration-tests

# Run specific integration test
mvn verify -Dtest=AuthControllerIntegrationTest
```

## 📚 API Documentation

### Swagger/OpenAPI

Access API documentation and try endpoints:

```
GET /swagger-ui.html        # Swagger UI
GET /api/docs               # OpenAPI JSON
```

### Key API Endpoints

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/auth/register` | Register new user | ❌ |
| POST | `/api/auth/login` | Login user | ❌ |
| GET | `/api/pets` | Browse pets (public catalog) | ❌ |
| GET | `/api/pets/{id}` | Get pet details | ❌ |
| POST | `/api/pets` | Create pet listing | ✅ Seller |
| GET | `/api/carts` | Get current cart | ✅ |
| POST | `/api/orders` | Create order from cart | ✅ |
| GET | `/api/sellers/me/earnings` | View seller earnings | ✅ Seller |

See [API.md](../docs/API.md) for complete endpoint documentation.

## 🏗️ Architecture

### Layered Architecture

```
┌─────────────────────────────────┐
│       REST Controllers          │  HTTP endpoints
├─────────────────────────────────┤
│        Services (DTOs)          │  Business logic, validation
├─────────────────────────────────┤
│        Repositories             │  JPA, data access
├─────────────────────────────────┤
│    JPA Entities & Database      │  PostgreSQL persistence
└─────────────────────────────────┘
```

### Security Flow

```
HTTP Request
    ↓
Spring Security Filter Chain
    ↓
JWT Validation (JwtProvider)
    ↓
UserDetailsService (load user roles)
    ↓
@PreAuthorize checks
    ↓
Controller endpoint
    ↓
Service business logic
    ↓
Repository DB access
```

## 🔒 Security

### Authentication
- **Method**: JWT (JSON Web Tokens) with RS256 signing
- **Token Lifetime**: 
  - Access token: 15 minutes
  - Refresh token: 7 days (single-use, rotated on refresh)
- **Storage**: RefreshToken stored in httpOnly cookie (XSS protection)
- **Format**: `Authorization: Bearer <access_token>`

### Authorization
- **Roles**: ROLE_CUSTOMER, ROLE_SELLER, ROLE_ADMIN
- **Seller Verification**: Email verification required before listing pets
- **Data Isolation**: seller_id foreign key ensures multi-seller data separation

### Password Security
- **Hashing**: bcrypt with cost factor 12+
- **Salting**: Automatic per Spring Security
- **Validation**: Min 8 chars, uppercase, lowercase, digit, special char

## 🐛 Debugging

### Enable Debug Logging

```bash
# Set debug environment variable
export LOGGING_LEVEL_COM_PETSTORE=DEBUG
export LOGGING_LEVEL_ORG_SPRINGFRAMEWORK_SECURITY=DEBUG

mvn spring-boot:run
```

### Debug in IDE

**IntelliJ IDEA**:
1. Run → Edit Configurations
2. Create new Spring Boot configuration
3. Click Debug button

**VS Code**:
```json
{
  "configurations": [
    {
      "type": "java",
      "name": "Spring Boot App",
      "request": "launch",
      "mainClass": "com.petstore.PetstoreApplication"
    }
  ]
}
```

## 📊 Performance

**Target Metrics**:
- API Response: <500ms (95th percentile)
- Database Query: <50ms average
- Concurrent Users: 100+ simultaneous
- Peak Throughput: 1000+ carts/minute

**Optimization Tips**:
- Lazy loading for relationships
- Query pagination (default 20 items, max 100)
- Database indexes on foreign keys and filters
- Caching for frequently accessed data (future)

## 🚀 Deployment

### Docker Build

```bash
# Development build
docker build -t petstore-backend:dev .

# Production build with optimization
docker build --target runtime -t petstore-backend:prod .
```

### Health Checks

```bash
# Liveness probe
curl http://localhost:8080/actuator/health

# Readiness probe (includes DB connection)
curl http://localhost:8080/actuator/health/readiness
```

## 🔧 Dependencies

### Key Dependencies
- Spring Boot 3.2.0
- Spring Security 6.x
- Spring Data JPA + Hibernate
- PostgreSQL Driver 42.6+
- JWT (io.jsonwebtoken) 0.12+
- Stripe SDK 22.x
- SpringDoc OpenAPI 2.x
- Flyway 9.x

### Testing Dependencies
- JUnit 5 (Jupiter)
- Mockito 5.x
- Testcontainers 1.17+
- Spring Boot Test

## 📝 Contributing

1. Follow Java conventions (CamelCase for classes/methods)
2. Add unit tests for all services
3. Use @PreAuthorize for endpoint security
4. Document API endpoints with @Operation and @Parameter
5. Run `mvn clean test` before pushing

## 🆘 Troubleshooting

**Database Connection Error**
```
Check SPRING_DATASOURCE_URL, username, password in logs
mvn spring-boot:run -X (verbose logging)
```

**JWT Token Validation Failed**
```
Ensure JWT_SECRET_KEY is set correctly and consistent
```

**Port 8080 Already in Use**
```
Change server.port in application.properties or set port via env var
```

See main [README.md](../README.md) for more help.

## 📞 Contact

Backend Issues: GitHub Issues
Slack: #backend-development

---

**Last Updated**: 2026-05-05  
**Java Version**: 17 LTS  
**Spring Boot Version**: 3.2.0
