# API Contract: Seller Management & Dashboard

**Version**: v1  
**Phase**: Phase 1 - API Design  
**Status**: Final

---

## Seller Account

### POST /api/sellers/register
Convert customer account to seller (with email verification)

**Headers**:
```
Authorization: Bearer {access_token}
Content-Type: application/json
```

**Request**:
```json
{
  "business_name": "Happy Paws Breeder",
  "bio": "Licensed breeder with 10+ years experience",
  "return_policy": "30-day return policy with health guarantee"
}
```

**Response** (201 Created):
```json
{
  "data": {
    "id": 10,
    "user_id": 5,
    "business_name": "Happy Paws Breeder",
    "verification_status": "pending",
    "email_verified_at": null,
    "rating": 0.0,
    "total_sales": 0,
    "created_at": "2026-05-05T10:00:00Z"
  },
  "meta": {
    "message": "Seller account created. Please verify your email to list pets.",
    "timestamp": "2026-05-05T10:00:00Z"
  }
}
```

---

### POST /api/sellers/verify-email
Verify seller email via token sent in registration email

**Request**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response** (200 OK):
```json
{
  "data": {
    "id": 10,
    "verification_status": "verified",
    "email_verified_at": "2026-05-05T10:05:00Z"
  },
  "meta": {
    "message": "Email verified. You can now list pets.",
    "timestamp": "2026-05-05T10:05:00Z"
  }
}
```

---

### GET /api/sellers/{sellerId}
Get public seller profile (No auth required)

**Response** (200 OK):
```json
{
  "data": {
    "id": 10,
    "business_name": "Happy Paws Breeder",
    "bio": "Licensed breeder with 10+ years experience",
    "rating": 4.8,
    "total_sales": 25,
    "response_time_hours": 2.5,
    "verified_at": "2026-01-15T00:00:00Z",
    "reviews": {
      "average_rating": 4.8,
      "count": 25,
      "recent": [
        {
          "buyer_name": "John D.",
          "rating": 5,
          "feedback": "Excellent seller!",
          "created_at": "2026-05-01T00:00:00Z"
        }
      ]
    }
  },
  "meta": {
    "timestamp": "2026-05-05T10:00:00Z"
  }
}
```

---

### PUT /api/sellers/me/profile
Update seller profile (Authenticated seller only)

**Headers**:
```
Authorization: Bearer {access_token}
Content-Type: application/json
```

**Request**:
```json
{
  "business_name": "Happy Paws Breeder",
  "bio": "Updated bio",
  "return_policy": "Updated policy",
  "account_holder_name": "Jane Smith",
  "payout_method": "bank_transfer"
}
```

**Response** (200 OK):
```json
{
  "data": {
    "id": 10,
    "business_name": "Happy Paws Breeder",
    "bio": "Updated bio",
    "return_policy": "Updated policy",
    "updated_at": "2026-05-05T10:00:00Z"
  },
  "meta": {
    "timestamp": "2026-05-05T10:00:00Z"
  }
}
```

---

## Seller Dashboard

### GET /api/sellers/me/dashboard
Get seller dashboard summary

**Headers**:
```
Authorization: Bearer {access_token}
```

**Response** (200 OK):
```json
{
  "data": {
    "seller_id": 10,
    "business_name": "Happy Paws Breeder",
    "verification_status": "verified",
    "rating": 4.8,
    "statistics": {
      "total_listings": 8,
      "active_listings": 6,
      "sold_count": 25,
      "pending_orders": 3,
      "shipped_orders": 5
    },
    "earnings": {
      "this_month": 5250.00,
      "this_year": 18750.00,
      "total_earned": 45000.00,
      "pending_payout": 2150.00
    },
    "recent_orders": [
      {
        "id": 1,
        "order_number": "ORD-20260505-001",
        "pet_name": "Max",
        "buyer_name": "John Doe",
        "amount": 450.00,
        "status": "shipped",
        "created_at": "2026-05-05T10:00:00Z"
      }
    ]
  },
  "meta": {
    "timestamp": "2026-05-05T10:00:00Z"
  }
}
```

