-- Inspect first. Choose one of YOUR demo orders and its current version.
SELECT id, customer_username, status, total, created_at, version
FROM shop_orders ORDER BY id DESC;

-- Replace NULL with that order's ID and version. Defaults change no rows.
SET @order_id = NULL;
SET @expected_version = NULL;
-- Valid values: NEW, CONFIRMED, PREPARING, SHIPPING, DELIVERING,
-- DELIVERED, CANCELLED, RETURNED.
SET @next_status = 'CONFIRMED';

START TRANSACTION;
UPDATE shop_orders
SET status = @next_status, version = version + 1
WHERE id = @order_id AND version = @expected_version
  AND @next_status IN ('NEW','CONFIRMED','PREPARING','SHIPPING',
                       'DELIVERING','DELIVERED','CANCELLED','RETURNED');
SELECT ROW_COUNT() AS changed_rows;
COMMIT;
SELECT id, customer_username, status, version FROM shop_orders WHERE id = @order_id;

-- Refresh /orders and choose the matching filter after each change.
-- This is a status-display demonstration, not a cancellation/refund workflow.
-- It intentionally does not modify inventory, payment or invent transition dates.
