# API Contract: Authentication & Authorization

**Version**: v1  
**Phase**: Phase 1 - API Design  
**Status**: Final

---

## Endpoints

### POST /api/auth/register
Register a new user account

**Request**:
```json
{
  "email": "user@example.com",
  "password": "securePassword123!",
  "first_name": "John",
  "last_name": "Doe",
  "phone": "+1-555-123-4567"
}
```

**Response** (201 Created):
```json
{
  "data": {
    "id": 1,
    "email": "user@example.com",
    "first_name": "John",
    "last_name": "Doe",
    "phone": "+1-555-123-4567",
    "is_seller": false,
    "created_at": "2026-05-05T10:00:00Z"
  },
  "meta": {
    "timestamp": "2026-05-05T10:00:00Z"
  }
}
```

**Error** (400):
```json
{
  "error": "Email already registered",
  "code": "DUPLICATE_EMAIL",
  "field": "email"
}
```

---

### POST /api/auth/login
Authenticate user and return JWT tokens

**Request**:
```json
{
  "email": "user@example.com",
  "password": "securePassword123!"
}
```

**Response** (200 OK):
```json
{
  "data": {
    "user": {
      "id": 1,
      "email": "user@example.com",
      "first_name": "John",
      "is_seller": false
    },
    "tokens": {
      "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
      "refresh_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
      "expires_in": 900
    }
  },
  "meta": {
    "timestamp": "2026-05-05T10:00:00Z"
  }
}
```

**Error** (401):
```json
{
  "error": "Invalid credentials",
  "code": "UNAUTHORIZED"
}
```

---

### POST /api/auth/refresh-token
Refresh expired access token using refresh token

**Request**:
```json
{
  "refresh_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response** (200 OK):
```json
{
  "data": {
    "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expires_in": 900
  },
  "meta": {
    "timestamp": "2026-05-05T10:00:00Z"
  }
}
```

---

### POST /api/auth/logout
Logout user (invalidates refresh token)

**Headers**:
```
Authorization: Bearer {access_token}
```

**Response** (200 OK):
```json
{
  "data": { "message": "Logged out successfully" },
  "meta": { "timestamp": "2026-05-05T10:00:00Z" }
}
```

---

### GET /api/auth/me
Get current authenticated user profile

**Headers**:
```
Authorization: Bearer {access_token}
```

**Response** (200 OK):
```json
{
  "data": {
    "id": 1,
    "email": "user@example.com",
    "first_name": "John",
    "last_name": "Doe",
    "phone": "+1-555-123-4567",
    "is_seller": false,
    "seller": null
  },
  "meta": {
    "timestamp": "2026-05-05T10:00:00Z"
  }
}
```

---

## Token Structure (JWT)

**Access Token Payload**:
```json
{
  "sub": "1",
  "email": "user@example.com",
  "roles": ["ROLE_CUSTOMER", "ROLE_SELLER"],
  "iat": 1715001200,
  "exp": 1715002100
}
```

**Refresh Token Payload**:
```json
{
  "sub": "1",
  "iat": 1715001200,
  "exp": 1715087600
}
```

---

## Security

- Passwords hashed with bcrypt (min cost factor 12)
- Tokens signed with RS256 (RSA)
- Refresh tokens single-use; new token issued on refresh
- Refresh tokens stored in httpOnly cookie (XSS protection)
- Access tokens in response body (for single-page apps)
- HTTPS required; HTTP rejected in production

---

## Rate Limiting

- `/api/auth/login`: 5 attempts per 15 minutes per IP
- `/api/auth/register`: 1 per 24 hours per email
- `/api/auth/refresh-token`: 10 per hour per user
