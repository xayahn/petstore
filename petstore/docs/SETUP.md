# Petstore Local Development Setup

Complete guide for setting up the Petstore development environment on your local machine.

**Last Updated**: 2026-05-05  
**Platform Support**: macOS, Linux, Windows (WSL2)

## 📋 Prerequisites

Before starting, ensure you have:

- **Git**: Version control
- **Docker & Docker Compose**: Containerization (easiest path)
- **Node.js**: Version 18+ (for frontend development)
- **Java**: JDK 17 LTS (for backend development)
- **PostgreSQL CLI**: `psql` for database management (optional, Docker provides this)
- **Make**: Build automation (optional, but recommended)

### System Requirements

- **RAM**: 8GB minimum (16GB recommended for smooth development)
- **Disk Space**: 10GB for all dependencies and containers
- **Processor**: Dual-core minimum (quad-core recommended)

## 🚀 Quick Start (Docker Compose)

### Fastest Path: Everything in Docker

```bash
# 1. Clone repository
git clone https://github.com/yourorg/petstore.git
cd petstore

# 2. Setup environment
cp .env.example .env
# Edit .env if needed (defaults work for local dev)

# 3. Build and start all services
docker-compose up -d

# 4. Wait for services to be healthy
docker-compose ps
# All services should show "healthy" or "running"

# 5. View logs
docker-compose logs -f

# 6. Access the application
# Frontend: http://localhost:5173 (dev server with HMR)
# Backend API: http://localhost:8080
# Swagger UI: http://localhost:8080/swagger-ui.html
# Postgres: localhost:5432 (pgAdmin or psql)
```

### Verify Installation

```bash
# Check all services are running
docker-compose ps

# Test backend
curl http://localhost:8080/actuator/health

# Test frontend (should return HTML)
curl http://localhost:5173
```

## 🛠️ Local Development Setup

### Option A: Backend Only (macOS/Linux/WSL2)

```bash
# 1. Install Java 17
# macOS:
brew install openjdk@17

# Linux (Ubuntu):
sudo apt-get install openjdk-17-jdk

# Windows (WSL2):
sudo apt-get install openjdk-17-jdk

# 2. Install Maven
# macOS:
brew install maven

# Linux/WSL2:
sudo apt-get install maven

# 3. Start PostgreSQL with Docker
docker-compose up postgres -d

# 4. Build backend
cd backend
mvn clean install

# 5. Run backend
mvn spring-boot:run
# Backend runs on http://localhost:8080
```

### Option B: Frontend Only

```bash
# 1. Install Node.js 18+
# macOS:
brew install node

# Linux/WSL2:
curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.0/install.sh | bash
nvm install 18

# Windows:
# Download from https://nodejs.org/

# 2. Start backend (Docker or local)
# Ensure backend is running on http://localhost:8080

# 3. Install frontend dependencies
cd frontend
npm install

# 4. Start dev server
npm run dev
# Frontend runs on http://localhost:5173 with HMR
```

### Option C: Both Locally

```bash
# Terminal 1: Backend
cd backend
mvn spring-boot:run

# Terminal 2: Frontend
cd frontend
npm run dev

# Terminal 3: PostgreSQL
docker-compose up postgres

# In a .env file or shell, ensure:
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/petstore_dev
VITE_API_BASE_URL=http://localhost:8080
```

## 📦 Environment Configuration

### .env File

Copy `.env.example` to `.env` and customize:

