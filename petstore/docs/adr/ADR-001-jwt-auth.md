# ADR-001: JWT Authentication Strategy

**Date**: 2026-05-05  
**Status**: ACCEPTED  
**Author**: Petstore Team  
**Deciders**: Tech Lead, Architecture Team

## Context

The Petstore MVP needs a stateless authentication system that:
1. Doesn't require server-side session storage
2. Works seamlessly with mobile and SPA frontends
3. Supports token refresh without password re-entry
4. Implements secure password handling
5. Prevents token hijacking and replay attacks

## Decision

Implement **JWT (JSON Web Tokens)** with **RS256 (RSA Signature)** signing and **refresh token rotation**.

### Key Components

1. **Access Token**
   - Expiration: 15 minutes
   - Signing: RS256 (asymmetric)
   - Contains: sub (user ID), email, roles, iat, exp
   - Storage: Memory (frontend), cannot be persisted in localStorage for XSS protection

2. **Refresh Token**
   - Expiration: 7 days
   - Signing: RS256
   - Single-use (rotated on refresh)
   - Storage: httpOnly, Secure, SameSite=Strict cookie
   - Cannot be accessed from JavaScript

3. **Key Rotation**
   - Private key: Server-side only, never shared
   - Public key: Distributed to frontend for verification
   - Key rotation: Quarterly (future)

### Token Refresh Flow

```
Client Request → (Token expired)
               ↓
         Check Refresh Token
               ↓
    (Valid) → Generate new Access Token
               ↓
         Return new tokens + Update cookie
               ↓
         Retry original request
```

## Rationale

### Why JWT over Sessions?

| Factor | JWT | Sessions |
|--------|-----|----------|
| Server Scalability | ✅ Stateless | ❌ Requires session store |
| Mobile Support | ✅ Works natively | ⚠️ Requires careful setup |
| Microservices | ✅ Easy to share | ❌ Requires centralized store |
| Development Speed | ✅ Faster initial setup | ⚠️ More configuration |

### Why RS256 over HS256?

- **RS256**: Asymmetric (private key for signing, public key for verification)
  - ✅ Public key can be shared safely
  - ✅ Frontend can verify tokens without private key
  - ✅ Multiple services can verify tokens
  - ❌ Slower verification

- **HS256**: Symmetric (same key for both)
  - ✅ Faster signing/verification
  - ❌ Private key must be kept secret from all services
  - ❌ Harder to rotate keys

**Decision**: RS256 chosen for security and multi-service architecture.

### Why httpOnly Cookies for Refresh Tokens?

- **httpOnly Cookies**: Cannot be accessed from JavaScript
  - ✅ Protected against XSS (JavaScript injection)
  - ✅ Automatically sent with requests
  - ⚠️ Vulnerable to CSRF (mitigated by SameSite attribute)

- **localStorage**: Can be accessed from JavaScript
  - ❌ Vulnerable to XSS attacks
  - ✅ Not vulnerable to CSRF

**Decision**: httpOnly cookies chosen for maximum security + SameSite attribute for CSRF protection.

## Implementation Details

### Backend (Spring Boot)

```java
// JwtProvider generates tokens
@Component
public class JwtProvider {
  // RS256 key pair generation
  KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
  kpg.initialize(2048);
  KeyPair keyPair = kpg.generateKeyPair();
  
  // Sign token with private key
  public String generateAccessToken(User user) {
    return Jwts.builder()
      .setSubject(user.getId().toString())
      .claim("email", user.getEmail())
      .claim("roles", user.getRoles())
      .setIssuedAt(new Date())
      .setExpiration(new Date(System.currentTimeMillis() + 15 * 60 * 1000))
      .signWith(privateKey, SignatureAlgorithm.RS256)
      .compact();
  }
}

// Filter validates tokens in every request
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  public void doFilterInternal(...) throws ServletException, IOException {
    String token = extractTokenFromHeader(request);
    if (jwtProvider.validateToken(token)) {
      Claims claims = jwtProvider.getClaimsFromToken(token);
      // Set security context with user details
    } else {
      throw new JwtException("Invalid token");
    }
  }
}
```

### Frontend (React)

```javascript
// Axios interceptor injects token in every request
api.interceptors.request.use(config => {
  const token = authContext.accessToken;
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Response interceptor handles token refresh
api.interceptors.response.use(
  response => response,
  async error => {
    if (error.response?.status === 401) {
      const refreshed = await refreshToken();
      if (refreshed) {
        return api(error.config); // Retry original request
      } else {
        redirectToLogin();
      }
    }
    throw error;
  }
);
```

## Alternatives Considered

### 1. OAuth 2.0 with OpenID Connect
- Pros: Industry standard, third-party provider support
- Cons: Overkill for MVP, requires additional infrastructure
- **Rejected**: Too much complexity for initial release

### 2. API Keys
- Pros: Simple, no session management
- Cons: No expiration, hard to rotate, not suitable for user authentication
- **Rejected**: Only suitable for service-to-service auth

### 3. mTLS (Mutual TLS)
- Pros: Certificate-based, very secure
- Cons: Complex setup, not suitable for user authentication
- **Rejected**: Better for service mesh security

## Consequences

### Positive ✅
- Stateless authentication scales easily
- No server-side session database needed
- Works with microservices architecture
- Standard across industry (easy hiring)
- Supports mobile and SPA clients natively

### Negative ⚠️
- Token revocation requires additional mechanism (blacklist)
- Clock skew between servers can cause issues
- Token size is larger than session ID
- Requires HTTPS (non-negotiable security requirement)

### Mitigation Strategies
- Implement token blacklist for logout
- Short access token expiration (15 min)
- Verify server time synchronization (NTP)
- Always use HTTPS, never HTTP

## Security Considerations

1. **Token Storage**
   - ✅ Access token: Memory only
   - ✅ Refresh token: httpOnly, Secure, SameSite cookie
   - ❌ NEVER store in localStorage

2. **Token Transmission**
   - ✅ HTTPS only (enforced in production)
   - ✅ Authorization header (standard HTTP)

3. **Token Validation**
   - ✅ Signature verification with public key
   - ✅ Expiration check
   - ✅ Issuer verification
   - ✅ Audience verification (if multi-tenant)

4. **Key Management**
   - ✅ Private key: Secured in environment variable or HSM
   - ✅ Private key never logged or exposed
   - ✅ Public key: Can be shared safely
   - ✅ Key rotation: Quarterly or on compromise

## Monitoring & Observability

### Metrics to Track
- Token generation rate
- Token validation failure rate
- Refresh token usage
- Logout rate
- Suspicious activity (brute force login attempts)

### Log Events
- Successful login
- Failed login (too many attempts → block)
- Token refresh
- Logout
- Suspicious token modifications

## References

- [RFC 7519 - JSON Web Token (JWT)](https://tools.ietf.org/html/rfc7519)
- [RFC 7515 - JSON Web Signature (JWS)](https://tools.ietf.org/html/rfc7515)
- [OWASP - Authentication Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Authentication_Cheat_Sheet.html)
- [Spring Security JWT](https://spring.io/guides/tutorials/spring-security-and-angular-js/)

---

**Last Updated**: 2026-05-05
