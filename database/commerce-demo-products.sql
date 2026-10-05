-- Optional legacy merchandise catalog. Sellable demo videos are now seeded safely
-- by AppListener and managed from /admin/videos with price and available quantity.
-- Re-running does NOT reset existing prices, stock or orders.
SET NAMES utf8mb4;
START TRANSACTION;
INSERT INTO shop_products (sku, name, price, stock, active, version)
SELECT 'DEMO-BOOK', 'Sách lập trình Java', 120000, 20, TRUE, 0
WHERE NOT EXISTS (SELECT 1 FROM shop_products WHERE sku = 'DEMO-BOOK');
INSERT INTO shop_products (sku, name, price, stock, active, version)
SELECT 'DEMO-NOTEBOOK', 'Sổ ghi chép', 35000, 15, TRUE, 0
WHERE NOT EXISTS (SELECT 1 FROM shop_products WHERE sku = 'DEMO-NOTEBOOK');
INSERT INTO shop_products (sku, name, price, stock, active, version)
SELECT 'DEMO-PEN', 'Bút bi', 10000, 5, TRUE, 0
WHERE NOT EXISTS (SELECT 1 FROM shop_products WHERE sku = 'DEMO-PEN');
COMMIT;
SELECT id, sku, name, price, stock, active, version FROM shop_products ORDER BY id;
