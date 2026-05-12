.PHONY: help build build-backend build-frontend up down logs test test-backend test-frontend lint format clean

# Default target
help:
	@echo "Petstore Development Commands:"
	@echo ""
	@echo "Setup & Build:"
	@echo "  make setup           - Initialize development environment"
	@echo "  make build           - Build all services (Docker)"
	@echo "  make build-backend   - Build backend only"
	@echo "  make build-frontend  - Build frontend only"
	@echo ""
	@echo "Development:"
	@echo "  make up              - Start all services (docker-compose up)"
	@echo "  make down            - Stop all services (docker-compose down)"
	@echo "  make logs            - View service logs"
	@echo "  make logs-backend    - View backend logs"
	@echo "  make logs-frontend   - View frontend logs"
	@echo ""
	@echo "Testing:"
	@echo "  make test            - Run all tests"
	@echo "  make test-backend    - Run backend unit tests"
	@echo "  make test-frontend   - Run frontend unit tests"
	@echo "  make test-e2e        - Run E2E tests"
	@echo ""
	@echo "Code Quality:"
	@echo "  make lint            - Run all linters"
	@echo "  make lint-backend    - Run backend linter"
	@echo "  make lint-frontend   - Run frontend linter"
	@echo "  make format          - Format code"
	@echo ""
	@echo "Utilities:"
	@echo "  make clean           - Clean build artifacts"
	@echo "  make seed            - Seed database with test data"
	@echo ""

# Setup development environment
setup:
	@echo "Setting up development environment..."
	@test -f .env || cp .env.example .env
	@docker-compose build
	@echo "Setup complete. Run 'make up' to start services."

# Build all services
build: build-backend build-frontend
	@echo "All services built successfully"

# Build backend
build-backend:
	@echo "Building backend..."
	docker-compose build backend

# Build frontend
build-frontend:
	@echo "Building frontend..."
	docker-compose build frontend-dev

# Start all services
up:
	@echo "Starting services..."
	docker-compose up -d
	@echo "Services started. Backend: http://localhost:8080, Frontend: http://localhost:5173"

# Stop all services
down:
	@echo "Stopping services..."
	docker-compose down

# View logs
logs:
	docker-compose logs -f

logs-backend:
	docker-compose logs -f backend

logs-frontend:
	docker-compose logs -f frontend-dev

# Run tests
test: test-backend test-frontend
	@echo "All tests completed"

test-backend:
	@echo "Running backend tests..."
	cd backend && mvn test

test-frontend:
	@echo "Running frontend tests..."
	cd frontend && npm test

test-e2e:
	@echo "Running E2E tests..."
	cd frontend && npm run e2e

# Code quality
lint: lint-backend lint-frontend
	@echo "Linting complete"

lint-backend:
	@echo "Linting backend..."
	cd backend && mvn validate

lint-frontend:
	@echo "Linting frontend..."
	cd frontend && npm run lint

format:
	@echo "Formatting code..."
	cd frontend && npm run format

# Clean
clean:
	@echo "Cleaning build artifacts..."
	rm -rf backend/target frontend/dist frontend/node_modules
	docker-compose down -v
	@echo "Clean complete"

# Seed database with test data
seed:
	@echo "Seeding database..."
	docker-compose exec backend mvn exec:java -Dexec.mainClass="com.petstore.util.DatabaseSeeder"
	@echo "Database seeded"

# Development helpers
shell-backend:
	docker-compose exec backend /bin/bash

shell-frontend:
	docker-compose exec frontend-dev /bin/sh

db-shell:
	docker-compose exec postgres psql -U postgres -d petstore_dev

# Status
status:
	docker-compose ps
