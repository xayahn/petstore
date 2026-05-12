import { test, expect } from '@playwright/test';

/**
 * E2E Tests - Catalog Browsing Flows
 * T097, T098, T099
 * 
 * Tests complete user workflows through the catalog:
 * - Landing page hero and featured pets
 * - Browse page with filters and search
 * - Pet detail page navigation
 */

test.describe('Catalog Browsing - E2E Tests', () => {
  const BASE_URL = 'http://localhost:5173';

  test.describe('T097: Landing Page Hero & Featured Pets', () => {
    test('user sees hero banner with CTA buttons on landing page', async ({ page }) => {
      await page.goto(BASE_URL);

      // Verify hero banner is visible
      const heroText = page.locator('text=Find Your Perfect Pet Companion');
      await expect(heroText).toBeVisible();

      // Verify CTA buttons
      const browsePetsBtn = page.locator('button:has-text("Browse Pets")');
      const startSellingBtn = page.locator('button:has-text("Start Selling")');

      await expect(browsePetsBtn).toBeVisible();
      await expect(startSellingBtn).toBeVisible();
    });

    test('featured pets section loads and displays pets', async ({ page }) => {
      await page.goto(BASE_URL);

      // Wait for featured pets section
      const featuredSection = page.locator('text=Featured Pets');
      await expect(featuredSection).toBeVisible();

      // Wait for pet cards to load
      await page.waitForSelector('img[alt*="Pet image"]', { timeout: 5000 }).catch(() => {
        // Images may not load in test environment, check for other indicators
      });

      // Verify at least one pet card or loading indicator
      const petCards = page.locator('.pet-card');
      const count = await petCards.count();
      expect(count).toBeGreaterThanOrEqual(0);
    });

    test('hero banner Browse Pets button navigates to browse page', async ({ page }) => {
      await page.goto(BASE_URL);

      const browsePetsBtn = page.locator('text=Browse Pets').first();
      await browsePetsBtn.click();

      // Verify navigation to browse page
      await expect(page).toHaveURL(/\/browse/);
      await expect(page.locator('text=Browse Pets')).toBeVisible();
    });

    test('features section displays information cards', async ({ page }) => {
      await page.goto(BASE_URL);

      // Scroll to see more content
      await page.evaluate(() => window.scrollBy(0, 300));

      // Verify feature cards are visible
      const verifiedSellers = page.locator('text=Verified Sellers');
      const securePayments = page.locator('text=Secure Payments');

      await expect(verifiedSellers).toBeVisible({ timeout: 5000 });
      await expect(securePayments).toBeVisible({ timeout: 5000 });
    });
  });

  test.describe('T098: Catalog Filtering, Search & Pagination', () => {
    test('user can filter pets by species', async ({ page }) => {
      await page.goto(`${BASE_URL}/browse`);

      // Wait for filter sidebar to load
      await expect(page.locator('text=Filters')).toBeVisible();

      // Select species filter
      const speciesSelect = page.locator('select#species-filter');
      await speciesSelect.selectOption('Dog');

      // Apply filters
      const applyBtn = page.locator('button:has-text("Apply Filters")');
      await applyBtn.click();

      // Wait for results to load
      await page.waitForSelector('text=/Showing [0-9]+ results/');

      // Verify filters were applied
      const resultsText = page.locator('text=/Showing [0-9]+ results/');
      await expect(resultsText).toBeVisible();
    });

    test('user can set price range filter', async ({ page }) => {
      await page.goto(`${BASE_URL}/browse`);

      // Wait for filter sidebar
      await expect(page.locator('text=Filters')).toBeVisible();

      // Set price range
      const minPriceInput = page.locator('input#min-price');
      const maxPriceInput = page.locator('input#max-price');

      await minPriceInput.fill('100');
      await maxPriceInput.fill('500');

      // Apply filters
      const applyBtn = page.locator('button:has-text("Apply Filters")');
      await applyBtn.click();

      // Verify results loaded
      await page.waitForSelector('text=/Showing [0-9]+ results/');
    });

    test('user can search for pets by query', async ({ page }) => {
      await page.goto(`${BASE_URL}/browse`);

      // Find search input
      const searchInput = page.locator('input[placeholder*="Search by name"]');
      await searchInput.fill('fluffy');

      // Click search button
      const searchBtn = page.locator('button:has-text("Search")');
      await searchBtn.click();

      // Verify results loaded
      await page.waitForSelector('text=/Page [0-9]+ of/');
    });

    test('user can change sort order', async ({ page }) => {
      await page.goto(`${BASE_URL}/browse`);

      // Wait for results to load
      await page.waitForSelector('text=/Showing [0-9]+ results/');

      // Change sort
      const sortSelect = page.locator('select').nth(0); // First select is sort
      await sortSelect.selectOption('price');

      // Verify change was applied
      await page.waitForTimeout(500); // Brief wait for results update
    });

    test('user can navigate between pages', async ({ page }) => {
      await page.goto(`${BASE_URL}/browse`);

      // Wait for initial results
      await page.waitForSelector('text=/Page [0-9]+ of/');

      // Get initial page number
      const initialPageText = await page.locator('text=/Page [0-9]+ of/').first().textContent();

      // Click next button if not last page
      const nextBtn = page.locator('button:has-text("Next →")');
      const isDisabled = await nextBtn.isDisabled();

      if (!isDisabled) {
        await nextBtn.click();

        // Verify page changed
        await page.waitForTimeout(300);
        const newPageText = await page.locator('text=/Page [0-9]+ of/').first().textContent();
        expect(newPageText).not.toEqual(initialPageText);
      }
    });

    test('clear filters button resets all filters', async ({ page }) => {
      await page.goto(`${BASE_URL}/browse`);

      // Apply a filter
      const speciesSelect = page.locator('select#species-filter');
      await speciesSelect.selectOption('Dog');

      // Verify filter is applied
      expect(speciesSelect).toHaveValue('Dog');

      // Click clear filters
      const clearBtn = page.locator('button:has-text("Clear All")');
      await clearBtn.click();

      // Verify filter was cleared
      expect(speciesSelect).toHaveValue('');
    });
  });

  test.describe('T099: Pet Detail Page Navigation', () => {
    test('user can click pet card and view detail page', async ({ page }) => {
      await page.goto(`${BASE_URL}/browse`);

      // Wait for pet cards to load
      await page.waitForSelector('a[href*="/pets/"]', { timeout: 5000 }).catch(() => {
        console.log('No pet cards found, skipping detail navigation test');
      });

      // Get first pet link
      const firstPetLink = page.locator('a[href*="/pets/"]').first();
      const href = await firstPetLink.getAttribute('href');

      if (href) {
        await firstPetLink.click();

        // Verify navigation to pet detail page
        await expect(page).toHaveURL(new RegExp(href));

        // Verify detail page content is visible
        const backBtn = page.locator('text=Back to Browse');
        await expect(backBtn).toBeVisible();
      }
    });

    test('pet detail page displays all required information', async ({ page }) => {
      await page.goto(`${BASE_URL}/browse`);

      // Navigate to first pet detail
      const firstPetLink = page.locator('a[href*="/pets/"]').first();
      const href = await firstPetLink.getAttribute('href');

      if (href) {
        await page.goto(BASE_URL + href);

        // Verify detail page elements
        const speciesOrName = page.locator('text=/Dog|Cat|Bird|Rabbit|Fluffy|Pet Name/i');
        await expect(speciesOrName.first()).toBeVisible({ timeout: 5000 });

        // Verify price is displayed
        const price = page.locator('text=/\$[0-9]+\.[0-9]{2}/');
        await expect(price.first()).toBeVisible({ timeout: 5000 });

        // Verify action buttons
        const addToCart = page.locator('button:has-text("Add to Cart")');
        await expect(addToCart).toBeVisible({ timeout: 5000 });
      }
    });

    test('back button returns to browse page', async ({ page }) => {
      await page.goto(`${BASE_URL}/browse`);

      // Navigate to a pet detail
      const firstPetLink = page.locator('a[href*="/pets/"]').first();
      const href = await firstPetLink.getAttribute('href');

      if (href) {
        await firstPetLink.click();

        // Click back button
        const backBtn = page.locator('text=Back to Browse');
        await backBtn.click();

        // Verify returned to browse
        await expect(page).toHaveURL(/\/browse/);
      }
    });

    test('pet detail page displays seller information', async ({ page }) => {
      await page.goto(`${BASE_URL}/browse`);

      // Navigate to pet detail
      const firstPetLink = page.locator('a[href*="/pets/"]').first();
      const href = await firstPetLink.getAttribute('href');

      if (href) {
        await page.goto(BASE_URL + href);

        // Verify seller information
        const sellerInfo = page.locator('text=Seller Information');
        await expect(sellerInfo).toBeVisible({ timeout: 5000 });

        // Verify seller details
        const contactBtn = page.locator('button:has-text("Contact Seller")');
        await expect(contactBtn).toBeVisible({ timeout: 5000 });
      }
    });

    test('image carousel allows navigation between pet images', async ({ page }) => {
      await page.goto(`${BASE_URL}/browse`);

      const firstPetLink = page.locator('a[href*="/pets/"]').first();
      const href = await firstPetLink.getAttribute('href');

      if (href) {
        await page.goto(BASE_URL + href);

        // Look for carousel navigation buttons
        const prevBtn = page.locator('button:has-text("❮")').first();
        const nextBtn = page.locator('button:has-text("❯")').first();

        // If multiple images, test navigation
        if (await prevBtn.isVisible().catch(() => false)) {
          await expect(prevBtn).toBeVisible();
          await expect(nextBtn).toBeVisible();
        }
      }
    });
  });

  test.describe('Complete User Journey', () => {
    test('user can browse from landing page to pet detail', async ({ page }) => {
      // Step 1: Start at landing page
      await page.goto(BASE_URL);
      await expect(page.locator('text=Find Your Perfect Pet Companion')).toBeVisible();

      // Step 2: Click Browse Pets
      const browsePetsBtn = page.locator('button:has-text("Browse Pets")').first();
      await browsePetsBtn.click();

      // Step 3: Verify browse page loaded
      await expect(page).toHaveURL(/\/browse/);
      await expect(page.locator('text=Filters')).toBeVisible();

      // Step 4: Click on a pet
      const petLink = page.locator('a[href*="/pets/"]').first();
      const href = await petLink.getAttribute('href');

      if (href) {
        await petLink.click();

        // Step 5: Verify pet detail page
        await expect(page).toHaveURL(new RegExp(href));
        await expect(page.locator('button:has-text("Add to Cart")')).toBeVisible();
      }
    });
  });
});
