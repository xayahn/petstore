# ADR-003: Database Schema Design - Single Schema vs Multi-Schema

**Date**: 2026-05-05  
**Status**: ACCEPTED  
**Author**: Petstore Team  
**Deciders**: Backend Lead, Database Team

## Context

Petstore is a peer-to-peer marketplace where multiple sellers can list and sell pets. We need to decide how to structure the database for multi-seller support:

1. **Single Schema**: All sellers' data in one schema with `seller_id` foreign keys
2. **Multi-Schema**: Each seller gets their own PostgreSQL schema
3. **Multi-Database**: Each seller gets their own database
4. **Multi-Tenant SaaS**: Platform-wide schema with tenant isolation

Key constraints:
- MVP launch: Single backend instance (no sharding)
- Growth path: Support 100+ sellers
- Data isolation: Sellers can only see their own data
- Operational simplicity: One database to manage

## Decision

Implement **Single Schema with `seller_id` Foreign Keys** for MVP.

### Schema Structure

```sql
-- Core tables
users (id, email, password_hash, first_name, last_name)
sellers (id, user_id UNIQUE, business_name, rating, created_at)

-- Seller-owned data
pets (id, seller_id FK, name, price, ...)
orders (id, seller_id FK, buyer_id FK, status, ...)
seller_earnings (id, seller_id FK, amount, ...)

-- Platform-wide tables
payments (id, order_id FK, amount, status, ...)
reviews (id, seller_id FK, buyer_id FK, rating, ...)
```

### Data Isolation Pattern

```sql
-- Seller can only see their own pets
SELECT * FROM pets WHERE seller_id = :current_user_seller_id;

-- Seller can only see their own orders
SELECT * FROM orders WHERE seller_id = :current_user_seller_id;

-- Verified with Spring Security @PreAuthorize
@PreAuthorize("@sellerService.isOwner(#sellerId, principal)")
public Order getOrder(Long sellerId, Long orderId) { }
```

## Rationale

### Comparison of Approaches

| Aspect | Single Schema | Multi-Schema | Multi-DB | Multi-Tenant SaaS |
|--------|---------------|--------------|----------|-------------------|
| Implementation | Easy | Medium | Hard | Hard |
| Query Complexity | Low | Very High | Very High | High |
| Performance | ✅ Fast | ⚠️ Schema switching | ❌ Slow | ✅ Fast |
| Data Isolation | Row-level | Row + Schema | Complete | Row-level |
| Scaling | Easy (sharding) | Complex | Very Complex | Proven |
| Operational Overhead | Minimal | Moderate | High | High |
| Cost | Low | Moderate | High | Very High |
| Best For | MVP | Compliance-heavy | Hostile tenants | Large SaaS |

### Why Single Schema?

#### ✅ Advantages

1. **Simplicity**
   - Single database = single backup, single restore
   - No schema switching logic in application
   - Simpler DevOps setup

2. **Query Performance**
   - No schema switching overhead
   - Single indexes on seller_id
   - Joins within single schema are fast

3. **Operational Ease**
   - Single connection pool
   - Unified monitoring and logging
   - Easy to debug queries

4. **Migration Path**
   - Easy to upgrade when needed
   - Can shard by seller_id later
   - Can move to multi-schema if compliance requires

5. **Cost-Effective**
   - One database instance
   - Simpler infrastructure
   - Lower cloud costs

#### ❌ Disadvantages

1. **Row-Level Isolation**
   - Requires application-level checks
   - Risk of query bugs exposing data
   - No database-enforced isolation

2. **Scaling Limit**
   - Single database scales to ~100k tables
   - Single schema can handle millions of rows
   - Eventually needs sharding

3. **Multi-Tenancy Concerns**
   - All sellers' data vulnerable to single SQL injection
   - Harder to achieve HIPAA/GDPR compliance
   - No complete data segregation

### Why NOT Multi-Schema?

