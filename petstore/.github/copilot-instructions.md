<!-- SPECKIT START -->
**Reference Implementation Plan**: [specs/001-petstore-core/plan.md](../specs/001-petstore-core/plan.md)

For technical context, architecture decisions, project structure, and implementation guidelines, 
refer to the current plan. Key context includes:
- Backend: Java 17 LTS + Spring Boot 3.x + Spring Security 6.x + PostgreSQL 14+
- Backend Structure: Layered architecture with `layug` package root (rest, service, repository, entity, dto, config, security, exception, util)
- Backend Documentation: [backend/PACKAGE_STRUCTURE.md](../backend/PACKAGE_STRUCTURE.md)
- Frontend: React 18+ + Vite 4+ + Context API state management
- API Design: RESTful with nested routes; contracts in specs/001-petstore-core/contracts/
- Database Schema: 17 entities with seller_id isolation for peer-to-peer marketplace
- Testing: Unit tests (80%+ coverage) + Integration tests (Spring TestContainers) + E2E (Playwright)
- Peer-to-Peer Model: Public catalog browsing + seller verification via email + commission-based revenue

All implementation follows the constitution principles in specs/.specify/memory/constitution.md
<!-- SPECKIT END -->
