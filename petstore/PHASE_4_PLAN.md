# 📋 Phase 4: Seller Account & Pet Listing Management - Implementation Plan

**Duration**: 4-5 days | **Tasks**: 44 total | **Type**: User Story 2  
**Branch**: 002-seller-account-listings

## Phase 4 Structure

### Stage 1: Backend Seller Infrastructure (T100-T110)
- **Entities**: Seller, SellerProfile with FK relationships
- **DTOs**: SellerDTO, SellerProfileDTO
- **Repositories**: Query methods for verification, rating, profiles
- **Services**: Registration, email verification, profile management
- **Controllers**: REST endpoints for seller operations
- **Tests**: Full test coverage for all layers

### Stage 2: Pet Listing Management (T111-T115)
- Extend PetService with seller-specific methods
- Update PetController with create/update/archive endpoints
- Add validation for seller listings
- Add seller_id FK constraint to Pet entity
- Add comprehensive tests

### Stage 3: Seller Dashboard API (T116-T118)
- SalesStats, RecentOrders, EarningsStats services
- Dashboard endpoint with aggregated data
- Tests for dashboard calculations

### Stage 4: Frontend Seller Onboarding (T119-T123)
- BecomeSeller page with registration form
- Email verification flow
- Form validation and submission
- Component tests

### Stage 5: Frontend Pet Listing (T124-T129)
- Create/Edit listing forms
- Multi-image upload component
- Archive functionality
- Form validation and tests

### Stage 6: Frontend Dashboard (T130-T134)
- Seller dashboard page
- My listings view
- Sales statistics display
- Recent orders widget
- Tests

### Stage 7: Frontend Seller Profile (T135-T138)
- Public seller profile page
- Seller card with reviews
- Review section
- Tests

### Stage 8: API Service & E2E (T139-T143)
- sellerService.js for API calls
- Complete E2E test suites for full workflows

## Execution Order

```
DAY 1: Backend Infrastructure
├─ T100-T104 [P] - Entity & DTO Layer
├─ T105 - Repository Tests (Integration)
├─ T106-T107 - Services
└─ T108 - Service Tests

DAY 2: Backend API & Validation
├─ T109-T110 - Controllers & Tests
├─ T111-T115 - Pet Listing Updates
└─ T116-T118 - Dashboard API

DAY 3: Frontend Onboarding & Listing
├─ T119-T123 - Seller Registration UI
├─ T124-T129 - Pet Listing Forms
└─ T139 - API Service Layer

DAY 4: Frontend Dashboard & Profile
├─ T130-T134 - Dashboard Components
└─ T135-T138 - Public Profile Pages

DAY 5: Testing & Polish
├─ T140-T143 - E2E Tests
├─ Bug Fixes
└─ Documentation
```

## Key Dependencies

**Before T106 (SellerService)**:
- ✅ T100-T104 (Entities, Repos)
- ✅ T105 (Repo Tests)

**Before T109 (SellerController)**:
- ✅ T106-T108 (Service & Tests)

**Before T111 (Update PetService)**:
- ✅ T109-T110 (Seller infrastructure)

**Before T124 (CreatePetListing)**:
- ✅ T111-T112 (Backend pet listing)

**Before T140-T143 (E2E Tests)**:
- ✅ All backend (T100-T118)
- ✅ All frontend (T119-T139)

## Database Schema Changes

### New Tables
- `sellers` - Seller business information
- `seller_profiles` - Extended seller details

### New Columns on Existing Tables
- `pets.seller_id` - FK to sellers (nullable)

### New Indexes
- `sellers.verification_status`
- `sellers.business_name`
- `seller_profiles.seller_id`
- `pets.seller_id`

## Authentication Requirements

- User must be authenticated to create seller account
- POST /api/sellers/register checks current user
- PUT /api/sellers/me/profile requires authentication
- Seller listing endpoints check seller ownership

## Email Integration

- EmailVerificationService handles token generation
- sendVerificationEmail() (implementation ready for Email service)
- Token format: UUID + 24-hour expiry
- Verification endpoint: `/api/sellers/verify-email?token=...`

## Next Steps

1. ✅ Create feature branch: 002-seller-account-listings
2. ⏳ T100-T104: Create Seller and SellerProfile entities
3. ⏳ T105: Write repository tests
4. ⏳ T106-T110: Services, controllers, tests
5. ⏳ Continue through all stages...

---

**Status**: Ready to start Stage 1  
**Estimated Completion**: May 16-17, 2026