```sql
-- Schema per seller (REJECTED)
CREATE SCHEMA seller_123;
CREATE TABLE seller_123.pets (...);
CREATE TABLE seller_123.orders (...);

-- Problems:
-- 1. Schema switching in application code
SET search_path = 'seller_123';
-- 2. No cross-schema queries (complex orders spanning sellers)
-- 3. Operational nightmare (100+ schemas to manage)
-- 4. Migrations must run on each schema
-- 5. No shared reference data
```

### Why NOT Multi-Database?

```
-- Database per seller (REJECTED)
petstore_seller_123.pets
petstore_seller_124.pets

-- Problems:
-- 1. Connection pool management (100+ connections)
-- 2. Cross-seller queries impossible (analytics)
-- 3. Operational overhead (backups, monitoring)
-- 4. High cloud costs (100+ database instances)
-- 5. Complex DevOps pipeline
```

## Implementation Details

### Backend: Spring Data JPA with seller_id Filter

```java
// Entity includes seller_id
@Entity
public class Pet {
  @Id private Long id;
  
  @ManyToOne
  @JoinColumn(name = "seller_id")
  private Seller seller;
  
  private String name;
  private BigDecimal price;
}

// Repository filters by seller_id
@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {
  List<Pet> findBySellerId(Long sellerId);
  
  // Query with both seller_id and pet_id (secure)
  Optional<Pet> findByIdAndSellerId(Long petId, Long sellerId);
}

// Service enforces seller_id check
@Service
@Transactional
public class PetService {
  public Pet getPet(Long petId, Long currentSellerId) {
    return petRepository.findByIdAndSellerId(petId, currentSellerId)
      .orElseThrow(() -> new NotFoundException("Pet not found"));
  }
  
  public List<Pet> getSellersInventory(Long sellerId) {
    // Authorization checked at controller level
    return petRepository.findBySellerId(sellerId);
  }
}

// Controller enforces authorization
@RestController
@RequestMapping("/api/sellers/{sellerId}/pets")
public class SellerPetController {
  
  @GetMapping
  @PreAuthorize("@sellerService.isOwner(#sellerId, principal)")
  public List<PetDto> getInventory(@PathVariable Long sellerId) {
    return petService.getSellersInventory(sellerId);
  }
  
  @PostMapping
  @PreAuthorize("@sellerService.isOwner(#sellerId, principal)")
  public PetDto createPet(
    @PathVariable Long sellerId,
    @RequestBody PetRequest request
  ) {
    return petService.createPet(sellerId, request);
  }
}
```

### Authorization Service (Gatekeeper)

```java
@Service
public class AuthorizationService {
  
  // Verify seller ownership
  public boolean isSeller(Long userId, Long sellerId) {
    Optional<Seller> seller = sellerRepository.findById(sellerId);
    return seller.isPresent() && seller.get().getUser().getId().equals(userId);
  }
  
  // Verify order access
  public boolean canAccessOrder(Long userId, Long orderId, String role) {
    Order order = orderRepository.findById(orderId).orElse(null);
    if (order == null) return false;
    
    if ("SELLER".equals(role)) {
      // Seller can only access their own orders (as seller)
      return order.getSeller().getUser().getId().equals(userId);
    } else {
      // Buyer can only access orders they placed
      return order.getBuyer().getId().equals(userId);
    }
  }
}

// Usage in controller
@PreAuthorize("@authService.canAccessOrder(#userId, #orderId, 'SELLER')")
public Order getSellerOrder(@PathVariable Long userId, @PathVariable Long orderId) {
  return orderService.getOrder(orderId);
}
```

### Database Queries with seller_id

```sql
-- Seller's inventory (fast, indexed)
SELECT * FROM pets WHERE seller_id = 123;

-- Seller's orders (fast, indexed)
SELECT * FROM orders WHERE seller_id = 123 AND status = 'processing';

-- Seller's earnings (fast, indexed)
SELECT DATE_TRUNC('month', created_at) AS month,
       SUM(seller_payout) AS total
FROM orders
WHERE seller_id = 123
GROUP BY DATE_TRUNC('month', created_at);

-- Buyer's orders (fast, indexed)
SELECT * FROM orders WHERE buyer_id = 456 ORDER BY created_at DESC;

-- Platform analytics (slow, should be async)
SELECT seller_id, COUNT(*) AS order_count, SUM(platform_commission)
FROM orders
WHERE created_at > NOW() - INTERVAL '30 days'
GROUP BY seller_id;
```

