-- ========== 인덱스 없는 테이블 ==========

CREATE TABLE products_no_index (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                name VARCHAR(255) NOT NULL,
                                price BIGINT NOT NULL
);

CREATE TABLE stocks_no_index (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                product_id BIGINT NOT NULL,
                                quantity INT NOT NULL
);

CREATE TABLE coupon_no_index (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                name VARCHAR(255) NOT NULL,
                                discount_amount BIGINT NOT NULL,
                                quantity BIGINT NOT NULL,
                                expired_at DATETIME NOT NULL
);

CREATE TABLE user_coupon_no_index (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                user_id BIGINT NOT NULL,
                                coupon_id BIGINT NOT NULL,
                                status VARCHAR(50) NOT NULL,
                                used_at DATETIME,
                                created_at DATETIME NOT NULL,
                                updated_at DATETIME
);

CREATE TABLE orders_no_index (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                user_id BIGINT NOT NULL,
                                user_coupon_id BIGINT,
                                status VARCHAR(50) NOT NULL,
                                total_price BIGINT NOT NULL
);

CREATE TABLE order_item_no_index (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                order_id BIGINT NOT NULL,
                                product_id BIGINT NOT NULL,
                                price BIGINT NOT NULL,
                                quantity INT NOT NULL
);

CREATE TABLE user_point_no_index (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                user_id BIGINT NOT NULL,
                                point BIGINT NOT NULL
);

CREATE TABLE payment_no_index (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                order_id BIGINT NOT NULL,
                                status VARCHAR(50) NOT NULL,
                                payment_amount BIGINT NOT NULL,
                                paid_at DATETIME
);

-- ========== 인덱스 있는 테이블 ==========

CREATE TABLE products_with_index (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                name VARCHAR(255) NOT NULL,
                                price BIGINT NOT NULL,
                                INDEX idx_price (price)
);

CREATE TABLE stocks_with_index (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                product_id BIGINT NOT NULL,
                                quantity INT NOT NULL,
                                INDEX idx_product_id_quantity (product_id, quantity)
);

CREATE TABLE coupon_with_index (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                name VARCHAR(255) NOT NULL,
                                discount_amount BIGINT NOT NULL,
                                quantity BIGINT NOT NULL,
                                expired_at DATETIME NOT NULL,
                                INDEX idx_expired_at (expired_at)
);

CREATE TABLE user_coupon_with_index (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                user_id BIGINT NOT NULL,
                                coupon_id BIGINT NOT NULL,
                                status VARCHAR(50) NOT NULL,
                                used_at DATETIME,
                                created_at DATETIME NOT NULL,
                                updated_at DATETIME,
                                INDEX idx_user_coupon (user_id, coupon_id),
                                INDEX idx_status_usedat (status, used_at)
);

CREATE TABLE orders_with_index (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                user_id BIGINT NOT NULL,
                                user_coupon_id BIGINT,
                                status VARCHAR(50) NOT NULL,
                                total_price BIGINT NOT NULL,
                                INDEX idx_user_id_status (user_id, status)
);

CREATE TABLE order_item_with_index (
                                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    order_id BIGINT NOT NULL,
                                    product_id BIGINT NOT NULL,
                                    price BIGINT NOT NULL,
                                    quantity INT NOT NULL,
                                    INDEX idx_order_id (order_id),
                                    INDEX idx_product_id (product_id)
);

CREATE TABLE user_point_with_index (
                                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    user_id BIGINT NOT NULL,
                                    point BIGINT NOT NULL,
                                    INDEX idx_user_id (user_id)
);

CREATE TABLE payment_with_index (
                                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    order_id BIGINT NOT NULL,
                                    status VARCHAR(50) NOT NULL,
                                    payment_amount BIGINT NOT NULL,
                                    paid_at DATETIME,
                                    INDEX idx_order_id (order_id),
                                    INDEX idx_status_paidat (status, paid_at)
);