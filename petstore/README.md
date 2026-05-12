# Petstore - Peer-to-Peer Pet Marketplace

A full-stack web application enabling customers to buy and sell pets in a secure, peer-to-peer marketplace. Built with Spring Boot 3.x, React 18+, and PostgreSQL.

## 🎯 Features

### Core Features
- **Public Pet Catalog**: Browse available pets without login
- **User Authentication**: Secure JWT-based authentication with email verification
- **Seller System**: Customers can become sellers after email verification
- **Shopping Cart**: Add/remove items and manage quantities
- **Secure Checkout**: Stripe payment integration with webhook handlers
- **Order Management**: Track orders and manage fulfillment
- **Seller Dashboard**: Manage listings, view orders, track earnings, request payouts
- **Reviews & Ratings**: Community feedback on sellers and purchased pets

### Technical Highlights
- **Scalable Architecture**: Stateless JWT auth, multi-seller data isolation
- **High Performance**: <500ms API response times, <2s page load
- **Security-First**: HTTPS/TLS, bcrypt hashing, SQL injection prevention, XSS protection
- **Test Coverage**: 80%+ unit tests, integration tests with TestContainers, E2E tests with Playwright
- **Docker Support**: Multi-stage builds for optimized production deployments

## 🚀 Quick Start

### Prerequisites
- Docker & Docker Compose (recommended)
- OR Java 17 LTS, Node.js 18+, PostgreSQL 14+

### Development with Docker (Recommended)

```bash
# Clone repository
git clone https://github.com/yourorg/petstore.git
cd petstore

# Copy environment template
cp .env.example .env

# Start all services
docker-compose up --build

# Access the application
# Frontend: http://localhost:5173
# Backend API: http://localhost:8080
# API Docs: http://localhost:8080/swagger-ui.html
```

### Local Development (Without Docker)

See [SETUP.md](./docs/SETUP.md) for detailed setup instructions for backend and frontend development.

## 📚 Documentation

- **[SETUP.md](./docs/SETUP.md)** - Local development setup and troubleshooting
- **[API.md](./docs/API.md)** - REST API reference and endpoint documentation
- **[ARCHITECTURE.md](./docs/ARCHITECTURE.md)** - System design and architecture decisions
- **[DATABASE.md](./docs/DATABASE.md)** - Database schema and entity relationships
- **[DEPLOYMENT.md](./docs/DEPLOYMENT.md)** - Production deployment guide

## 🔧 Tech Stack

### Backend
- **Java 17 LTS** with Spring Boot 3.x
- **Spring Security 6.x** with JWT authentication
- **Spring Data JPA** with PostgreSQL
- **Stripe API** for payments
- **Flyway** for database migrations
- **JUnit 5** & **Mockito** for testing

### Frontend
- **React 18+** with Context API state management
- **Vite 4+** for fast dev server and optimized builds
- **React Router 6** for navigation
- **Axios** for HTTP requests
- **Jest** & **React Testing Library** for testing
- **Playwright** for E2E testing

### Infrastructure
- **PostgreSQL 14+** relational database
- **Docker** & **Docker Compose** for containerization
- **Nginx** (production) for static file serving

## 📦 Project Structure

```
petstore/
├── backend/                          # Spring Boot application
│   ├── pom.xml
│   ├── src/main/java/com/petstore/
│   │   ├── config/                  # Security, DB, etc.
│   │   ├── entity/                  # JPA entities
│   │   ├── dto/                     # Data transfer objects
│   │   ├── repository/              # Database access layer
│   │   ├── service/                 # Business logic
│   │   └── controller/              # REST endpoints
│   └── Dockerfile
├── frontend/                         # React + Vite application
│   ├── package.json
│   ├── vite.config.js
│   ├── src/
│   │   ├── pages/                   # Route pages
│   │   ├── components/              # Reusable components
│   │   ├── context/                 # State management
│   │   ├── services/                # API clients
│   │   └── hooks/                   # Custom React hooks
│   └── Dockerfile
├── docs/                             # Documentation
│   ├── SETUP.md
│   ├── API.md
│   ├── ARCHITECTURE.md
│   ├── DATABASE.md
│   └── DEPLOYMENT.md
├── docker-compose.yml               # Local dev environment
├── Makefile                         # Development commands
└── .env.example                     # Environment template
```

## 🧪 Testing

```bash
# Backend unit tests
make test-backend

# Frontend unit tests
make test-frontend

# E2E tests
make test-e2e

# All tests
make test
```

## 🔐 Security Features

- **Authentication**: JWT with RS256 signing, refresh token rotation
- **Authorization**: Role-based access control (ROLE_CUSTOMER, ROLE_SELLER, ROLE_ADMIN)
- **Password Security**: bcrypt hashing with cost factor 12+
- **API Security**: CORS configuration, SQL injection prevention, XSS protection
- **Data Privacy**: Seller-scoped data isolation, PCI compliance via Stripe

## 📊 Performance Targets

- **Page Load**: <2 seconds (initial, with caching)
- **API Response**: <500ms for 95th percentile
- **Concurrent Users**: 100+ concurrent active users
- **Peak Throughput**: 1000+ concurrent cart operations

## 🚢 Deployment

Production deployment uses:
- **Backend**: Docker image on Kubernetes or Docker Compose with Nginx reverse proxy
- **Frontend**: Pre-built SPA served via Nginx
- **Database**: PostgreSQL 14+ managed instance (RDS, Azure Database, etc.)
- **Payments**: Stripe production keys
- **Email**: SendGrid or similar for transactional emails

See [DEPLOYMENT.md](./docs/DEPLOYMENT.md) for detailed deployment guide.

## 📝 Development Commands

```bash
# Build and start development environment
make setup
make up

# View logs
make logs
make logs-backend
make logs-frontend

# Run tests
make test
make test-backend
make test-frontend

# Code quality
make lint
make format

# Clean up
make down
make clean
```

## 🐛 Troubleshooting

**Backend won't start**: Check database connection in logs (`make logs-backend`)
**Frontend won't load**: Verify VITE_API_BASE_URL environment variable
**Port already in use**: Modify ports in `.env` file
**Tests failing**: Run `make clean` and rebuild

See [SETUP.md](./docs/SETUP.md) for more troubleshooting.

## 📄 License

MIT License - see LICENSE file for details

## 🤝 Contributing

1. Create a feature branch: `git checkout -b feature/your-feature`
2. Make changes and commit: `git commit -am 'Add feature'`
3. Push branch: `git push origin feature/your-feature`
4. Create Pull Request with description

## 📞 Support

- **Issues**: GitHub Issues
- **Discussions**: GitHub Discussions
- **Email**: support@petstore-demo.dev

---

**Last Updated**: 2026-05-05  
**Version**: 1.0.0 (MVP)
