# Petstore API Reference

Complete REST API documentation for the Petstore backend.

## 📚 API Endpoints

### Authentication
- [POST /api/auth/register](./contracts/auth-api.md#post-apiauthregister) - Register new user
- [POST /api/auth/login](./contracts/auth-api.md#post-apiauthlogin) - User login
- [POST /api/auth/refresh-token](./contracts/auth-api.md#post-apiauthrefresh-token) - Refresh JWT token
- [POST /api/auth/logout](./contracts/auth-api.md#post-apiauthlogout) - Logout user
- [GET /api/auth/me](./contracts/auth-api.md#get-apiauthme) - Get current user profile

### Pets
- [GET /api/pets](./contracts/pet-api.md#get-apipets) - Browse all pets (public)
- [GET /api/pets/{id}](./contracts/pet-api.md#get-apipetsid) - Get pet details
- [POST /api/pets](./contracts/pet-api.md#post-apipets) - Create new pet listing (seller)
- [PUT /api/pets/{id}](./contracts/pet-api.md#put-apipetsid) - Update pet listing (seller)
- [DELETE /api/pets/{id}](./contracts/pet-api.md#delete-apipetsid) - Delete pet listing (seller)
- [POST /api/pets/{id}/reviews](./contracts/pet-api.md#post-apipetsidreviews) - Leave review

### Shopping Cart
- [GET /api/carts](./contracts/order-api.md#get-apicarts) - Get current cart
- [POST /api/carts/items](./contracts/order-api.md#post-apicartitems) - Add item to cart
- [PUT /api/carts/items/{id}](./contracts/order-api.md#put-apicartsitemsid) - Update cart item
- [DELETE /api/carts/items/{id}](./contracts/order-api.md#delete-apicartsitemsid) - Remove from cart

### Orders & Checkout
- [POST /api/orders/checkout](./contracts/order-api.md#post-apiordercheckout) - Create order
- [GET /api/orders](./contracts/order-api.md#get-apiorders) - List user orders
- [GET /api/orders/{id}](./contracts/order-api.md#get-apiordersid) - Get order details
- [PUT /api/orders/{id}/status](./contracts/order-api.md#put-apiordersidstatus) - Update order status

### Payments
- [POST /api/payments/create-intent](./contracts/order-api.md#post-apipaymentscreate-intent) - Create Stripe payment intent
- [POST /api/payments/confirm](./contracts/order-api.md#post-apipayconfirm) - Confirm payment

### Sellers
- [POST /api/sellers/register](./contracts/seller-api.md#post-apisellersregister) - Become a seller
- [GET /api/sellers/{id}](./contracts/seller-api.md#get-apisellersid) - Get seller profile (public)
- [PUT /api/sellers/me/profile](./contracts/seller-api.md#put-apisellersmeprofile) - Update seller profile
- [GET /api/sellers/me/dashboard](./contracts/seller-api.md#get-apisellersmedashboard) - Seller dashboard
- [GET /api/sellers/me/listings](./contracts/seller-api.md#get-apisellersmelistings) - Seller's pet listings
- [GET /api/sellers/me/earnings](./contracts/seller-api.md#get-apisellersmeearnings) - View earnings
- [GET /api/sellers/me/payouts](./contracts/seller-api.md#get-apisellersemepayouts) - Payout history
- [POST /api/sellers/me/request-payout](./contracts/seller-api.md#post-apisellersmerequesto-payout) - Request payout

## 🔑 Authentication

All endpoints except `/auth/register`, `/auth/login`, `/pets` (GET), and `/sellers/{id}` require a valid JWT token:

```
Authorization: Bearer <access_token>
```

See [Authentication API Contract](../specs/001-petstore-core/contracts/auth-api.md) for token details.

## 📊 API Contracts

Complete API specifications:
- [auth-api.md](../specs/001-petstore-core/contracts/auth-api.md)
- [pet-api.md](../specs/001-petstore-core/contracts/pet-api.md)
- [order-api.md](../specs/001-petstore-core/contracts/order-api.md)
- [seller-api.md](../specs/001-petstore-core/contracts/seller-api.md)

## 🧪 Testing Endpoints

### Using cURL

```bash
# Register a user
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"Pass123!","first_name":"John","last_name":"Doe"}'

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"Pass123!"}'

# Browse pets (public, no auth required)
curl http://localhost:8080/api/pets?category=dogs&price_max=500

# Get current user (requires token)
curl -H "Authorization: Bearer <token>" \
  http://localhost:8080/api/auth/me
```

### Using Postman

Import the collection: [Petstore.postman_collection.json](./postman-collection.json)

### Using Swagger UI

Access interactive API explorer at: `http://localhost:8080/swagger-ui.html`

## 🚀 Base URL

- **Development**: `http://localhost:8080`
- **Production**: `https://api.petstore.com` (example)

## 📝 Request/Response Format

### Request Example

```json
{
  "email": "seller@example.com",
  "password": "SecurePass123!",
  "first_name": "Jane",
  "last_name": "Smith"
}
```

### Response Format

Success (2xx):
```json
{
  "data": {
    "id": 1,
    "email": "seller@example.com",
    "first_name": "Jane"
  },
  "meta": {
    "timestamp": "2026-05-05T10:00:00Z"
  }
}
```

Error (4xx/5xx):
```json
{
  "error": "Email already registered",
  "code": "DUPLICATE_EMAIL",
  "field": "email"
}
```

## 🔄 Pagination

Endpoints that return lists support pagination:

```
GET /api/pets?page=1&limit=20&sort=price_asc

Response meta:
{
  "page": 1,
  "limit": 20,
  "total": 150,
  "total_pages": 8
}
```

## ⚡ Rate Limiting

- Login: 5 attempts per 15 minutes per IP
- Register: 1 per 24 hours per email
- General API: 100 requests per minute per authenticated user

## 🔐 Security Notes

- All endpoints use HTTPS in production
- JWT tokens expire after 15 minutes (access) or 7 days (refresh)
- Refresh tokens are single-use and rotated
- Passwords are hashed with bcrypt (cost 12+)
- SQL injection and XSS protection enabled

---

**Last Updated**: 2026-05-05  
**Version**: 1.0.0  
**API Version**: v1