---

### GET /api/sellers/me/listings
List seller's pet listings

**Headers**:
```
Authorization: Bearer {access_token}
```

**Query Parameters**:
| Param | Type | Example |
|-------|------|---------|
| status | string | available,reserved,sold,archived |
| page | integer | 1 |
| limit | integer | 20 |
| sort | string | newest, price_asc, price_desc |

**Response** (200 OK):
```json
{
  "data": [
    {
      "id": 1,
      "name": "Max",
      "breed": "Golden Retriever",
      "price": 450.00,
      "availability_status": "available",
      "stock_quantity": 1,
      "views": 125,
      "in_carts": 3,
      "review_count": 5,
      "rating": 4.8,
      "created_at": "2026-05-01T08:00:00Z"
    }
  ],
  "meta": {
    "page": 1,
    "limit": 20,
    "total": 8,
    "total_pages": 1
  }
}
```

---

### GET /api/sellers/me/orders
List seller's orders (from buyers)

**Headers**:
```
Authorization: Bearer {access_token}
```

**Query Parameters**:
| Param | Type | Example |
|-------|------|---------|
| status | string | pending,processing,shipped,delivered |
| page | integer | 1 |
| limit | integer | 20 |

**Response** (200 OK):
```json
{
  "data": [
    {
      "id": 1,
      "order_number": "ORD-20260505-001",
      "status": "processing",
      "buyer": {
        "id": 5,
        "name": "John Doe",
        "email": "john@example.com",
        "phone": "+1-555-123-4567"
      },
      "items": [
        {
          "id": 100,
          "pet_name": "Max",
          "quantity": 1,
          "unit_price": 450.00
        }
      ],
      "total": 500.50,
      "shipping_address": {
        "street": "123 Main St",
        "city": "Springfield",
        "state": "IL",
        "postal_code": "62701"
      },
      "created_at": "2026-05-05T10:00:00Z"
    }
  ],
  "meta": {
    "page": 1,
    "limit": 20,
    "total": 10,
    "total_pages": 1
  }
}
```

---

### PUT /api/sellers/me/orders/{orderId}/status
Update order fulfillment status

**Headers**:
```
Authorization: Bearer {access_token}
Content-Type: application/json
```

**Request**:
```json
{
  "status": "shipped",
  "tracking_number": "1Z999AA10123456784",
  "shipping_carrier": "UPS"
}
```

**Response** (200 OK):
```json
{
  "data": {
    "id": 1,
    "order_number": "ORD-20260505-001",
    "status": "shipped",
    "tracking_number": "1Z999AA10123456784",
    "shipping_carrier": "UPS",
    "updated_at": "2026-05-05T14:00:00Z"
  },
  "meta": {
    "message": "Buyer notified of shipment",
    "timestamp": "2026-05-05T14:00:00Z"
  }
}
```

---

## Seller Earnings & Payouts

### GET /api/sellers/me/earnings
Get earnings summary and transaction history

**Headers**:
```
Authorization: Bearer {access_token}
```

**Query Parameters**:
| Param | Type | Example |
|-------|------|---------|
| period | string | this_month, this_year, all |
| page | integer | 1 |
| limit | integer | 20 |

**Response** (200 OK):
```json
{
  "data": {
    "summary": {
      "this_month": 5250.00,
      "this_year": 18750.00,
      "total_earned": 45000.00,
      "total_commission_paid": -3000.00,
      "pending_payout": 2150.00
    },
    "transactions": [
      {
        "id": 50,
        "order_id": 1,
        "order_number": "ORD-20260505-001",
        "gross_amount": 500.50,
        "commission_rate": 15.0,
        "commission_amount": -75.08,
        "seller_payout": 425.42,
        "status": "paid",
        "payout_date": "2026-05-12T00:00:00Z",
        "created_at": "2026-05-05T10:00:00Z"
      }
    ]
  },
  "meta": {
    "page": 1,
    "limit": 20,
    "total": 100,
    "total_pages": 5
  }
}
```

