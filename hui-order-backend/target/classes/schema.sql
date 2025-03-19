-- 創建使用者表
CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       name VARCHAR(100) NOT NULL,
                       email VARCHAR(100) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL
);

-- 創建商品表
CREATE TABLE items (
                       id VARCHAR(255) PRIMARY KEY,
                       name VARCHAR(100) NOT NULL,
                       description TEXT,
                       price DECIMAL(10, 2) NOT NULL,
                       image_url VARCHAR(255),
                       category VARCHAR(50),
                       stock INTEGER NOT NULL DEFAULT 0
);

-- 創建訂單表
CREATE TABLE orders (
                        id BIGSERIAL PRIMARY KEY,
                        user_id BIGINT NOT NULL REFERENCES users(id),
                        total DECIMAL(10, 2) NOT NULL,
                        status VARCHAR(50) NOT NULL,
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        shipping_name VARCHAR(100),
                        shipping_address VARCHAR(255),
                        shipping_city VARCHAR(100),
                        shipping_state VARCHAR(100),
                        shipping_zip_code VARCHAR(20)
);

-- 創建訂單項目表
CREATE TABLE order_items (
                             id BIGSERIAL PRIMARY KEY,
                             order_id BIGINT NOT NULL REFERENCES orders(id),
                             item_id VARCHAR(255) NOT NULL REFERENCES items(id),
                             item_name VARCHAR(100) NOT NULL,
                             quantity INTEGER NOT NULL,
                             price DECIMAL(10, 2) NOT NULL
);

-- 在常用查詢欄位上創建索引
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_items_category ON items(category);
CREATE INDEX idx_orders_user_id ON orders(user_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_order_items_item_id ON order_items(item_id);

-- 觸發器來自動更新 orders 表的 updated_at 欄位
CREATE OR REPLACE FUNCTION update_modified_column()
RETURNS TRIGGER AS $$
BEGIN
   NEW.updated_at = CURRENT_TIMESTAMP;
RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_orders_modtime
    BEFORE UPDATE ON orders
    FOR EACH ROW
    EXECUTE FUNCTION update_modified_column();

-- 庫存管理觸發器：減少物品庫存量當訂單建立時
CREATE OR REPLACE FUNCTION decrease_item_stock()
RETURNS TRIGGER AS $$
BEGIN
UPDATE items SET stock = stock - NEW.quantity
WHERE id = NEW.item_id;
RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER trg_decrease_stock
    AFTER INSERT ON order_items
    FOR EACH ROW
    EXECUTE FUNCTION decrease_item_stock();

-- 訂單總額自動計算
CREATE OR REPLACE FUNCTION calculate_order_total()
RETURNS TRIGGER AS $$
BEGIN
UPDATE orders SET total = (
    SELECT SUM(price * quantity) FROM order_items
    WHERE order_id = NEW.order_id
)
WHERE id = NEW.order_id;
RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER trg_calculate_total
    AFTER INSERT OR UPDATE OR DELETE ON order_items
    FOR EACH ROW
    EXECUTE FUNCTION calculate_order_total();