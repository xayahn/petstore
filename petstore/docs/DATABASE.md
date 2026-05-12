# Petstore Database Schema & Design

Complete database schema documentation for the Petstore peer-to-peer pet marketplace.

**Status**: Phase 1 - Schema Design  
**Last Updated**: 2026-05-05  
**Database**: PostgreSQL 14+

## 📋 Quick Reference

**Total Entities**: 17  
**Total Tables**: 17  
**Foreign Keys**: 25+  
**Indexes**: 30+  

See [data-model.md](../specs/001-petstore-core/data-model.md) for complete entity definitions and ERD diagram.

## 🔗 Data Model

The full data model is documented in: `specs/001-petstore-core/data-model.md`

This includes:
- Complete entity definitions (all 17 entities)
- ERD diagram (entity-relationship diagram)
- Table schemas with column types and constraints
- Foreign key relationships
- Index definitions
- Business logic rules (10+ documented)

## 🗄️ Core Tables

### Users
```sql
CREATE TABLE users (
  id SERIAL PRIMARY KEY,
  email VARCHAR(255) UNIQUE NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  first_name VARCHAR(100),
  last_name VARCHAR(100),
  phone VARCHAR(20),
  is_seller BOOLEAN DEFAULT false,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_email ON users(email);
```

### Sellers
```sql
CREATE TABLE sellers (
  id SERIAL PRIMARY KEY,
  user_id INTEGER UNIQUE NOT NULL REFERENCES users(id),
  business_name VARCHAR(255) NOT NULL,
  verification_status VARCHAR(50) DEFAULT 'pending',
  email_verified_at TIMESTAMP,
  rating DECIMAL(2,1) DEFAULT 0.0,
  response_time_hours DECIMAL(4,1),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_sellers_user_id ON sellers(user_id);
CREATE INDEX idx_sellers_status ON sellers(verification_status);
```

### Pets
```sql
CREATE TABLE pets (
  id SERIAL PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  species VARCHAR(100),
  breed VARCHAR(100),
  age DECIMAL(4,2),
  price DECIMAL(10,2) NOT NULL,
  description TEXT,
  health_status VARCHAR(255),
  stock_quantity INTEGER DEFAULT 1,
  availability_status VARCHAR(50) DEFAULT 'available',
  seller_id INTEGER NOT NULL REFERENCES sellers(id),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_pets_seller_id ON pets(seller_id);
CREATE INDEX idx_pets_status ON pets(availability_status);
CREATE INDEX idx_pets_category ON pets(species);
```

### Orders
```sql
CREATE TABLE orders (
  id SERIAL PRIMARY KEY,
  order_number VARCHAR(50) UNIQUE,
  buyer_id INTEGER NOT NULL REFERENCES users(id),
  seller_id INTEGER NOT NULL REFERENCES sellers(id),
  status VARCHAR(50) DEFAULT 'pending',
  subtotal DECIMAL(12,2),
  tax DECIMAL(12,2),
  shipping_cost DECIMAL(12,2),
  platform_commission DECIMAL(12,2),
  seller_payout DECIMAL(12,2),
  total DECIMAL(12,2),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_orders_buyer_id ON orders(buyer_id);
CREATE INDEX idx_orders_seller_id ON orders(seller_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_created_at ON orders(created_at);
```

### See Full Schema

All 17 table definitions with complete columns, constraints, and indexes are documented in:  
**[data-model.md](../specs/001-petstore-core/data-model.md)**

## 🚀 Database Migrations (Flyway)

Migrations are managed by Flyway and stored in: `backend/src/main/resources/db/migration/`

### Migration File Naming

```
V001__initial_schema.sql
V002__add_seller_features.sql
V003__add_payment_support.sql
...
```

### Running Migrations

```bash
# Automatic on app startup (Spring Boot)
mvn spring-boot:run

# Manual migration
mvn flyway:migrate

# Check migration status
mvn flyway:info

# Baseline existing DB
mvn flyway:baseline
```

## 🔍 Key Queries

### Public Catalog Browsing
```sql
SELECT p.*, s.business_name, s.rating 
FROM pets p
JOIN sellers s ON p.seller_id = s.id
WHERE p.availability_status IN ('available', 'reserved')
  AND p.species = 'dogs'
  AND p.price BETWEEN 100 AND 500
ORDER BY p.created_at DESC
LIMIT 20;
```

### Seller Dashboard - Recent Orders
```sql
SELECT o.id, o.order_number, o.status, oi.quantity, p.name, u.first_name
FROM orders o
JOIN order_items oi ON o.id = oi.order_id
JOIN pets p ON oi.pet_id = p.id
JOIN users u ON o.buyer_id = u.id
WHERE o.seller_id = $1
ORDER BY o.created_at DESC
LIMIT 10;
```

