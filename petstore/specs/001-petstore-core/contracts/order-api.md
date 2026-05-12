# API Contract: Orders, Cart & Checkout

**Version**: v1  
**Phase**: Phase 1 - API Design  
**Status**: Final

---

## Shopping Cart

### GET /api/carts
Get current user's shopping cart

**Headers**:
```
Authorization: Bearer {access_token}
```

**Response** (200 OK):
```json
{
  "data": {
    "id": 1,
    "user_id": 5,
    "items": [
      {
        "id": 10,
        "pet_id": 1,
        "pet_name": "Max",
        "price_at_time_of_add": 450.00,
        "quantity": 1,
        "seller": {
          "id": 10,
          "business_name": "Happy Paws Breeder"
        },
        "added_at": "2026-05-05T09:00:00Z"
      }
    ],
    "subtotal": 450.00,
    "tax": 40.50,
    "shipping": 10.00,
    "total": 500.50,
    "updated_at": "2026-05-05T09:00:00Z"
  },
  "meta": {
    "timestamp": "2026-05-05T10:00:00Z"
  }
}
```

---

### POST /api/carts/items
Add pet to cart

**Headers**:
```
Authorization: Bearer {access_token}
Content-Type: application/json
```

**Request**:
```json
{
  "pet_id": 1,
  "quantity": 1
}
```

**Response** (201 Created):
```json
{
  "data": {
    "id": 10,
    "pet_id": 1,
    "pet_name": "Max",
    "price_at_time_of_add": 450.00,
    "quantity": 1,
    "added_at": "2026-05-05T10:00:00Z"
  },
  "meta": {
    "timestamp": "2026-05-05T10:00:00Z"
  }
}
```

**Error** (400):
```json
{
  "error": "Pet is out of stock",
  "code": "OUT_OF_STOCK"
}
```

---

### PUT /api/carts/items/{itemId}
Update cart item quantity

**Headers**:
```
Authorization: Bearer {access_token}
Content-Type: application/json
```

**Request**:
```json
{
  "quantity": 2
}
```

**Response** (200 OK):
```json
{
  "data": {
    "id": 10,
    "quantity": 2,
    "updated_at": "2026-05-05T10:00:00Z"
  },
  "meta": {
    "timestamp": "2026-05-05T10:00:00Z"
  }
}
```

---

### DELETE /api/carts/items/{itemId}
Remove item from cart

**Headers**:
```
Authorization: Bearer {access_token}
```

**Response** (204 No Content):
```
[empty body]
```

---

## Checkout & Orders

### POST /api/orders/checkout
Create order from cart and initiate payment

**Headers**:
```
Authorization: Bearer {access_token}
Content-Type: application/json
```

**Request**:
```json
{
  "shipping_address_id": 5,
  "billing_address_id": 5,
  "payment_method": "card"
}
```

**Response** (201 Created):
```json
{
  "data": {
    "id": 1,
    "order_number": "ORD-20260505-001",
    "status": "pending",
    "buyer_id": 5,
    "items": [
      {
        "id": 100,
        "pet_id": 1,
        "pet_name": "Max",
        "quantity": 1,
        "unit_price": 450.00,
        "seller": {
          "id": 10,
          "business_name": "Happy Paws Breeder"
        }
      }
    ],
    "subtotal": 450.00,
    "tax": 40.50,
    "shipping": 10.00,
    "total": 500.50,
    "shipping_address": {
      "street": "123 Main St",
      "city": "Springfield",
      "state": "IL",
      "postal_code": "62701",
      "country": "US"
    },
    "created_at": "2026-05-05T10:00:00Z"
  },
  "meta": {
    "timestamp": "2026-05-05T10:00:00Z"
  }
}
```

---

### POST /api/payments/create-intent
Create Stripe payment intent for order

**Headers**:
```
Authorization: Bearer {access_token}
Content-Type: application/json
```

**Request**:
```json
{
  "order_id": 1
}
```

**Response** (200 OK):
```json
{
  "data": {
    "client_secret": "pi_1234567890_secret_xyz",
    "payment_intent_id": "pi_1234567890",
    "amount": 50050,
    "currency": "usd",
    "status": "requires_payment_method"
  },
  "meta": {
    "timestamp": "2026-05-05T10:00:00Z"
  }
}
```

---

### POST /api/payments/confirm
Confirm payment after frontend processes Stripe

**Headers**:
```
Authorization: Bearer {access_token}
Content-Type: application/json
```

**Request**:
```json
{
  "order_id": 1,
  "payment_intent_id": "pi_1234567890"
}
```

**Response** (200 OK):
```json
{
  "data": {
    "id": 50,
    "order_id": 1,
    "amount": 500.50,
    "method": "card",
    "status": "succeeded",
    "transaction_id": "pi_1234567890",
    "created_at": "2026-05-05T10:00:15Z"
  },
  "meta": {
    "timestamp": "2026-05-05T10:00:15Z"
  }
}
```

**Error** (400):
```json
{
  "error": "Payment failed: Card declined",
  "code": "PAYMENT_FAILED"
}
```

---

### GET /api/orders
List user's orders (with filtering)

**Headers**:
```
Authorization: Bearer {access_token}
```

**Query Parameters**:
| Param | Type | Example |
|-------|------|---------|
| status | string | pending,processing,shipped,delivered |
| page | integer | 1 |
| limit | integer | 10 |
| sort | string | newest, oldest |

