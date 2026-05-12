# Data Model: Full Stack Petstore Core Platform

**Phase**: Phase 1 - Design & Contracts  
**Date**: 2026-05-05  
**Status**: Complete - Database Schema & Entity Relationships

---

## Entity Relationship Diagram (ERD)

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                        PETSTORE MARKETPLACE DATA MODEL                         │
├─────────────────────────────────────────────────────────────────────────────────┤

                            ┌──────────────────┐
                            │      USER        │
                            ├──────────────────┤
                            │ id (PK)          │
                            │ email (UNIQUE)   │
                            │ password_hash    │
                            │ first_name       │
                            │ last_name        │
                            │ phone            │
                            │ is_seller        │
                            │ created_at       │
                            │ updated_at       │
                            └──────┬───────────┘
                                   │
                    ┌──────────────┼──────────────┐
                    │              │              │
                    ▼              ▼              ▼
            ┌──────────────┐ ┌──────────────┐ ┌──────────────┐
            │    SELLER    │ │     CART     │ │   WISHLIST   │
            ├──────────────┤ ├──────────────┤ ├──────────────┤
            │ id (PK)      │ │ id (PK)      │ │ id (PK)      │
            │ user_id (FK) │ │ user_id (FK) │ │ user_id (FK) │
            │ biz_name     │ │ created_at   │ │ created_at   │
            │ verified_at  │ │ updated_at   │ │ updated_at   │
            │ rating       │ │              │ │              │
            │ response_hrs │ └──────────────┘ └──────┬───────┘
            │ created_at   │                         │
            │ updated_at   │              ┌──────────┘
            └──────┬───────┘              │
                   │              ┌───────▼──────────┐
                   │              │  WISHLIST_ITEM   │
                   │              ├───────────────────┤
                   │              │ id (PK)           │
                   │              │ wishlist_id (FK)  │
                   │              │ pet_id (FK)       │
                   │              │ added_at          │
        ┌──────────┼──────────────┴───────────────────┘
        │          │
        │          │        ┌──────────────────┐
        │          │        │   SELLER_PROFILE │
        │          │        ├──────────────────┤
        │          └───────▶│ id (PK)          │
        │                   │ seller_id (FK)   │
        │                   │ bio              │
        │                   │ return_policy    │
        │                   │ acct_holder_name │
        │                   │ bank_account     │
        │                   │ payout_method    │
        │                   └──────────────────┘
        │
        │        ┌─────────────────────┐
        │        │       PET           │
        │        ├─────────────────────┤
        │        │ id (PK)             │
        │        │ name                │
        │        │ species             │
        │        │ breed               │
        │        │ age                 │
        │        │ price               │
        │        │ description         │
        │        │ health_status       │
        │        │ availability_status │
        │        │ stock_quantity      │
        │        │ seller_id (FK)      │◀──┘
        │        │ created_at          │
        │        │ updated_at          │
        │        └──────┬──────────────┘
        │               │
        │    ┌──────────┼──────────────┐
        │    │          │              │
        │    ▼          ▼              ▼
        │  ┌──────────┐ ┌─────────┐ ┌────────────┐
        │  │ PET_IMAGE│ │ REVIEW  │ │ CART_ITEM  │
        │  ├──────────┤ ├─────────┤ ├────────────┤
        │  │ id (PK)  │ │ id (PK) │ │ id (PK)    │
        │  │ pet_id   │ │ pet_id  │ │ cart_id    │
        │  │ image_url│ │ buyer_id│ │ pet_id     │
        │  │ order    │ │ rating  │ │ quantity   │
        │  │ created  │ │ text    │ │ price_snap │
        │  └──────────┘ │ created │ │ added_at   │
        │               └─────────┘ └────────────┘
        │
        ▼
    ┌────────────────────────┐
    │      ORDER             │
    ├────────────────────────┤
    │ id (PK)                │
    │ order_number (UNIQUE)  │
    │ buyer_id (FK: User)    │
    │ seller_id (FK: Seller) │
    │ status                 │
    │ subtotal               │
    │ tax                    │
    │ shipping_cost          │
    │ platform_commission    │
    │ seller_payout          │
    │ total                  │
    │ shipping_addr_id (FK)  │
    │ created_at             │
    │ updated_at             │
    └────────┬───────────────┘
             │
    ┌────────┼──────────────┬─────────────────┐
    │        │              │                 │
    ▼        ▼              ▼                 ▼
