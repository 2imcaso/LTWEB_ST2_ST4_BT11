-- MySQL 8+. Select the application's existing database before running.
-- Additive only: no DROP, no changes to Users/Videos or their data.
-- Hibernate also maps these tables through META-INF/persistence.xml.
CREATE TABLE IF NOT EXISTS shop_products (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    sku VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    price DECIMAL(15,0) NOT NULL,
    stock INT NOT NULL,
    active BOOLEAN NOT NULL,
    video_id VARCHAR(50) NULL UNIQUE,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT chk_shop_product_stock CHECK (stock >= 0),
    CONSTRAINT chk_shop_product_price CHECK (price > 0)
) ENGINE=InnoDB;

-- Migration for databases that already had shop_products before videos became sellable.
SET @video_column_exists = (SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'shop_products' AND COLUMN_NAME = 'video_id');
SET @add_video_column = IF(@video_column_exists = 0,
    'ALTER TABLE shop_products ADD COLUMN video_id VARCHAR(50) NULL UNIQUE AFTER active',
    'SELECT 1');
PREPARE add_video_column_stmt FROM @add_video_column;
EXECUTE add_video_column_stmt;
DEALLOCATE PREPARE add_video_column_stmt;

CREATE TABLE IF NOT EXISTS shop_orders (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    customer_username VARCHAR(50) NOT NULL,
    checkout_token VARCHAR(36) NOT NULL UNIQUE,
    recipient VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    address VARCHAR(500) NOT NULL,
    note VARCHAR(1000) NOT NULL,
    payment_method VARCHAR(10) NOT NULL DEFAULT 'COD',
    status VARCHAR(32) NOT NULL DEFAULT 'NEW',
    total DECIMAL(24,0) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    INDEX idx_shop_orders_customer_status (customer_username, status, id),
    CONSTRAINT chk_shop_order_total CHECK (total > 0),
    CONSTRAINT chk_shop_order_cod CHECK (payment_method = 'COD'),
    CONSTRAINT chk_shop_order_status CHECK (status IN
        ('NEW','CONFIRMED','PREPARING','SHIPPING','DELIVERING','DELIVERED','CANCELLED','RETURNED'))
) ENGINE=InnoDB;
-- The customer relation is managed by Hibernate. Its FK uses the existing Users
-- table's encoding/collation; do not ALTER the existing Users table for this demo.

CREATE TABLE IF NOT EXISTS shop_order_items (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    sku VARCHAR(50) NOT NULL,
    product_name VARCHAR(150) NOT NULL,
    unit_price DECIMAL(15,0) NOT NULL,
    quantity INT NOT NULL,
    CONSTRAINT uk_shop_order_product UNIQUE (order_id, product_id),
    CONSTRAINT fk_shop_item_order FOREIGN KEY (order_id) REFERENCES shop_orders(id),
    CONSTRAINT chk_shop_item_quantity CHECK (quantity > 0),
    CONSTRAINT chk_shop_item_price CHECK (unit_price > 0)
) ENGINE=InnoDB;
