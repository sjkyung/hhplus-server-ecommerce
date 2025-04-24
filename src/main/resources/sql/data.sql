-- 인덱스 있는 products/stocks 테이블에 100만 건 삽입
SET SESSION cte_max_recursion_depth = 10000000;

-- products_with_index
INSERT INTO products_with_index (name, price)
    WITH RECURSIVE product_seq(n) AS (
        SELECT 1
        UNION ALL
        SELECT n + 1 FROM product_seq WHERE n < 10000000
    )
SELECT
    CONCAT('상품', n),
    FLOOR(RAND() * 900000 + 100000)
FROM product_seq;

-- stocks_with_index (product_id 매핑)
INSERT INTO stocks_with_index (product_id, quantity)
    WITH RECURSIVE stock_seq(n) AS (
        SELECT 1
        UNION ALL
        SELECT n + 1 FROM stock_seq WHERE n < 10000000
    )
SELECT
    n,
    FLOOR(RAND() * 500)
FROM stock_seq;


-- 인덱스 없는 products/stocks 테이블에 100만 건 삽입

-- products_no_index
INSERT INTO products_no_index (name, price)
    WITH RECURSIVE product_seq_no_index(n) AS (
        SELECT 1
        UNION ALL
        SELECT n + 1 FROM product_seq_no_index WHERE n < 1000000
    )
SELECT
    CONCAT('상품', n),
    FLOOR(RAND() * 10000 + 1000)
FROM product_seq_no_index;

-- stocks_no_index (product_id 매핑)
INSERT INTO stocks_no_index (product_id, quantity)
    WITH RECURSIVE stock_seq_no_index(n) AS (
        SELECT 1
        UNION ALL
        SELECT n + 1 FROM stock_seq_no_index WHERE n < 1000000
    )
SELECT
    n,
    FLOOR(RAND() * 500)
FROM stock_seq_no_index;



INSERT INTO orders_no_index (user_id, user_coupon_id, status, total_price)
    WITH RECURSIVE seq AS (
        SELECT 1 AS n
        UNION ALL
        SELECT n + 1 FROM seq WHERE n < 1000000
    )
SELECT
    FLOOR(RAND() * 10000 + 1),                -- user_id: 1~10000
    CASE WHEN RAND() < 0.7 THEN FLOOR(RAND() * 100000 + 1) ELSE NULL END, -- user_coupon_id: 70% 확률로 값 존재
    ELT(FLOOR(RAND() * 3) + 1, 'PENDING', 'COMPLETED', 'CANCEL'),         -- 상태 코드 3종
    FLOOR(RAND() * 100000 + 1000)            -- total_price: 1,000 ~ 100,000
FROM seq;



INSERT INTO orders_with_index (user_id, user_coupon_id, status, total_price)
    WITH RECURSIVE seq AS (
        SELECT 1 AS n
        UNION ALL
        SELECT n + 1 FROM seq WHERE n < 1000000
    )
SELECT
    FLOOR(RAND() * 10000 + 1),
    CASE WHEN RAND() < 0.7 THEN FLOOR(RAND() * 100000 + 1) ELSE NULL END,
    ELT(FLOOR(RAND() * 3) + 1, 'PENDING', 'COMPLETED', 'CANCEL'),
    FLOOR(RAND() * 100000 + 1000)
FROM seq;



INSERT INTO order_item_with_index (order_id, product_id, price, quantity)
WITH RECURSIVE item_seq AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM item_seq WHERE n < 1000000
)
SELECT
    FLOOR(1 + (RAND() * 1000000)),  -- order_id
    FLOOR(1 + (RAND() * 1000000)),  -- product_id
    FLOOR(1000 + (RAND() * 99000)), -- price: 1000~100000
    FLOOR(1 + (RAND() * 10))        -- quantity: 1~10
FROM item_seq;


INSERT INTO order_item_no_index (order_id, product_id, price, quantity)
WITH RECURSIVE item_seq_no_index AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM item_seq_no_index WHERE n < 1000000
)
SELECT
    FLOOR(1 + (RAND() * 1000000)),
    FLOOR(1 + (RAND() * 1000000)),
    FLOOR(1000 + (RAND() * 99000)),
    FLOOR(1 + (RAND() * 10))
FROM item_seq_no_index;




SET SESSION cte_max_recursion_depth = 10000000;
SET @start := UNIX_TIMESTAMP('2025-01-01');
SET @range := 31536000;

INSERT INTO user_coupon_with_index (user_id, coupon_id, status, created_at)
WITH RECURSIVE seq AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 1000000
)
SELECT
    FLOOR(1 + (RAND() * 10000)),                         -- user_id: 1~10000
    FLOOR(1 + (RAND() * 100000)),                        -- coupon_id: 1~100000
    ELT(FLOOR(RAND() * 3) + 1, 'AVAILABLE', 'USED', 'EXPIRED'), -- status: 3종류 중 하나
    FROM_UNIXTIME(@start + FLOOR(RAND() * @range))           -- created_at: 랜덤 날짜
FROM seq;

INSERT INTO user_coupon_no_index (user_id, coupon_id, status, created_at)
WITH RECURSIVE seq AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 1000000
)
SELECT
    FLOOR(1 + (RAND() * 10000)),
    FLOOR(1 + (RAND() * 100000)),
    ELT(FLOOR(RAND() * 3) + 1, 'UNUSED', 'USED', 'EXPIRED'),
    FROM_UNIXTIME(@start + FLOOR(RAND() * @range))
FROM seq;