---

### GET /api/sellers/me/payouts
Get payout history

**Headers**:
```
Authorization: Bearer {access_token}
```

**Response** (200 OK):
```json
{
  "data": [
    {
      "id": 1,
      "amount": 2500.00,
      "method": "bank_transfer",
      "status": "completed",
      "reference_number": "TRX-20260512-001",
      "created_at": "2026-05-12T08:00:00Z",
      "completed_at": "2026-05-13T14:30:00Z"
    },
    {
      "id": 2,
      "amount": 1250.00,
      "method": "bank_transfer",
      "status": "processing",
      "created_at": "2026-05-19T08:00:00Z",
      "completed_at": null
    }
  ],
  "meta": {
    "total": 2,
    "timestamp": "2026-05-05T10:00:00Z"
  }
}
```

---

### POST /api/sellers/me/request-payout
Request payout of pending earnings

**Headers**:
```
Authorization: Bearer {access_token}
Content-Type: application/json
```

**Request**:
```json
{
  "amount": 2150.00,
  "method": "bank_transfer"
}
```

**Response** (201 Created):
```json
{
  "data": {
    "id": 3,
    "amount": 2150.00,
    "method": "bank_transfer",
    "status": "pending",
    "created_at": "2026-05-05T10:00:00Z"
  },
  "meta": {
    "message": "Payout request submitted. You will receive funds within 3-5 business days.",
    "timestamp": "2026-05-05T10:00:00Z"
  }
}
```

**Error** (400):
```json
{
  "error": "Insufficient pending balance for requested payout",
  "code": "INSUFFICIENT_BALANCE"
}
```

---

## Seller Reviews

### GET /api/sellers/me/reviews
Get reviews about this seller

**Headers**:
```
Authorization: Bearer {access_token}
```

**Query Parameters**:
| Param | Type | Example |
|-------|------|---------|
| rating | integer | 5 |
| page | integer | 1 |
| limit | integer | 20 |

**Response** (200 OK):
```json
{
  "data": [
    {
      "id": 100,
      "buyer_name": "John D.",
      "rating": 5,
      "feedback": "Excellent seller! Pet arrived healthy and happy.",
      "order_id": 1,
      "created_at": "2026-05-05T10:00:00Z"
    }
  ],
  "meta": {
    "page": 1,
    "limit": 20,
    "total": 25,
    "average_rating": 4.8,
    "rating_distribution": {
      "5": 20,
      "4": 4,
      "3": 1,
      "2": 0,
      "1": 0
    }
  }
}
```

---

## Error Codes

| Code | Status | Meaning |
|------|--------|---------|
| SELLER_NOT_FOUND | 404 | Seller does not exist |
| NOT_VERIFIED | 403 | Seller account not verified |
| UNAUTHORIZED | 401 | User not authenticated |
| INSUFFICIENT_BALANCE | 400 | Insufficient pending payout balance |
| INVALID_PAYOUT_METHOD | 400 | Payout method not configured |
| ORDER_NOT_FOUND | 404 | Order does not exist or seller unauthorized |

---

## Seller Status Lifecycle

```
┌──────────────────┐
│    REGISTERED    │ (Seller account created)
└──────┬───────────┘
       │ Email verification required
       ▼
┌──────────────────┐
│    VERIFIED      │ (Can list pets)
└──────┬───────────┘
       │
       ▼
┌──────────────────┐
│  ACTIVE SELLER   │ (Listings visible, can receive orders)
└──────────────────┘

Alternative:
ANY → SUSPENDED (Policy violation by admin)
```

---

## Commission Structure

- **Default Rate**: 15% per transaction
- **Calculation**: commission_amount = gross_amount × 0.15
- **Seller Receives**: gross_amount - commission_amount
- **Payout Schedule**: Weekly or monthly (configurable per seller)
- **Payout Methods**: Bank transfer, PayPal, Stripe Connect
- **Tax Handling**: Platform issues 1099 forms (US sellers); seller responsible for tax reporting