┌──────────────┐ ┌─────────────────┐ ┌──────────────┐ ┌─────────────────┐
│ ORDER_ITEM   │ │  PAYMENT        │ │ SELLER_REVIEW│ │ SHIPPING_ADDR   │
├──────────────┤ ├─────────────────┤ ├──────────────┤ ├─────────────────┤
│ id (PK)      │ │ id (PK)         │ │ id (PK)      │ │ id (PK)         │
│ order_id (FK)│ │ order_id (FK)   │ │ seller_id(FK)│ │ user_id (FK)    │
│ pet_id       │ │ amount          │ │ buyer_id (FK)│ │ street          │
│ pet_name     │ │ method          │ │ order_id (FK)│ │ city            │
│ quantity     │ │ status          │ │ rating       │ │ state           │
│ unit_price   │ │ transaction_id  │ │ feedback     │ │ postal_code     │
│ subtotal     │ │ created_at      │ │ created_at   │ │ country         │
│ seller_id(FK)│ └─────────────────┘ └──────────────┘ │ is_default      │
└──────────────┘                                       └─────────────────┘

                    ┌──────────────────────┐
                    │  SELLER_EARNINGS     │
                    ├──────────────────────┤
                    │ id (PK)              │
                    │ seller_id (FK)       │
                    │ order_id (FK)        │
                    │ gross_amount         │
                    │ commission_rate (%)  │
                    │ commission_amount    │
                    │ seller_payout        │
                    │ status               │
                    │ payout_date          │
                    │ created_at           │
                    └──────────────────────┘
                             │
                             ▼
                    ┌──────────────────────┐
                    │   PAYOUT             │
                    ├──────────────────────┤
                    │ id (PK)              │
                    │ seller_id (FK)       │
                    │ amount               │
                    │ method               │
                    │ status               │
                    │ created_at           │
                    │ completed_at         │
                    │ reference_number     │
                    └──────────────────────┘
