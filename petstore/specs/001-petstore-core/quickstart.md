# Petstore MVP: Quick Start Guide

**Status**: Phase 1 Complete  
**Last Updated**: 2026-05-05

---

## Prerequisites

Ensure you have the following installed:

- **Git**: v2.30+
- **Docker**: v24.0+ with Docker Compose
- **Node.js**: v18 LTS or v20+
- **Java**: 17 LTS (e.g., OpenJDK 17 or Eclipse Temurin)
- **Maven**: v3.8+
- **PostgreSQL**: v14+ (for local development without Docker; optional if using Docker)
- **curl** or **Postman**: For testing API endpoints

---

## Project Structure Overview

```
petstore/
├── backend/                    # Spring Boot 3.x Java backend
│   ├── pom.xml
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/petstore/
│   │   │   │   ├── config/              # Spring security, database, etc.
│   │   │   │   ├── entity/              # JPA entities (User, Pet, Order, etc.)
│   │   │   │   ├── dto/                 # Data transfer objects
│   │   │   │   ├── repository/          # Spring Data JPA repos
│   │   │   │   ├── service/             # Business logic
│   │   │   │   ├── controller/          # REST endpoints
│   │   │   │   └── PetstoreApplication.java
│   │   │   └── resources/
│   │   │       ├── application.properties
│   │   │       └── db/migration/        # Flyway SQL migrations
│   │   └── test/                        # JUnit + Mockito tests
│   └── Dockerfile
├── frontend/                   # React 18 + Vite SPA
│   ├── package.json
│   ├── vite.config.js
│   ├── jest.config.js
│   ├── .eslintrc.js
│   ├── src/
│   │   ├── main.jsx
│   │   ├── App.jsx
│   │   ├── pages/               # Page components
│   │   ├── components/          # Reusable UI components
│   │   ├── services/            # API clients
│   │   ├── context/             # React Context for state
│   │   ├── hooks/               # Custom React hooks
│   │   ├── styles/              # CSS/SCSS
│   │   └── __tests__/           # Jest tests
│   ├── Dockerfile
│   └── .dockerignore
├── docs/                        # Documentation
│   ├── README.md
│   ├── SETUP.md                 # This file
│   ├── API.md                   # API reference
│   ├── ARCHITECTURE.md
│   ├── DATABASE.md
│   ├── DEPLOYMENT.md
│   └── adr/                     # Architecture Decision Records
├── docker-compose.yml          # Development environment orchestration
├── Makefile                     # Convenience commands
├── .env.example                 # Environment template
└── .github/
    └── workflows/              # CI/CD pipelines
```

---

## Local Development Setup

### Step 1: Clone Repository

```bash
git clone https://github.com/yourorg/petstore.git
cd petstore
```

### Step 2: Configure Environment

Copy the example environment file and customize:

```bash
cp .env.example .env
```

Edit `.env` with your settings (database credentials, API keys, etc.):

```env
# Backend
JAVA_OPTS=-Xmx512m
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/petstore_dev
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres_password
JWT_SECRET_KEY=your-secret-key-here
STRIPE_PUBLIC_KEY=pk_test_xxx
STRIPE_SECRET_KEY=sk_test_xxx

# Frontend
VITE_API_BASE_URL=http://localhost:8080
VITE_STRIPE_PUBLIC_KEY=pk_test_xxx

# Database
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres_password
POSTGRES_DB=petstore_dev
```

### Step 3: Start Development Environment

Start all services using Docker Compose:

```bash
docker-compose up --build
```

This starts:
- **PostgreSQL** database on `localhost:5432`
- **Backend** Spring Boot server on `http://localhost:8080`
- **Frontend** Vite dev server on `http://localhost:5173`

Verify services are healthy:

```bash
docker-compose ps
```

### Step 4: Initialize Database

Flyway automatically runs migrations on backend startup. To manually run migrations:

```bash
# Via docker-compose
docker-compose exec backend mvn flyway:migrate

# Or via docker
docker exec petstore-backend mvn flyway:info
```

### Step 5: Seed Database (Optional)

Create sample data for testing:

```bash
# Option 1: Run seed script via Docker
docker-compose exec backend java -cp target/classes com.petstore.util.DatabaseSeeder

# Option 2: Using curl to create sample data
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "seller@example.com",
    "password": "SecurePass123!",
    "first_name": "Jane",
    "last_name": "Smith",
    "phone": "+1-555-987-6543"
  }'
```

---

## Running Services Locally (Without Docker)

### Backend (Spring Boot)

```bash
cd backend

# Install dependencies
mvn clean install

# Run application
mvn spring-boot:run

# Backend runs on http://localhost:8080
```

Configure `backend/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/petstore_dev
spring.datasource.username=postgres
spring.datasource.password=postgres_password
spring.jpa.hibernate.ddl-auto=validate
spring.flyway.enabled=true
```

### Frontend (React + Vite)

```bash
cd frontend

# Install dependencies
npm install

# Start dev server
npm run dev

# Frontend runs on http://localhost:5173
# API requests proxy to http://localhost:8080
```

Frontend automatically proxies API requests via `vite.config.js`:

```javascript
proxy: {
  '/api': {
    target: 'http://localhost:8080',
    changeOrigin: true,
    secure: false
  }
}
```

---

## Testing APIs

### Via curl

**Register a user**:
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "SecurePass123!",
    "first_name": "John",
    "last_name": "Doe",
    "phone": "+1-555-123-4567"
  }'
```

**Login**:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "SecurePass123!"
  }'
```

