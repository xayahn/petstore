# ADR-004: API Design - RESTful vs GraphQL

**Date**: 2026-05-05  
**Status**: ACCEPTED  
**Author**: Petstore Team  
**Deciders**: Frontend Lead, Backend Lead

## Context

Petstore MVP frontend needs to fetch pet listings, user data, orders, and seller information. The backend must serve both web (React) and potentially mobile clients in the future.

Decision: Choose between REST and GraphQL for API design.

## Decision

Implement **RESTful API** with nested routes for MVP.

## Rationale

### REST vs GraphQL Comparison

| Factor | REST | GraphQL |
|--------|------|---------|
| Learning Curve | Easy | Steep |
| Client Flexibility | Fixed responses | Full flexibility |
| Over-fetching | Yes | No |
| Under-fetching | Yes | No |
| Caching | HTTP cache headers (simple) | Complex |
| Real-time | Webhooks/polling | Subscriptions |
| Error Handling | HTTP status codes | Single 200 with error field |
| Pagination | Query params | Cursor-based |
| Implementation | 2-3 days | 5-7 days |
| DevTools | Swagger/OpenAPI | GraphQL Playground |

### Why REST for MVP?

1. **Simplicity**: Industry standard, well-understood
2. **Team Knowledge**: All developers know REST
3. **Caching**: HTTP cache headers work out-of-the-box
4. **Frontend Stack**: React + Axios setup is straightforward
5. **Monitoring**: Standard HTTP tools (curl, Postman, Insomnia)
6. **API Documentation**: Swagger UI built-in with SpringDoc
7. **Development Speed**: Faster to implement

### REST API Structure

```
GET    /api/pets                    # List pets (public)
GET    /api/pets?category=dogs      # Filter by category
GET    /api/pets/:id                # Get pet details
GET    /api/sellers/:id             # Get seller profile (public)
GET    /api/sellers/:id/pets        # Get seller's pets
GET    /api/sellers/:id/ratings     # Get seller ratings

POST   /api/auth/register           # User registration
POST   /api/auth/login              # User login
POST   /api/auth/refresh-token      # Refresh JWT

POST   /api/carts/items             # Add to cart
PUT    /api/carts/items/:id         # Update cart item
DELETE /api/carts/items/:id         # Remove from cart
POST   /api/orders/checkout         # Create order

GET    /api/sellers/me/dashboard    # Seller dashboard (auth)
GET    /api/sellers/me/orders       # My orders (as seller)
POST   /api/pets                    # Create pet (seller)
```

## Alternatives Considered

### GraphQL (Rejected for MVP)

Pros:
- No over-fetching (request only needed fields)
- Single endpoint
- Strong typing system

Cons:
- Steeper learning curve
- No HTTP caching benefits
- More complex error handling
- Requires client library (Apollo)
- Takes longer to implement
- Overkill for MVP

### gRPC (Rejected for MVP)

Pros:
- High performance
- Strongly typed

Cons:
- Binary protocol (not human-readable)
- Harder for browser clients
- Learning curve
- Not web-friendly

## Implementation

### REST with Nested Resources

```java
@RestController
@RequestMapping("/api/sellers/{sellerId}")
public class SellerController {
  
  @GetMapping
  public SellerDto getSeller(@PathVariable Long sellerId) { }
  
  @GetMapping("/pets")
  public List<PetDto> getSellerPets(@PathVariable Long sellerId) { }
  
  @GetMapping("/orders")
  @PreAuthorize("@authService.isSeller(#sellerId)")
  public List<OrderDto> getSellerOrders(@PathVariable Long sellerId) { }
  
  @PostMapping("/pets")
  @PreAuthorize("@authService.isSeller(#sellerId)")
  public PetDto createPet(
    @PathVariable Long sellerId,
    @RequestBody PetRequest request
  ) { }
}
```

## Future Migration Path

If MVP grows beyond REST's needs:

1. **Add GraphQL layer** (keep REST for backwards compatibility)
2. **Use Apollo Federation** for seamless migration
3. **Deprecate REST** endpoints gradually
4. **Effort**: 2-3 weeks for gradual migration

## Monitoring

### API Metrics

- Response time (p50, p95, p99)
- Error rate by endpoint
- Request count by endpoint
- Cache hit/miss rate

### Tools

- Swagger UI: `/swagger-ui.html`
- OpenAPI spec: `/api-docs`
- Prometheus: `/actuator/metrics`

---

**Last Updated**: 2026-05-05

## References

- [REST Best Practices](https://restfulapi.net/)
- [HTTP Status Codes](https://httpwg.org/specs/rfc7231.html#status.codes)
- [OpenAPI 3.0 Specification](https://spec.openapis.org/oas/v3.0.3)
