-- V4__seed_data.sql
-- Seed data for development and testing
-- This migration adds sample users, sellers, and pets for manual testing

-- Insert sample admin/test users
INSERT INTO users (id, email, password_hash, first_name, last_name, is_active, email_verified, email_verified_at)
VALUES 
  ('550e8400-e29b-41d4-a716-446655440001', 'admin@petstore.local', '$2a$10$N9qo8uLOickgx2ZMRZoMye', 'Admin', 'User', true, true, NOW()),
  ('550e8400-e29b-41d4-a716-446655440002', 'seller@petstore.local', '$2a$10$N9qo8uLOickgx2ZMRZoMye', 'John', 'Seller', true, true, NOW()),
  ('550e8400-e29b-41d4-a716-446655440003', 'buyer@petstore.local', '$2a$10$N9qo8uLOickgx2ZMRZoMye', 'Jane', 'Buyer', true, true, NOW())
ON CONFLICT (email) DO NOTHING;

-- Insert sample sellers
INSERT INTO sellers (id, user_id, business_name, is_verified, verified_at, is_active)
VALUES 
  ('650e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440002', 'Happy Pets Farm', true, NOW(), true)
ON CONFLICT (user_id) DO NOTHING;

-- Insert sample seller profiles
INSERT INTO seller_profiles (id, seller_id, bio, response_time_hours, return_policy)
VALUES 
  (
    '750e8400-e29b-41d4-a716-446655440001',
    '650e8400-e29b-41d4-a716-446655440001',
    'Welcome to Happy Pets Farm! We specialize in raising healthy, happy puppies and kittens.',
    24,
    '30-day return policy for health-related issues with veterinary documentation.'
  )
ON CONFLICT (seller_id) DO NOTHING;

-- Insert sample pets
INSERT INTO pets (id, seller_id, name, species, breed, age_years, age_months, gender, description, price, quantity_available, status)
VALUES 
  (
    '850e8400-e29b-41d4-a716-446655440001',
    '650e8400-e29b-41d4-a716-446655440001',
    'Buddy',
    'Dog',
    'Golden Retriever',
    1,
    6,
    'Male',
    'Friendly and energetic Golden Retriever puppy. Fully vaccinated and ready for a loving home.',
    1200.00,
    1,
    'active'
  ),
  (
    '850e8400-e29b-41d4-a716-446655440002',
    '650e8400-e29b-41d4-a716-446655440001',
    'Luna',
    'Cat',
    'Persian',
    0,
    4,
    'Female',
    'Adorable Persian kitten with beautiful white fur. Very calm and affectionate.',
    800.00,
    2,
    'active'
  ),
  (
    '850e8400-e29b-41d4-a716-446655440003',
    '650e8400-e29b-41d4-a716-446655440001',
    'Max',
    'Dog',
    'Labrador',
    2,
    0,
    'Male',
    'Well-trained Labrador ready to be a family companion. Excellent with children.',
    1500.00,
    1,
    'active'
  )
ON CONFLICT (id) DO NOTHING;

-- Insert sample pet images
INSERT INTO pet_images (id, pet_id, image_url, is_primary)
VALUES 
  ('950e8400-e29b-41d4-a716-446655440001', '850e8400-e29b-41d4-a716-446655440001', '/images/buddy-1.jpg', true),
  ('950e8400-e29b-41d4-a716-446655440002', '850e8400-e29b-41d4-a716-446655440002', '/images/luna-1.jpg', true),
  ('950e8400-e29b-41d4-a716-446655440003', '850e8400-e29b-41d4-a716-446655440003', '/images/max-1.jpg', true)
ON CONFLICT (id) DO NOTHING;

-- Insert sample shopping carts (empty)
INSERT INTO carts (id, user_id, total_items, total_price)
VALUES 
  ('a50e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440003', 0, 0.00)
ON CONFLICT (user_id) DO NOTHING;

-- Insert sample shipping addresses
INSERT INTO shipping_addresses (id, user_id, label, street_address, city, state_province, postal_code, country, is_primary)
VALUES 
  (
    'b50e8400-e29b-41d4-a716-446655440001',
    '550e8400-e29b-41d4-a716-446655440003',
    'Home',
    '123 Main Street',
    'San Francisco',
    'CA',
    '94102',
    'USA',
    true
  )
ON CONFLICT (id) DO NOTHING;

-- Insert sample wishlists
INSERT INTO wishlists (id, user_id, name, is_public)
VALUES 
  ('c50e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440003', 'Dream Pets', false)
ON CONFLICT (id) DO NOTHING;

-- Insert sample wishlist items
INSERT INTO wishlist_items (id, wishlist_id, pet_id)
VALUES 
  ('d50e8400-e29b-41d4-a716-446655440001', 'c50e8400-e29b-41d4-a716-446655440001', '850e8400-e29b-41d4-a716-446655440001')
ON CONFLICT (wishlist_id, pet_id) DO NOTHING;

-- Insert sample seller reviews
INSERT INTO seller_reviews (id, seller_id, reviewer_id, rating, comment)
VALUES 
  (
    'e50e8400-e29b-41d4-a716-446655440001',
    '650e8400-e29b-41d4-a716-446655440001',
    '550e8400-e29b-41d4-a716-446655440003',
    5,
    'Excellent seller! Very responsive and the pet arrived healthy and happy.'
  )
ON CONFLICT (id) DO NOTHING;