```

---

## Core Entities & Relationships

### 1. USER
**Purpose**: Central user account entity; represents both customers and sellers

| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| id | SERIAL | PRIMARY KEY | Auto-incrementing |
| email | VARCHAR(255) | UNIQUE, NOT NULL | Login identifier |
| password_hash | VARCHAR(255) | NOT NULL | bcrypt hashed |
| first_name | VARCHAR(100) | NOT NULL | |
| last_name | VARCHAR(100) | NOT NULL | |
| phone | VARCHAR(20) | | Optional |
| is_seller | BOOLEAN | DEFAULT FALSE | Seller capability flag |
| created_at | TIMESTAMP | DEFAULT NOW() | |
| updated_at | TIMESTAMP | DEFAULT NOW() ON UPDATE CURRENT_TIMESTAMP | |

**Indexes**:
- `UNIQUE INDEX idx_user_email` on email
- `INDEX idx_user_is_seller` on is_seller (for seller queries)

**Relationships**:
- 1:1 with Seller (when is_seller=true)
- 1:N with Cart
- 1:N with Order (as buyer_id)
- 1:N with Review (as buyer_id)
- 1:N with WishlistItem
- 1:N with ShippingAddress

---

### 2. SELLER
**Purpose**: Extends User with seller-specific attributes; tracks seller reputation and performance

| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| id | SERIAL | PRIMARY KEY | |
| user_id | INTEGER | FOREIGN KEY (User.id), NOT NULL | Seller user account |
| business_name | VARCHAR(255) | NOT NULL | Seller business name |
| verification_status | ENUM('pending', 'verified', 'suspended') | DEFAULT 'pending' | Email verification status |
| email_verified_at | TIMESTAMP | | Timestamp of email verification |
| rating | DECIMAL(2,1) | DEFAULT 0.0 | Average seller rating (1-5) |
| total_sales | INTEGER | DEFAULT 0 | Count of successful orders |
| response_time_hours | DECIMAL(4,1) | DEFAULT 24.0 | Avg hours to respond to inquiries |
| created_at | TIMESTAMP | DEFAULT NOW() | |
| updated_at | TIMESTAMP | DEFAULT NOW() | |

**Indexes**:
- `INDEX idx_seller_verification_status` on verification_status (for admin queries)
- `INDEX idx_seller_rating` on rating (for sorting)

**Relationships**:
- 1:1 with User
- 1:1 with SellerProfile
- 1:N with Pet (pets listed by this seller)
- 1:N with Order (as seller_id)
- 1:N with SellerEarnings
- 1:N with SellerReview (reviews about this seller)

---

### 3. SELLER_PROFILE
**Purpose**: Extended seller information; payment and business details

| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| id | SERIAL | PRIMARY KEY | |
| seller_id | INTEGER | FOREIGN KEY (Seller.id), NOT NULL | |
| bio | TEXT | | Business description |
| return_policy | TEXT | | Return/exchange policy |
| account_holder_name | VARCHAR(255) | | For payouts |
| bank_account | VARCHAR(255) | ENCRYPTED | Bank account (encrypted) |
| payout_method | ENUM('bank_transfer', 'paypal', 'stripe_connect') | DEFAULT 'bank_transfer' | |
| created_at | TIMESTAMP | DEFAULT NOW() | |
| updated_at | TIMESTAMP | DEFAULT NOW() | |

---

### 4. PET
**Purpose**: Pet product listing; owned by seller or admin (seller_id NULL)

| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| id | SERIAL | PRIMARY KEY | |
| name | VARCHAR(255) | NOT NULL | Pet name |
| species | ENUM('dog', 'cat', 'fish', 'bird', 'other') | NOT NULL | Species |
| breed | VARCHAR(100) | NOT NULL | Breed |
| age | DECIMAL(3,1) | | Age in years |
| price | DECIMAL(10,2) | NOT NULL | Sale price |
| description | TEXT | NOT NULL | Pet details |
| health_status | TEXT | | Vaccination, health info |
| availability_status | ENUM('available', 'reserved', 'sold', 'archived') | DEFAULT 'available' | |
| stock_quantity | INTEGER | DEFAULT 1 | Available units |
| seller_id | INTEGER | FOREIGN KEY (Seller.id), NULLABLE | NULL = admin pet, NOT NULL = seller pet |
| created_at | TIMESTAMP | DEFAULT NOW() | |
| updated_at | TIMESTAMP | DEFAULT NOW() | |

**Indexes**:
- `INDEX idx_pet_seller_id` on seller_id (for seller catalog)
- `INDEX idx_pet_species_category` on (species, availability_status)
- `INDEX idx_pet_price` on price (for price range filters)
- `FULLTEXT INDEX idx_pet_search` on (name, description, breed) (for search)

**Relationships**:
- N:1 with Seller (nullable; admin pets have no seller)
- 1:N with PetImage (images)
- 1:N with CartItem
- 1:N with OrderItem
- 1:N with Review
- 1:N with WishlistItem

---

### 5. PET_IMAGE
**Purpose**: Store multiple images per pet; ordered for display

| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| id | SERIAL | PRIMARY KEY | |
| pet_id | INTEGER | FOREIGN KEY (Pet.id), NOT NULL | |
| image_url | VARCHAR(500) | NOT NULL | CDN URL |
| display_order | INTEGER | DEFAULT 0 | Sort order for display |
| created_at | TIMESTAMP | DEFAULT NOW() | |

**Indexes**:
- `INDEX idx_pet_image_pet_id_order` on (pet_id, display_order)

---

### 6. CART
**Purpose**: Shopping cart; belongs to user; persists across sessions

| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| id | SERIAL | PRIMARY KEY | |
| user_id | INTEGER | FOREIGN KEY (User.id), NOT NULL, UNIQUE | One cart per user |
| created_at | TIMESTAMP | DEFAULT NOW() | |
| updated_at | TIMESTAMP | DEFAULT NOW() | |

**Indexes**:
- `UNIQUE INDEX idx_cart_user_id` on user_id

**Relationships**:
- N:1 with User
- 1:N with CartItem

---

### 7. CART_ITEM
**Purpose**: Individual items in a shopping cart

| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| id | SERIAL | PRIMARY KEY | |
| cart_id | INTEGER | FOREIGN KEY (Cart.id), NOT NULL | |
| pet_id | INTEGER | FOREIGN KEY (Pet.id), NOT NULL | |
| quantity | INTEGER | DEFAULT 1 | Usually 1 per pet (pets not stackable) |
| price_at_time_of_add | DECIMAL(10,2) | NOT NULL | Price snapshot for cart consistency |
| added_at | TIMESTAMP | DEFAULT NOW() | |

**Indexes**:
- `UNIQUE INDEX idx_cart_item_unique` on (cart_id, pet_id) (prevent duplicate items)

**Relationships**:
- N:1 with Cart
- N:1 with Pet

---

### 8. ORDER
**Purpose**: Completed purchase order; links buyer and seller

| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| id | SERIAL | PRIMARY KEY | |
| order_number | VARCHAR(20) | UNIQUE, NOT NULL | Human-readable order ID |
| buyer_id | INTEGER | FOREIGN KEY (User.id), NOT NULL | Customer |
| seller_id | INTEGER | FOREIGN KEY (Seller.id), NULLABLE | Seller (could be from multiple sellers per order) |
| status | ENUM('pending', 'processing', 'shipped', 'delivered', 'cancelled', 'refunded') | DEFAULT 'pending' | Order lifecycle |
| subtotal | DECIMAL(10,2) | NOT NULL | Pre-tax, pre-commission |
| tax | DECIMAL(10,2) | DEFAULT 0.00 | Sales tax |
| shipping_cost | DECIMAL(10,2) | DEFAULT 0.00 | Shipping fee |
| platform_commission | DECIMAL(10,2) | DEFAULT 0.00 | Commission taken by platform |
| seller_payout | DECIMAL(10,2) | DEFAULT 0.00 | Seller receives (subtotal - commission) |
| total | DECIMAL(10,2) | NOT NULL | Final amount paid by buyer |
| shipping_address_id | INTEGER | FOREIGN KEY (ShippingAddress.id), NULLABLE | |
| created_at | TIMESTAMP | DEFAULT NOW() | |
| updated_at | TIMESTAMP | DEFAULT NOW() | |

**Indexes**:
- `UNIQUE INDEX idx_order_number` on order_number
- `INDEX idx_order_buyer_id` on buyer_id (for order history)
- `INDEX idx_order_seller_id` on seller_id (for seller fulfillment)
- `INDEX idx_order_status` on status (for filtering)
- `INDEX idx_order_created_at` on created_at (for sorting by date)

**Relationships**:
- N:1 with User (as buyer_id)
- N:1 with Seller (as seller_id)
- 1:N with OrderItem
- 1:1 with Payment
- 1:N with SellerEarnings
- 1:1 with ShippingAddress

---

### 9. ORDER_ITEM
**Purpose**: Line items within an order; one per pet purchased

| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| id | SERIAL | PRIMARY KEY | |
| order_id | INTEGER | FOREIGN KEY (Order.id), NOT NULL | |
| pet_id | INTEGER | FOREIGN KEY (Pet.id), NOT NULL | Pet purchased |
| pet_name | VARCHAR(255) | NOT NULL | Snapshot of pet name |
| quantity | INTEGER | DEFAULT 1 | Qty ordered |
| unit_price | DECIMAL(10,2) | NOT NULL | Price at purchase time |
| subtotal | DECIMAL(10,2) | NOT NULL | unit_price * quantity |
| seller_id | INTEGER | FOREIGN KEY (Seller.id), NULLABLE | Seller of this specific pet |
| created_at | TIMESTAMP | DEFAULT NOW() | |

**Relationships**:
- N:1 with Order
- N:1 with Pet

---

### 10. PAYMENT
**Purpose**: Payment transaction tracking; linked to Stripe payment intents

| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| id | SERIAL | PRIMARY KEY | |
| order_id | INTEGER | FOREIGN KEY (Order.id), NOT NULL | |
| amount | DECIMAL(10,2) | NOT NULL | Amount charged |
| method | ENUM('card', 'paypal', 'bank_transfer', 'other') | NOT NULL | |
| status | ENUM('pending', 'succeeded', 'failed', 'refunded') | DEFAULT 'pending' | |
| transaction_id | VARCHAR(100) | | Stripe payment_intent ID |
| created_at | TIMESTAMP | DEFAULT NOW() | |
| updated_at | TIMESTAMP | DEFAULT NOW() | |

**Indexes**:
- `INDEX idx_payment_order_id` on order_id
- `INDEX idx_payment_status` on status (for payment reconciliation)

**Relationships**:
- N:1 with Order

---

### 11. REVIEW
**Purpose**: Customer reviews of purchased pets; includes ratings

| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| id | SERIAL | PRIMARY KEY | |
| pet_id | INTEGER | FOREIGN KEY (Pet.id), NOT NULL | |
| buyer_id | INTEGER | FOREIGN KEY (User.id), NOT NULL | Reviewer |
| seller_id | INTEGER | FOREIGN KEY (Seller.id), NOT NULL | Seller of pet |
| rating | INTEGER | CHECK (rating >= 1 AND rating <= 5) | 1-5 stars |
| review_text | TEXT | | Review content |
| created_at | TIMESTAMP | DEFAULT NOW() | |
| updated_at | TIMESTAMP | DEFAULT NOW() | |

**Indexes**:
- `INDEX idx_review_pet_id` on pet_id (for listing reviews)
- `INDEX idx_review_seller_id` on seller_id (for seller rating calculation)
- `UNIQUE INDEX idx_review_unique` on (pet_id, buyer_id) (one review per buyer per pet)

**Relationships**:
- N:1 with Pet
- N:1 with User (as buyer_id)
- N:1 with Seller

---

### 12. SELLER_REVIEW
**Purpose**: Reviews of sellers (not just pets); aggregated for seller rating

| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| id | SERIAL | PRIMARY KEY | |
| seller_id | INTEGER | FOREIGN KEY (Seller.id), NOT NULL | |
| buyer_id | INTEGER | FOREIGN KEY (User.id), NOT NULL | Reviewer |
| order_id | INTEGER | FOREIGN KEY (Order.id), NOT NULL | Source order |
| rating | INTEGER | CHECK (rating >= 1 AND rating <= 5) | Seller rating |
| feedback | TEXT | | |
| created_at | TIMESTAMP | DEFAULT NOW() | |

**Indexes**:
- `INDEX idx_seller_review_seller_id` on seller_id (for calculating seller avg rating)

---

### 13. SELLER_EARNINGS
**Purpose**: Track seller revenue per transaction; foundation for payouts

| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| id | SERIAL | PRIMARY KEY | |
| seller_id | INTEGER | FOREIGN KEY (Seller.id), NOT NULL | |
| order_id | INTEGER | FOREIGN KEY (Order.id), NOT NULL | |
| gross_amount | DECIMAL(10,2) | NOT NULL | Order total |
| commission_rate | DECIMAL(4,2) | DEFAULT 15.00 | Platform commission percentage |
| commission_amount | DECIMAL(10,2) | NOT NULL | Calculated: gross * rate / 100 |
| seller_payout | DECIMAL(10,2) | NOT NULL | Calculated: gross - commission |
| status | ENUM('pending', 'paid', 'refunded') | DEFAULT 'pending' | |
| payout_date | TIMESTAMP | | When paid to seller |
| created_at | TIMESTAMP | DEFAULT NOW() | |

**Indexes**:
- `INDEX idx_seller_earnings_seller_id_status` on (seller_id, status) (for payout processing)

**Relationships**:
- N:1 with Seller
- N:1 with Order

---

### 14. PAYOUT
**Purpose**: Actual fund transfer to seller; tracks completion status

| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| id | SERIAL | PRIMARY KEY | |
| seller_id | INTEGER | FOREIGN KEY (Seller.id), NOT NULL | |
| amount | DECIMAL(10,2) | NOT NULL | Total payout amount |
| method | ENUM('bank_transfer', 'paypal', 'stripe_connect') | NOT NULL | |
| status | ENUM('pending', 'processing', 'completed', 'failed') | DEFAULT 'pending' | |
| created_at | TIMESTAMP | DEFAULT NOW() | |
| completed_at | TIMESTAMP | | When completed |
| reference_number | VARCHAR(100) | | Bank transfer reference |

**Indexes**:
- `INDEX idx_payout_seller_id_status` on (seller_id, status)

---

### 15. WISHLIST
**Purpose**: User's saved items for later; personalization feature

| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| id | SERIAL | PRIMARY KEY | |
| user_id | INTEGER | FOREIGN KEY (User.id), NOT NULL, UNIQUE | One wishlist per user |
| created_at | TIMESTAMP | DEFAULT NOW() | |
| updated_at | TIMESTAMP | DEFAULT NOW() | |

**Relationships**:
- N:1 with User
- 1:N with WishlistItem

---

### 16. WISHLIST_ITEM
**Purpose**: Individual wishlist entries

| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| id | SERIAL | PRIMARY KEY | |
| wishlist_id | INTEGER | FOREIGN KEY (Wishlist.id), NOT NULL | |
| pet_id | INTEGER | FOREIGN KEY (Pet.id), NOT NULL | |
| added_at | TIMESTAMP | DEFAULT NOW() | |

**Indexes**:
- `UNIQUE INDEX idx_wishlist_item_unique` on (wishlist_id, pet_id)

---

### 17. SHIPPING_ADDRESS
**Purpose**: Customer delivery addresses; supports multiple addresses per user

| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| id | SERIAL | PRIMARY KEY | |
| user_id | INTEGER | FOREIGN KEY (User.id), NOT NULL | |
| street | VARCHAR(255) | NOT NULL | |
| city | VARCHAR(100) | NOT NULL | |
| state | VARCHAR(100) | NOT NULL | |
| postal_code | VARCHAR(20) | NOT NULL | |
| country | VARCHAR(2) | DEFAULT 'US' | ISO 3166-1 alpha-2 |
| is_default | BOOLEAN | DEFAULT FALSE | Default delivery address |
| created_at | TIMESTAMP | DEFAULT NOW() | |

**Indexes**:
- `INDEX idx_shipping_address_user_id` on user_id

---

## Database Constraints & Integrity

### Foreign Key Constraints
All foreign keys configured with:
- ON DELETE RESTRICT (prevent deletion of referenced records)
- ON UPDATE CASCADE (propagate updates)

### Check Constraints
- Rating: 1-5 range enforced at DB level
- Quantities: >= 0
- Prices: >= 0.00
- Commission rates: 0-100

### Unique Constraints
- User.email: Prevent duplicate registrations
- Order.order_number: Unique order identifiers
- Cart.user_id: One cart per user
- CartItem: (cart_id, pet_id) - no duplicate items in cart
- WishlistItem: (wishlist_id, pet_id) - no duplicate items in wishlist
- Review: (pet_id, buyer_id) - one review per buyer per pet

### Indexes Summary
**Performance Optimization**:
- Foreign keys indexed for JOIN performance
- Status columns indexed for WHERE/filtering queries
- Created_at/updated_at indexed for sorting and time-range queries
- seller_id indexed for multi-seller marketplace filtering
- Full-text search index on pet names/descriptions for fast text search

---

## Data Integrity Rules (Business Logic)

1. **Order Lifecycle**: pending → processing → shipped → delivered (or cancelled/refunded)
2. **Inventory Management**: stock_quantity decremented on order; restored on order cancellation
3. **Commission Calculation**: commission_amount = gross_amount * commission_rate / 100
4. **Seller Payout**: seller_payout = gross_amount - commission_amount
5. **Seller Rating**: Aggregated average of all seller reviews; updated after each new review
6. **Seller Verification**: Email verification required before listing pets (verification_status = 'verified')
7. **Cart Persistence**: Cart contents persisted across sessions; restored on login
8. **Review Eligibility**: Only buyers of pets can review; checked at application layer
9. **Seller Trust**: New sellers default to 'pending' verification; email verification moves to 'verified'
10. **Payment Idempotence**: Stripe transaction_id prevents duplicate charges

---

## Migration Strategy

**Flyway Version Control**:
- `V1__initial_schema.sql`: Core tables (User, Seller, Pet, Cart, Order, Payment)
- `V2__add_seller_profile.sql`: Extended seller info
- `V3__add_reviews_wishlist.sql`: Community features
- `V4__add_indexes_and_constraints.sql`: Performance optimization
- `V5__add_audit_logging.sql`: Audit trail for compliance

Each migration is idempotent and tested locally before deployment.

---

**Next Phase**: Generate API contracts based on these entities.