### Security Measures

```java
// 1. Application-level checks (primary)
@PreAuthorize("hasRole('SELLER')")  // User is seller
@PreAuthorize("@authService.isOwner(#sellerId, principal)")  // Correct seller

// 2. SQL-level constraints (backup)
ALTER TABLE pets ADD CONSTRAINT validate_seller
  CHECK (seller_id IN (SELECT id FROM sellers));

// 3. Row-level security (future PostgreSQL 15+)
ALTER TABLE pets ENABLE ROW LEVEL SECURITY;
CREATE POLICY seller_policy ON pets
  FOR SELECT USING (seller_id = current_user_id());

// 4. Audit logging (compliance)
CREATE TABLE audit_log (
  id SERIAL PRIMARY KEY,
  table_name VARCHAR(50),
  record_id BIGINT,
  seller_id BIGINT,
  action VARCHAR(20),
  timestamp TIMESTAMP
);
```

## Scaling & Future Evolution

### Phase 1-2: Single Schema (MVP)
- ✅ All data in one schema
- ✅ Row-level isolation via application checks
- ✅ Supports up to ~500 sellers (reasonable MVP)

### Phase 3: Read Replicas
- Add read-only PostgreSQL replicas
- Direct analytics queries to read replicas
- No schema changes needed

### Phase 4: Sharding (if needed >10k sellers)
```sql
-- Hash-based sharding by seller_id
IF seller_id % 4 == 0:
  CONNECT TO shard_0.petstore
ELSE IF seller_id % 4 == 1:
  CONNECT TO shard_1.petstore
-- etc.

-- Sharding library handles routing
@ShardingKey(column = "seller_id")
List<Pet> findBySellerId(Long sellerId);
```

### Phase 5: Complete Isolation (if compliance requires)
- Migrate to multi-schema or multi-database
- Application layer handles schema/db switching
- Legacy single-schema becomes read-only

## Monitoring & Operations

### Backup Strategy

```bash
# Single backup command (easy)
pg_dump -U postgres petstore_production > petstore-$(date +%Y%m%d).sql

# Upload to S3 (single backup file)
aws s3 cp petstore-20260505.sql s3://backups/

# Restore entire system
psql -U postgres < petstore-20260505.sql
```

### Performance Monitoring

```sql
-- Monitor seller_id index usage
SELECT schemaname, tablename, indexname, idx_scan
FROM pg_stat_user_indexes
WHERE indexname LIKE '%seller%'
ORDER BY idx_scan DESC;

-- Verify indexes are being used
EXPLAIN ANALYZE
SELECT * FROM orders WHERE seller_id = 123;
```

## Compliance & Security

### Data Isolation Validation

```bash
# Test 1: Seller can't see other seller's data
SELECT * FROM pets WHERE seller_id = OTHER_SELLER_ID;
-- Should return 401 Unauthorized (enforced at controller)

# Test 2: SQL injection doesn't break isolation
SELECT * FROM pets WHERE seller_id = '123 OR 1=1';
-- Only returns seller_id=123 (parameterized queries prevent injection)

# Test 3: Audit all seller_id queries
tail -f /var/log/postgresql/audit.log | grep seller_id
```

## Success Criteria

- [ ] Single schema with seller_id foreign keys implemented
- [ ] Authorization checks in all endpoints (0 queries without WHERE seller_id = ?)
- [ ] No data leaks between sellers (security testing)
- [ ] Indexes on seller_id reduce query time <100ms
- [ ] Seller can't access another seller's data (even with admin token)

## References

- [PostgreSQL Row-Level Security](https://www.postgresql.org/docs/current/ddl-rowsecurity.html)
- [Multi-Tenancy Patterns](https://en.wikipedia.org/wiki/Multi-tenancy)
- [Database Sharding Strategy](https://www.postgresql.org/docs/current/sql-createtable.html)

---

**Last Updated**: 2026-05-05