**Browse pets** (public, no auth):
```bash
curl "http://localhost:8080/api/pets?category=dogs&price_max=500&sort=price_asc" \
  -H "Accept: application/json"
```

**Get pet details**:
```bash
curl http://localhost:8080/api/pets/1 \
  -H "Accept: application/json"
```

### Via Postman

1. Import `.postman_collection.json` from project root
2. Use environment variables:
   - `{{base_url}}`: http://localhost:8080
   - `{{access_token}}`: Paste token from login response
3. Execute requests from the collection

### Running Tests

**Backend unit tests**:
```bash
cd backend
mvn test                          # All tests
mvn test -Dtest=PetServiceTest   # Specific test class
mvn test -DfailIfNoTests=false   # Include integration tests
```

**Backend integration tests** (requires Docker Compose):
```bash
cd backend
mvn verify -P integration-tests   # Runs Testcontainers tests
```

**Frontend unit tests**:
```bash
cd frontend
npm test                          # Run all tests
npm test -- --coverage           # With coverage report
npm test -- --watch              # Watch mode
```

**Frontend E2E tests** (requires backend running):
```bash
cd frontend
npx playwright test               # All tests
npx playwright test pet-listing   # Specific test
npx playwright test --ui          # UI mode
```

---

## Common Development Tasks

### Using the Makefile

```bash
# View available tasks
make help

# Start development environment
make dev

# Stop services
make down

# Run backend tests
make test-backend

# Run frontend tests
make test-frontend

# Build production images
make build

# Format code
make format

# Run linters
make lint
```

### Debugging Backend

Enable debug mode in IDE (VS Code, IntelliJ):

```bash
# Set JAVA_DEBUG_PORT environment variable
export JAVA_DEBUG_PORT=5005

# Or in docker-compose.override.yml
environment:
  JAVA_TOOL_OPTIONS: "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"
```

Then attach debugger to `localhost:5005`.

### Debugging Frontend

Frontend debugger built into browser DevTools (F12):

1. Open `http://localhost:5173` in Chrome
2. Open DevTools (F12)
3. Set breakpoints in **Sources** tab
4. Step through code

Or use VS Code:

```json
// .vscode/launch.json
{
  "version": "0.2.0",
  "configurations": [
    {
      "type": "chrome",
      "request": "attach",
      "name": "Attach Chrome",
      "port": 9222,
      "pathMapping": {
        "/": "${workspaceRoot}/frontend/src/",
        "/src/": "${workspaceRoot}/frontend/src/"
      }
    }
  ]
}
```

Then run Chrome with debugging port:

```bash
google-chrome --remote-debugging-port=9222
```

---

## Troubleshooting

### Port Already in Use

```bash
# Find process using port 8080
lsof -i :8080

# Kill process
kill -9 <PID>

# Or change ports in docker-compose.yml and .env
```

### Database Connection Errors

```bash
# Check PostgreSQL is running
docker-compose ps postgres

# View logs
docker-compose logs postgres

# Reset database
docker-compose down -v              # Remove volumes
docker-compose up --build           # Recreate from scratch
```

### Frontend Not Connecting to Backend

```bash
# Check VITE_API_BASE_URL is correct in frontend .env
echo $VITE_API_BASE_URL

# Verify backend is running
curl http://localhost:8080/actuator/health

# Check Vite proxy configuration in vite.config.js
```

### Maven Build Failures

```bash
# Clear Maven cache
rm -rf ~/.m2/repository
mvn clean install

# Update dependencies
mvn dependency:resolve-plugins

# Force rebuild
mvn clean -U install
```

---

## Next Steps

After setup:

1. **Review Architecture**: Read [ARCHITECTURE.md](./ARCHITECTURE.md)
2. **Understand Database Schema**: Read [DATABASE.md](./DATABASE.md)
3. **API Integration**: Review [API.md](./API.md) for endpoint documentation
4. **Start Contributing**: Pick an issue from `tasks.md` or GitHub Issues
5. **Read ADRs**: Review [adr/](./adr/) for architectural decisions

---

## Support

- **Issues**: Report bugs in GitHub Issues
- **Discussions**: Ask questions in GitHub Discussions
- **Docs**: Full documentation in `docs/` directory
- **Slack**: Join our Slack workspace (link in README)

---

## Environment Details

### Development Stack

- **Backend**: Spring Boot 3.2.x (Java 17), Spring Data JPA, Spring Security 6.x
- **Frontend**: React 18.2+, Vite 4.3+, Axios, React Router 6
- **Database**: PostgreSQL 14+ with Flyway migrations
- **Testing**: JUnit 5, Mockito, Testcontainers (backend); Jest 29, React Testing Library, Playwright (frontend)
- **Build**: Maven (backend), npm/webpack (frontend)
- **Container**: Docker & Docker Compose for local dev and deployment

### Key Configuration Files

| File | Purpose |
|------|---------|
| `docker-compose.yml` | Local dev environment orchestration |
| `.env` | Environment variables (create from `.env.example`) |
| `backend/pom.xml` | Maven dependencies and build config |
| `backend/src/main/resources/application.properties` | Spring Boot config |
| `frontend/package.json` | npm dependencies |
| `frontend/vite.config.js` | Vite build and dev server config |
| `frontend/jest.config.js` | Jest testing config |

---

## Performance Notes

- **Frontend**: Vite dev server rebuilds in <100ms with HMR
- **Backend**: Spring Boot startup ~5-10 seconds
- **Database**: Migrations run on startup (~2 seconds for empty DB)
- **Typical Page Load**: <2 seconds for local development

For production performance details, see [DEPLOYMENT.md](./DEPLOYMENT.md).
