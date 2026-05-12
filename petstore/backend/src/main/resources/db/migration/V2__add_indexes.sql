-- V2__add_indexes.sql
-- Performance indexes for Petstore platform

-- Users indexes
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_is_active ON users(is_active);
CREATE INDEX idx_users_created_at ON users(created_at DESC);
CREATE INDEX idx_users_email_verified ON users(email_verified);

-- Sellers indexes
CREATE INDEX idx_sellers_user_id ON sellers(user_id);
CREATE INDEX idx_sellers_is_verified ON sellers(is_verified);
CREATE INDEX idx_sellers_is_active ON sellers(is_active);
CREATE INDEX idx_sellers_created_at ON sellers(created_at DESC);

-- Pets indexes
CREATE INDEX idx_pets_seller_id ON pets(seller_id);
CREATE INDEX idx_pets_status ON pets(status);
CREATE INDEX idx_pets_species ON pets(species);
CREATE INDEX idx_pets_breed ON pets(breed);
CREATE INDEX idx_pets_created_at ON pets(created_at DESC);
CREATE INDEX idx_pets_price ON pets(price);

-- Full-text search indexes for pet discovery
CREATE INDEX idx_pets_name_tsvector ON pets USING GIN(to_tsvector('english', name));
CREATE INDEX idx_pets_description_tsvector ON pets USING GIN(to_tsvector('english', description));

-- Pet images indexes
CREATE INDEX idx_pet_images_pet_id ON pet_images(pet_id);
CREATE INDEX idx_pet_images_is_primary ON pet_images(is_primary);

-- Cart indexes
CREATE INDEX idx_carts_user_id ON carts(user_id);
CREATE INDEX idx_carts_updated_at ON carts(updated_at DESC);

-- Cart items indexes
CREATE INDEX idx_cart_items_cart_id ON cart_items(cart_id);
CREATE INDEX idx_cart_items_pet_id ON cart_items(pet_id);

-- Orders indexes
CREATE INDEX idx_orders_user_id ON orders(user_id);
CREATE INDEX idx_orders_seller_id ON orders(seller_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_payment_status ON orders(payment_status);
CREATE INDEX idx_orders_created_at ON orders(created_at DESC);

-- Order items indexes
CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_order_items_pet_id ON order_items(pet_id);

-- Payments indexes
CREATE INDEX idx_payments_order_id ON payments(order_id);
CREATE INDEX idx_payments_status ON payments(status);
CREATE INDEX idx_payments_stripe_payment_intent_id ON payments(stripe_payment_intent_id);
CREATE INDEX idx_payments_created_at ON payments(created_at DESC);

-- Reviews indexes
CREATE INDEX idx_reviews_pet_id ON reviews(pet_id);
CREATE INDEX idx_reviews_seller_id ON reviews(seller_id);
CREATE INDEX idx_reviews_reviewer_id ON reviews(reviewer_id);
CREATE INDEX idx_reviews_order_id ON reviews(order_id);
CREATE INDEX idx_reviews_rating ON reviews(rating);
CREATE INDEX idx_reviews_created_at ON reviews(created_at DESC);

-- Seller reviews indexes
CREATE INDEX idx_seller_reviews_seller_id ON seller_reviews(seller_id);
CREATE INDEX idx_seller_reviews_reviewer_id ON seller_reviews(reviewer_id);
CREATE INDEX idx_seller_reviews_rating ON seller_reviews(rating);

-- Wishlists indexes
CREATE INDEX idx_wishlists_user_id ON wishlists(user_id);
CREATE INDEX idx_wishlists_is_public ON wishlists(is_public);

-- Wishlist items indexes
CREATE INDEX idx_wishlist_items_wishlist_id ON wishlist_items(wishlist_id);
CREATE INDEX idx_wishlist_items_pet_id ON wishlist_items(pet_id);

-- Seller profiles indexes
CREATE INDEX idx_seller_profiles_seller_id ON seller_profiles(seller_id);

-- Seller earnings indexes
CREATE INDEX idx_seller_earnings_seller_id ON seller_earnings(seller_id);
CREATE INDEX idx_seller_earnings_order_id ON seller_earnings(order_id);
CREATE INDEX idx_seller_earnings_status ON seller_earnings(status);
CREATE INDEX idx_seller_earnings_created_at ON seller_earnings(created_at DESC);

-- Payouts indexes
CREATE INDEX idx_payouts_seller_id ON payouts(seller_id);
CREATE INDEX idx_payouts_status ON payouts(status);
CREATE INDEX idx_payouts_stripe_payout_id ON payouts(stripe_payout_id);
CREATE INDEX idx_payouts_period ON payouts(period_start, period_end);
CREATE INDEX idx_payouts_created_at ON payouts(created_at DESC);

-- Shipping addresses indexes
CREATE INDEX idx_shipping_addresses_user_id ON shipping_addresses(user_id);
CREATE INDEX idx_shipping_addresses_is_primary ON shipping_addresses(is_primary);

-- Audit logs indexes
CREATE INDEX idx_audit_logs_entity ON audit_logs(entity_type, entity_id);
CREATE INDEX idx_audit_logs_user_id ON audit_logs(user_id);
CREATE INDEX idx_audit_logs_created_at ON audit_logs(created_at DESC);
