# API Contract: Pet Catalog & Inventory

**Version**: v1  
**Phase**: Phase 1 - API Design  
**Status**: Final

---

## Public Endpoints (No Auth Required)

### GET /api/pets
List all available pets with filtering and pagination

**Query Parameters**:
| Param | Type | Example | Notes |
|-------|------|---------|-------|
| category | string | dogs,cats,fish,birds | Filter by species |
| price_min | decimal | 100.00 | Minimum price |
| price_max | decimal | 500.00 | Maximum price |
| breed | string | Golden Retriever | Filter by breed |
| search | string | Golden | Search in name/description |
| page | integer | 1 | Page number (default: 1) |
| limit | integer | 20 | Items per page (default: 20, max: 100) |
| sort | string | price_asc, price_desc, rating_desc, newest | Sort order |

**Response** (200 OK):
```json
{
  "data": [
    {
      "id": 1,
      "name": "Max",
      "species": "dog",
      "breed": "Golden Retriever",
      "age": 2.5,
      "price": 450.00,
      "description": "Friendly and energetic",
      "health_status": "Vaccinated",
      "availability_status": "available",
      "image_url": "https://cdn.example.com/max.jpg",
      "seller": {
        "id": 10,
        "business_name": "Happy Paws Breeder",
        "rating": 4.8,
        "total_sales": 25
      },
      "created_at": "2026-05-01T08:00:00Z"
    },
    { "..." }
  ],
  "meta": {
    "page": 1,
    "limit": 20,
    "total": 150,
    "total_pages": 8,
    "timestamp": "2026-05-05T10:00:00Z"
  }
}
```

---

### GET /api/pets/{petId}
Get detailed information about a single pet

**Parameters**:
| Param | Type | Notes |
|-------|------|-------|
| petId | integer | Pet ID |

**Response** (200 OK):
```json
{
  "data": {
    "id": 1,
    "name": "Max",
    "species": "dog",
    "breed": "Golden Retriever",
    "age": 2.5,
    "price": 450.00,
    "description": "Friendly and energetic. Great with families.",
    "health_status": "Vaccinated, microchipped, health certificate included",
    "availability_status": "available",
    "stock_quantity": 1,
    "images": [
      { "url": "https://cdn.example.com/max1.jpg", "order": 1 },
      { "url": "https://cdn.example.com/max2.jpg", "order": 2 }
    ],
    "seller": {
      "id": 10,
      "business_name": "Happy Paws Breeder",
      "rating": 4.8,
      "total_sales": 25,
      "response_time_hours": 2.5,
      "verified_at": "2026-01-15T00:00:00Z"
    },
    "reviews": {
      "count": 5,
      "average_rating": 4.8,
      "items": [
        {
          "id": 50,
          "buyer_name": "John D.",
          "rating": 5,
          "review_text": "Perfect pet, excellent seller!",
          "created_at": "2026-04-20T10:00:00Z"
        }
      ]
    },
    "created_at": "2026-05-01T08:00:00Z"
  },
  "meta": {
    "timestamp": "2026-05-05T10:00:00Z"
  }
}
```

**Error** (404):
```json
{
  "error": "Pet not found",
  "code": "NOT_FOUND"
}
```

---

## Authenticated Endpoints (Auth Required)

### POST /api/pets
Create a new pet listing (Seller only)

**Headers**:
```
Authorization: Bearer {access_token}
Content-Type: application/json
```

**Request**:
```json
{
  "name": "Bella",
  "species": "dog",
  "breed": "Labrador Retriever",
  "age": 1.5,
  "price": 500.00,
  "description": "Sweet and playful puppy",
  "health_status": "Vaccinated, health certificate included",
  "images": [
    {
      "url": "https://cdn.example.com/bella1.jpg",
      "order": 1
    }
  ]
}
```

**Response** (201 Created):
```json
{
  "data": {
    "id": 2,
    "name": "Bella",
    "species": "dog",
    "breed": "Labrador Retriever",
    "age": 1.5,
    "price": 500.00,
    "description": "Sweet and playful puppy",
    "health_status": "Vaccinated, health certificate included",
    "availability_status": "available",
    "stock_quantity": 1,
    "seller_id": 10,
    "created_at": "2026-05-05T10:00:00Z"
  },
  "meta": {
    "timestamp": "2026-05-05T10:00:00Z"
  }
}
```

**Error** (403):
```json
{
  "error": "User must be a verified seller to create listings",
  "code": "SELLER_NOT_VERIFIED"
}
```

---

### PUT /api/pets/{petId}
Update pet listing (Seller owner only)

**Headers**:
```
Authorization: Bearer {access_token}
Content-Type: application/json
```

**Request**:
```json
{
  "price": 450.00,
  "description": "Updated description",
  "availability_status": "reserved"
}
```

**Response** (200 OK):
```json
{
  "data": {
    "id": 2,
    "price": 450.00,
    "description": "Updated description",
    "availability_status": "reserved",
    "updated_at": "2026-05-05T10:30:00Z"
  },
  "meta": {
    "timestamp": "2026-05-05T10:30:00Z"
  }
}
```

**Error** (403):
```json
{
  "error": "You do not have permission to update this listing",
  "code": "FORBIDDEN"
}
```

---

### DELETE /api/pets/{petId}
Archive/delete pet listing (Seller owner only)

**Headers**:
```
Authorization: Bearer {access_token}
```

**Response** (204 No Content):
```
[empty body]
```

---

### POST /api/pets/{petId}/reviews
Leave a review for a purchased pet (Buyers only)

**Headers**:
```
Authorization: Bearer {access_token}
Content-Type: application/json
```

**Request**:
```json
{
  "rating": 5,
  "review_text": "Excellent pet and very friendly seller!"
}
```

**Response** (201 Created):
```json
{
  "data": {
    "id": 100,
    "pet_id": 1,
    "buyer_id": 5,
    "rating": 5,
    "review_text": "Excellent pet and very friendly seller!",
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
  "error": "You can only review pets you have purchased",
  "code": "INVALID_REVIEW"
}
```

---

### GET /api/pets/{petId}/reviews
Get reviews for a pet (Public)

**Query Parameters**:
| Param | Type | Example |
|-------|------|---------|
| page | integer | 1 |
| limit | integer | 10 |

**Response** (200 OK):
```json
{
  "data": [
    {
      "id": 100,
      "buyer_name": "John D.",
      "rating": 5,
      "review_text": "Excellent!",
      "created_at": "2026-05-05T10:00:00Z"
    }
  ],
  "meta": {
    "page": 1,
    "limit": 10,
    "total": 5,
    "average_rating": 4.8
  }
}
```

---

## Status Codes

| Code | Meaning |
|------|---------|
| 200 | OK |
| 201 | Created |
| 204 | No Content |
| 400 | Bad Request |
| 401 | Unauthorized |
| 403 | Forbidden |
| 404 | Not Found |
| 422 | Unprocessable Entity |
| 429 | Too Many Requests (rate limit) |
| 500 | Server Error |

---

## Filtering & Sorting Rules

**Availability Status**: 'available', 'reserved', 'sold', 'archived'
- Only 'available' and 'reserved' shown in public catalog
- Sellers see all their listings regardless of status

**Category Filter**: Case-insensitive; supports multiple (comma-separated)
- Valid: dogs, cats, fish, birds, other

**Sort Options**:
- `price_asc`: Lowest price first
- `price_desc`: Highest price first
- `rating_desc`: Highest rated sellers first
- `newest`: Recently listed first
- `popularity`: Most reviewed first

---

## Pagination

- Default page size: 20
- Max page size: 100
- Pages 1-indexed
- Returns total count for UI pagination