```bash
# Database
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres_password
POSTGRES_DB=petstore_dev
DB_PORT=5432

# Backend
BACKEND_PORT=8080
JAVA_OPTS=-Xmx512m -Xms256m

# JWT
JWT_SECRET_KEY=your-secret-key-for-jwt-tokens-min-32-chars-long
JWT_ACCESS_TOKEN_EXPIRATION=900000  # 15 minutes
JWT_REFRESH_TOKEN_EXPIRATION=604800000  # 7 days

# Stripe (get from Stripe Dashboard)
STRIPE_SECRET_KEY=sk_test_...
STRIPE_PUBLIC_KEY=pk_test_...
STRIPE_WEBHOOK_SECRET=whsec_...

# Frontend
FRONTEND_PORT=5173
VITE_API_BASE_URL=http://localhost:8080
VITE_STRIPE_PUBLIC_KEY=pk_test_...

# Email (SendGrid - optional for MVP)
SENDGRID_API_KEY=
SENDGRID_FROM_EMAIL=noreply@petstore.local

# AWS (optional for later)
AWS_REGION=us-east-1
AWS_ACCESS_KEY_ID=
AWS_SECRET_ACCESS_KEY=
```

## 📚 Database Setup

### Automatic Setup (First Run)

```bash
# Docker Compose handles this automatically:
# 1. Flyway migrations run on backend startup
# 2. Initial schema is created
# 3. No manual steps needed
```

### Manual Database Access

```bash
# Connect to PostgreSQL in Docker
docker-compose exec postgres psql -U postgres -d petstore_dev

# Useful commands:
\dt                    # List tables
\d table_name          # Describe table
SELECT * FROM users;   # Query data
\q                     # Quit

# Or use pgAdmin web interface (optional)
# Add connection: localhost:5432, user: postgres, pass: postgres_password
```

### Reset Database

```bash
# WARNING: This deletes all data!

# Option 1: Using Docker Compose
docker-compose down -v  # Remove volumes
docker-compose up postgres  # Recreate database

# Option 2: Using psql
docker-compose exec postgres psql -U postgres -d postgres -c "DROP DATABASE petstore_dev;"
docker-compose exec postgres psql -U postgres -d postgres -c "CREATE DATABASE petstore_dev;"

# Option 3: Using Flyway
cd backend
mvn flyway:clean  # Clean migrations
mvn flyway:migrate  # Re-run all migrations
```

## 🧪 Running Tests

### Backend Tests

```bash
# All tests (unit + integration)
cd backend
mvn test

# Unit tests only
mvn test -P unit

# Integration tests only (requires Docker)
mvn test -P integration

# Specific test class
mvn test -Dtest=UserRepositoryTest

# With coverage
mvn test jacoco:report
# View report: target/site/jacoco/index.html
```

### Frontend Tests

```bash
# Unit tests
cd frontend
npm test

# Watch mode
npm run test:watch

# Coverage
npm run test:coverage

# E2E tests
npm run e2e

# Specific test file
npm test -- CartContext.test.js
```

## 🔧 Development Commands

### Using Make (macOS/Linux)

```bash
# Show all available commands
make help

# Setup (first time)
make setup

# Start development
make up

# View logs
make logs

# Stop services
make down

# Run tests
make test

# Format code
make format

# Lint code
make lint

# Clean artifacts
make clean

# Seed database
make seed
```

### Using NPM/Maven Directly

```bash
# Frontend
cd frontend
npm run dev          # Start dev server
npm run build        # Build for production
npm run lint         # Lint code
npm test            # Run tests
npm run e2e         # Run E2E tests

# Backend
cd backend
mvn spring-boot:run                    # Start
mvn test                               # Run tests
mvn clean package                      # Build JAR
mvn dependency:tree                    # Show dependencies
mvn spotless:apply                     # Format code
```

## 🐛 Debugging

### Backend Debugging

```bash
# Enable debug logging
export LOG_LEVEL=DEBUG
mvn spring-boot:run

# Remote debugging with IDE
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5005"
# Then attach debugger to localhost:5005
```

### Frontend Debugging

```bash
# Vue/React DevTools in browser
# Install browser extension
# Open DevTools (F12) → Components/Profiler tab

# Debug in VS Code
# Install Debugger for Chrome/Edge
# Create .vscode/launch.json:
{
  "version": "0.2.0",
  "configurations": [
    {
      "type": "chrome",
      "request": "launch",
      "name": "Launch Chrome",
      "url": "http://localhost:5173",
      "webRoot": "${workspaceFolder}/frontend/src"
    }
  ]
}
# Press F5 to start debugging
```