**Response** (200 OK):
```json
{
  "data": [
    {
      "id": 1,
      "order_number": "ORD-20260505-001",
      "status": "shipped",
      "subtotal": 450.00,
      "tax": 40.50,
      "shipping": 10.00,
      "total": 500.50,
      "item_count": 1,
      "tracking_number": "1Z999AA10123456784",
      "created_at": "2026-05-05T10:00:00Z",
      "updated_at": "2026-05-05T14:00:00Z"
    }
  ],
  "meta": {
    "page": 1,
    "limit": 10,
    "total": 5,
    "total_pages": 1
  }
}
```

---

### GET /api/orders/{orderId}
Get detailed order information

**Headers**:
```
Authorization: Bearer {access_token}
```

**Response** (200 OK):
```json
{
  "data": {
    "id": 1,
    "order_number": "ORD-20260505-001",
    "status": "shipped",
    "buyer": {
      "id": 5,
      "name": "John Doe",
      "email": "john@example.com"
    },
    "items": [
      {
        "id": 100,
        "pet_id": 1,
        "pet_name": "Max",
        "quantity": 1,
        "unit_price": 450.00,
        "seller": {
          "id": 10,
          "business_name": "Happy Paws Breeder",
          "phone": "+1-555-987-6543"
        }
      }
    ],
    "subtotal": 450.00,
    "tax": 40.50,
    "shipping": 10.00,
    "total": 500.50,
    "shipping_address": {
      "street": "123 Main St",
      "city": "Springfield",
      "state": "IL",
      "postal_code": "62701",
      "country": "US"
    },
    "tracking_number": "1Z999AA10123456784",
    "shipping_carrier": "UPS",
    "estimated_delivery": "2026-05-10T00:00:00Z",
    "created_at": "2026-05-05T10:00:00Z",
    "updated_at": "2026-05-05T14:00:00Z"
  },
  "meta": {
    "timestamp": "2026-05-05T10:00:00Z"
  }
}
```

---

### PUT /api/orders/{orderId}/status
Update order status (Admin/Seller only)

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
    "updated_at": "2026-05-05T14:00:00Z"
  },
  "meta": {
    "timestamp": "2026-05-05T14:00:00Z"
  }
}
```

---

## Shipping Addresses

### GET /api/addresses
List user's saved shipping addresses

**Headers**:
```
Authorization: Bearer {access_token}
```

**Response** (200 OK):
```json
{
  "data": [
    {
      "id": 5,
      "street": "123 Main St",
      "city": "Springfield",
      "state": "IL",
      "postal_code": "62701",
      "country": "US",
      "is_default": true,
      "created_at": "2026-04-01T00:00:00Z"
    }
  ],
  "meta": {
    "timestamp": "2026-05-05T10:00:00Z"
  }
}
```

---

### POST /api/addresses
Add new shipping address

**Headers**:
```
Authorization: Bearer {access_token}
Content-Type: application/json
```

**Request**:
```json
{
  "street": "456 Oak Ave",
  "city": "Shelbyville",
  "state": "IL",
  "postal_code": "62702",
  "country": "US",
  "is_default": false
}
```

**Response** (201 Created):
```json
{
  "data": {
    "id": 6,
    "street": "456 Oak Ave",
    "city": "Shelbyville",
    "state": "IL",
    "postal_code": "62702",
    "country": "US",
    "is_default": false,
    "created_at": "2026-05-05T10:00:00Z"
  },
  "meta": {
    "timestamp": "2026-05-05T10:00:00Z"
  }
}
```

---

## Webhooks (No Auth)

### POST /webhooks/stripe
Stripe webhook handler for payment events

**Headers**:
```
Content-Type: application/json
Stripe-Signature: t=timestamp,v1=signature
```

**Supported Events**:
- `payment_intent.succeeded`: Mark order as paid; create SellerEarnings
- `payment_intent.payment_failed`: Mark order as failed; restore inventory
- `charge.refunded`: Process refund; adjust seller earnings

**Response** (200 OK):
```json
{
  "received": true
}
```

---

## Error Codes

| Code | Status | Meaning |
|------|--------|---------|
| CART_EMPTY | 400 | Cannot checkout with empty cart |
| OUT_OF_STOCK | 400 | Pet inventory depleted |
| INVALID_ADDRESS | 400 | Shipping address invalid or incomplete |
| PAYMENT_FAILED | 400 | Payment processing failed |
| ORDER_NOT_FOUND | 404 | Order does not exist |
| UNAUTHORIZED | 401 | Not authenticated or unauthorized |
| FORBIDDEN | 403 | User lacks permission |

---

## Order Status Lifecycle

```
┌─────────────┐
│   PENDING   │ (Initial state after checkout)
└──────┬──────┘
       │
       ▼
┌─────────────────┐
│   PROCESSING    │ (Payment confirmed, inventory reserved)
└──────┬──────────┘
       │
       ▼
┌─────────────────┐
│    SHIPPED      │ (In transit with tracking)
└──────┬──────────┘
       │
       ▼
┌─────────────────┐
│   DELIVERED     │ (Final success state)
└─────────────────┘

Alternative paths:
PENDING → CANCELLED (buyer cancels before payment)
PROCESSING → REFUNDED (payment reversal)
ANY → CANCELLED (admin cancels due to issues)
```
