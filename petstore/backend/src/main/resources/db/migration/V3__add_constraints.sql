-- V3__add_constraints.sql
-- Additional constraints and foreign keys for Petstore platform

-- Add ON DELETE and ON UPDATE rules where needed
-- Note: These are handled in V1__initial_schema.sql, but this migration
-- can be used for future constraint adjustments

-- Add computed column for cart total (for performance)
ALTER TABLE carts ADD COLUMN IF NOT EXISTS computed_total BOOLEAN DEFAULT false;

-- Add status transition audit table
CREATE TABLE IF NOT EXISTS status_transitions (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  entity_type VARCHAR(100) NOT NULL,
  entity_id UUID NOT NULL,
  from_status VARCHAR(50),
  to_status VARCHAR(50) NOT NULL,
  reason TEXT,
  user_id UUID REFERENCES users(id) ON DELETE SET NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_status_transitions_entity (entity_type, entity_id),
  INDEX idx_status_transitions_created_at (created_at DESC)
);

-- Add trigger for audit logging on orders status change
-- (Triggers and procedures can be added here for complex logic)

-- Add sequences for business operations
CREATE SEQUENCE IF NOT EXISTS order_number_seq START WITH 1000 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS invoice_number_seq START WITH 10000 INCREMENT BY 1;

-- Performance: Add partial indexes for common queries
CREATE INDEX IF NOT EXISTS idx_pets_active_by_seller 
  ON pets(seller_id) WHERE status = 'active';

CREATE INDEX IF NOT EXISTS idx_orders_pending 
  ON orders(created_at DESC) WHERE status IN ('pending', 'processing');

CREATE INDEX IF NOT EXISTS idx_orders_completed_by_seller 
  ON orders(seller_id, created_at DESC) WHERE status = 'completed';

CREATE INDEX IF NOT EXISTS idx_seller_earnings_unpaid 
  ON seller_earnings(seller_id) WHERE status IN ('pending', 'processing');

-- Add check constraints for data integrity
ALTER TABLE reviews 
ADD CONSTRAINT check_review_rating 
CHECK (rating >= 1 AND rating <= 5) 
NOT VALID; -- NOT VALID allows concurrent writes during migration

ALTER TABLE seller_reviews 
ADD CONSTRAINT check_seller_review_rating 
CHECK (rating >= 1 AND rating <= 5) 
NOT VALID;

-- Add NOT NULL constraints where business logic requires
ALTER TABLE orders 
ALTER COLUMN user_id SET NOT NULL;

ALTER TABLE orders 
ALTER COLUMN seller_id SET NOT NULL;

ALTER TABLE order_items 
ALTER COLUMN quantity SET NOT NULL;

-- Composite unique constraints for data uniqueness
ALTER TABLE cart_items 
ADD CONSTRAINT unique_cart_item_combination 
UNIQUE(cart_id, pet_id);

-- Add indexes for LEFT JOINs (common in queries)
CREATE INDEX IF NOT EXISTS idx_sellers_null_check 
ON sellers(id) WHERE is_verified = true;

-- Performance: Add materialized view for seller aggregates (optional for Phase 1)
-- This can be created and refreshed periodically
CREATE MATERIALIZED VIEW IF NOT EXISTS seller_aggregates AS
SELECT 
  s.id,
  s.user_id,
  s.business_name,
  COUNT(DISTINCT p.id) as total_pets_listed,
  COUNT(DISTINCT CASE WHEN p.status = 'active' THEN p.id END) as active_pets,
  ROUND(AVG(sr.rating)::numeric, 2) as avg_seller_rating,
  COUNT(DISTINCT sr.id) as total_seller_reviews,
  COUNT(DISTINCT o.id) as total_orders,
  COALESCE(SUM(se.net_amount), 0) as total_earnings
FROM sellers s
LEFT JOIN pets p ON s.id = p.seller_id
LEFT JOIN seller_reviews sr ON s.id = sr.seller_id
LEFT JOIN orders o ON s.id = o.seller_id
LEFT JOIN seller_earnings se ON s.id = se.seller_id AND se.status = 'paid'
GROUP BY s.id, s.user_id, s.business_name;

-- Index for the materialized view
CREATE INDEX IF NOT EXISTS idx_seller_aggregates_id ON seller_aggregates(id);

-- Performance: Add function for calculating cart totals (used by triggers)
CREATE OR REPLACE FUNCTION calculate_cart_total()
RETURNS TRIGGER AS $$
BEGIN
  UPDATE carts
  SET total_price = (
    SELECT COALESCE(SUM(quantity * price_at_add), 0)
    FROM cart_items
    WHERE cart_id = NEW.cart_id
  ),
  total_items = (
    SELECT COALESCE(SUM(quantity), 0)
    FROM cart_items
    WHERE cart_id = NEW.cart_id
  )
  WHERE id = NEW.cart_id;
  
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger for auto-calculating cart totals
DROP TRIGGER IF EXISTS trigger_calculate_cart_total ON cart_items;
CREATE TRIGGER trigger_calculate_cart_total
AFTER INSERT OR UPDATE OR DELETE ON cart_items
FOR EACH ROW
EXECUTE FUNCTION calculate_cart_total();