### Database Debugging

```bash
# View query execution
docker-compose exec postgres psql -U postgres -d petstore_dev -c "SET log_statement = 'all';"

# Monitor active queries
SELECT * FROM pg_stat_activity;

# Explain query plan
EXPLAIN ANALYZE SELECT * FROM pets WHERE seller_id = 1;
```

## 🌐 Browser Access

Once everything is running:

```
Frontend (React Dev Server):        http://localhost:5173
Backend API:                        http://localhost:8080
Swagger UI (API Docs):              http://localhost:8080/swagger-ui.html
OpenAPI Spec (JSON):                http://localhost:8080/v3/api-docs
Health Check:                       http://localhost:8080/actuator/health
Database (PostgreSQL):              localhost:5432
```

## 📝 First Steps After Setup

1. **Create an account**
   - Register at http://localhost:5173/register
   - Use any email (e.g., test@example.com)
   - Password: any strong password (min 8 chars, 1 uppercase, 1 number)

2. **Browse pets**
   - Visit http://localhost:5173/pets
   - View all available pets

3. **Become a seller**
   - Go to http://localhost:5173/seller/register
   - Verify email (in dev, check backend logs for verification link)
   - Add a pet listing

4. **Test checkout** (requires Stripe test keys)
   - Add pet to cart
   - Proceed to checkout
   - Use Stripe test card: `4242 4242 4242 4242`
   - Any future expiry, any CVC

5. **View admin dashboard**
   - Backend logs show admin recommendations
   - Check `/orders` API endpoint

## 🆘 Troubleshooting

### Docker Issues

```bash
# Container won't start
docker-compose logs backend
# Check for error messages

# Port already in use
# Find process using port 8080:
lsof -i :8080
kill -9 <PID>

# Rebuild images (if dependencies changed)
docker-compose build --no-cache
docker-compose up

# Clean everything and start fresh
docker-compose down -v
docker-compose build --no-cache
docker-compose up
```

### Database Connection Issues

```bash
# Can't connect to database
# 1. Check database is running
docker-compose ps postgres

# 2. Check credentials in .env
cat .env | grep POSTGRES

# 3. Test connection manually
docker-compose exec postgres psql -U postgres -d postgres

# 4. Check firewall if not using Docker
telnet localhost 5432
```

### Backend Issues

```bash
# Application won't start
# 1. Check Java version
java -version  # Should be 17+

# 2. Check Maven build
cd backend
mvn clean install

# 3. View logs
docker-compose logs backend

# 4. Port 8080 already in use
lsof -i :8080
```

### Frontend Issues

```bash
# Webpack errors
npm cache clean --force
npm install

# Node modules corrupted
rm -rf node_modules package-lock.json
npm install

# Port 5173 in use
lsof -i :5173
```

## 📖 Next Steps

1. **Read Architecture Docs**: [ARCHITECTURE.md](../docs/ARCHITECTURE.md)
2. **Explore API Docs**: [API.md](../docs/API.md)
3. **Review Code Structure**: See [frontend/README.md](../frontend/README.md) and [backend/README.md](../backend/README.md)
4. **Run Tests**: `npm test` and `mvn test`
5. **Start Coding**: Pick a task from [tasks.md](../tasks.md)

## 🚀 Performance Tips

### Frontend
- Use React DevTools Profiler to find slow renders
- Use Chrome DevTools Network tab to optimize API calls
- Enable production build to test bundle size

### Backend
- Use JProfiler or YourKit for profiling
- Monitor PostgreSQL query performance
- Use Java Flight Recorder for JVM profiling

## 📞 Getting Help

- **Backend Issues**: See [backend/README.md](../backend/README.md#troubleshooting)
- **Frontend Issues**: See [frontend/README.md](../frontend/README.md#troubleshooting)
- **Database Issues**: See [docs/DATABASE.md](../docs/DATABASE.md)
- **General**: Check troubleshooting sections above

---

**Happy coding! 🐾**