### Seller Earnings Summary
```sql
SELECT 
  DATE_TRUNC('month', se.created_at) AS month,
  SUM(se.gross_amount) AS gross,
  SUM(se.commission_amount) AS commission,
  SUM(se.seller_payout) AS payout
FROM seller_earnings se
WHERE se.seller_id = $1
GROUP BY DATE_TRUNC('month', se.created_at)
ORDER BY month DESC;
```

## 📊 Data Statistics (MVP Targets)

| Table | Rows (1 year) | Notes |
|-------|---------------|-------|
| users | 10,000+ | Buyers + sellers |
| sellers | 500+ | Active sellers |
| pets | 5,000+ | Active listings |
| orders | 50,000+ | Annual transactions |
| order_items | 50,000+ | Average 1 item/order |
| payments | 50,000+ | Payment records |
| seller_earnings | 50,000+ | Earning records |
| reviews | 50,000+ | User-generated content |

## 🔐 Security Considerations

### Data Protection
- **Passwords**: Bcrypt hashing (never stored in plain text)
- **Sensitive Data**: Bank accounts encrypted at rest
- **PCI Compliance**: Payment info never stored (Stripe only)
- **PII**: User addresses encrypted for GDPR compliance

### Query Security
- **SQL Injection Prevention**: Parameterized queries (JPA/Hibernate)
- **Backup Strategy**: Daily automated backups (3-month retention)
- **Audit Trail**: Payment webhooks logged for disputes

## 📈 Performance Optimization

### Indexes Strategy

**High-Priority Indexes** (created for query optimization):
- `users.email` - Fast login lookups
- `sellers.user_id` - Seller profile access
- `pets.seller_id` - Seller's inventory
- `orders.seller_id` + `orders.status` - Order filtering
- `order_items.order_id` - Fetch items for order
- `payments.order_id` - Payment history
- `seller_earnings.seller_id` + `seller_earnings.created_at` - Earnings reports

### Query Optimization
- Pagination enforced (max 100 rows per query)
- Lazy loading for relationships
- Connection pooling (HikariCP, 10-20 connections)
- Query timeout: 30 seconds

### Scaling Path

1. **Current (MVP)**: Single PostgreSQL instance, max ~10k concurrent connections
2. **Phase 2**: Read replicas for reporting queries
3. **Phase 3**: Sharding by seller_id for multi-region support
4. **Phase 4**: Redis cache for hot data (catalog, listings)

## 🛠️ Database Management

### Connection String

```
jdbc:postgresql://localhost:5432/petstore_dev
User: postgres
Password: postgres_password
```

### Docker Setup

```bash
# Start PostgreSQL via Docker Compose
docker-compose up postgres

# Connect to database
docker-compose exec postgres psql -U postgres -d petstore_dev

# Backup database
docker-compose exec postgres pg_dump -U postgres petstore_dev > backup.sql

# Restore database
docker-compose exec -T postgres psql -U postgres petstore_dev < backup.sql
```

### Monitoring

**Table Sizes**:
```sql
SELECT schemaname, tablename, pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename))
FROM pg_tables
WHERE schemaname = 'public'
ORDER BY pg_total_relation_size(schemaname||'.'||tablename) DESC;
```

**Index Usage**:
```sql
SELECT schemaname, tablename, indexname, idx_scan
FROM pg_stat_user_indexes
ORDER BY idx_scan DESC;
```

## 🔄 Transaction Management

### ACID Compliance
- **Atomicity**: All-or-nothing order processing
- **Consistency**: Foreign key constraints enforced
- **Isolation**: Default READ_COMMITTED level
- **Durability**: WAL (Write-Ahead Logging)

### Transaction Examples

**Checkout Transaction**:
```java
@Transactional
public Order checkout(Long userId, CheckoutRequest request) {
  // Fetch cart items
  // Validate inventory
  // Create order
  // Reduce stock
  // Record earnings
  // Create payment intent
  // Commit all or rollback
}
```

## 📚 Related Documents

- **[data-model.md](../specs/001-petstore-core/data-model.md)** - Complete entity definitions and ERD
- **[ARCHITECTURE.md](./ARCHITECTURE.md)** - System design overview
- **[backend/README.md](../backend/README.md)** - Backend setup and configuration

---

**Last Updated**: 2026-05-05  
**Schema Version**: 1.0.0 (MVP)  
**Compatible**: PostgreSQL 14+
