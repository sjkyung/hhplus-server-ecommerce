# ADR: 조회 기능 성능 분석 및 인덱스 설계

## Status
Accepted

## Context
대용량 데이터에서 다음과 같은 주요 조회 기능이 성능 병목을 유발할 수 있다고 판단됨.  
기능별 조회 조건, 병목 원인, 실제 성능 차이를 실험하고 분석함.

실험 데이터는 인덱스 유무에 따른 성능 차이를 분석하기 위해 총 100만 건 이상을 삽입하였으며, 데이터 생성 시 각 컬럼의 카디널리티를 의도적으로 다르게 설정하였다.

## 1. 조회 기능 리스트업

| 기능               | 테이블              | 조회 조건 / 패턴                          |
|--------------------|----------------------|-------------------------------------------|
| 상품 목록 조회     | products             | price 기준 정렬, 범위 필터, 페이징        |
| 상품 재고 확인     | stocks               | product_id 기준 단건 조회 (조인 대상)     |
| 주문 내역 조회     | orders               | user_id + status 필터링           |
| 주문 상세 조회     | order_items          | order_id 기준 다건 조회                   |
| 사용자 쿠폰 내역   | user_coupon          | user_id + status 필터링                   |


---

## 2. 병목 원인 분석

| 테이블            | 병목 원인 설명                                    |
|-------------------|---------------------------------------------|
| products          | price 인덱스 없으면 정렬/범위 검색 느림                   |
| stocks            | product_id 인덱스 없으면 단건 조회도 full scan         |
| orders            | user_id + status 조건 인덱스 없으면 조건 필터링 위해 전체 스캔 |
| order_items       | order_id 조인 시 인덱스 없으면 매번 테이블 전체 검색          |
| user_coupon       | user_id + status 필터 인덱스 없으면 전체 스캔           |


---

## 3. EXPLAIN / ANALYZE 성능 비교
### 3-1. EXPLAIN 기반 성능 비교 표

| 테이블              | 실행 쿼리 예시                                 | type   | key                  | rows   | Extra                 | 실행 시간  |
|---------------------|------------------------------------------------|--------|-----------------------|--------|-----------------------|------------|
| products_no_index   | `price BETWEEN 5000 AND 6000`                  | ALL    | NULL                  | 997144 | Using where           | 289ms      |
| products_with_index | `price BETWEEN 5000 AND 6000`                  | range  | idx_price             | 1      | Using index condition | 0.12ms     |
| stocks_no_index     | `product_id = 12345`                           | ALL    | NULL                  | 998002 | Using where           | 451ms      |
| stocks_with_index   | `product_id = 12345`                           | ref    | idx_product_id        | 1      | Using index           | 0.2ms      |
| orders_no_index     | `user_id = 100 AND status = 'COMPLETED'`       | ALL    | NULL                  | 996786 | Using where           | 560ms      |
| orders_with_index   | `user_id = 100 AND status = 'COMPLETED'`       | ref    | idx_user_id_status    | 28     | Null                  | 2.49ms     |
| order_items_no_idx  | `order_id = 12345`                              | ALL    | NULL                  | 996630 | Using where           | 329ms      |
| order_items_with_idx| `order_id = 12345`                              | ref    | idx_order_id          | 1      | Null                  | 0.078ms    |
| user_coupon_no_idx  | `user_id = 5 AND status = 'USED'`              | ALL    | NULL                  | 996253 | Using where           | 360ms      |
| user_coupon_with_idx| `user_id = 5 AND status = 'USED'`              | ref    | idx_user_coupon       | 73     | Using where           | 8.77ms     |

---

### 3-2. EXPLAIN ANALYZE 상세 실행 요약

#### `products_no_index`
```
Filter: (products_no_index.price between 5000 and 6000)
(cost=102887 rows=110783) (actual time=2.63..299 rows=99866 loops=1)
Table scan on products_no_index
(cost=102887 rows=997144) (actual time=2.61..262 rows=1e+6 loops=1)
```

#### `products_with_index`
```
Index range scan on products_with_index using idx_price over (5000 <= price <= 6000)
with index condition: (products_with_index.price between 5000 and 6000)
(cost=2.19 rows=1) (actual time=0.123..0.123 rows=0 loops=1)
```

#### `stocks_no_index`
```
Filter: (stocks_no_index.product_id = 12345)
(cost=102396 rows=99800) (actual time=16..451 rows=1 loops=1)
Table scan on stocks_no_index
(cost=102396 rows=998002) (actual time=8.97..416 rows=1e+6 loops=1)
```

#### `stocks_with_index`
```
Covering index lookup on stocks_with_index using idx_product_id_quantity (product_id=12345)
(cost=0.602 rows=1) (actual time=0.205..0.23 rows=1 loops=1)
```

#### `orders_no_index`
```
Filter: ((orders_no_index.status = 'COMPLETED') and (orders_no_index.user_id = 100))
(cost=100936 rows=9968) (actual time=10.1..560 rows=36 loops=1)
Table scan on orders_no_index
(cost=100936 rows=996786) (actual time=0.817..474 rows=1e+6 loops=1)
```

#### `orders_with_index`
```
Index lookup on orders_with_index using idx_user_id_status (user_id=100, status='COMPLETED')
(cost=30.7 rows=28) (actual time=1.88..2.49 rows=28 loops=1)
```

#### `order_item_no_index`
```
Filter: (order_item_no_index.order_id = 12345)
(cost=103284 rows=99663) (actual time=329..329 rows=0 loops=1)
Table scan on order_item_no_index
(cost=103284 rows=996630) (actual time=7.19..293 rows=1e+6 loops=1)
```

#### `order_item_with_index`
```
Index lookup on order_item_with_index using idx_order_id (order_id=12345)
(cost=1.1 rows=1) (actual time=0.0787..0.0787 rows=0 loops=1)
```

#### `user_coupon_no_index`
```
Filter: ((user_coupon_no_index.status = 'USED') and (user_coupon_no_index.user_id = 5))
(cost=101405 rows=9963) (actual time=27.7..360 rows=25 loops=1)
Table scan on user_coupon_no_index
(cost=101405 rows=996253) (actual time=2.58..292 rows=1e+6 loops=1)
```

#### `user_coupon_with_index`
```
Filter: (user_coupon_with_index.status = 'USED')
(cost=76.6 rows=36.5) (actual time=3.66..8.77 rows=22 loops=1)
Index lookup on user_coupon_with_index using idx_user_coupon (user_id=5)
(cost=76.6 rows=73) (actual time=3.65..8.74 rows=73 loops=1)
```


### 3-3. B+Tree 기반 복합 인덱스 탐색 실험

복합 인덱스의 성능을 정량적으로 비교하기 위해 `orders_with_index` 테이블에 다음 인덱스를 생성한 뒤,  
조건 순서에 따른 인덱스 활용 여부와 실행 계획의 차이를 실험하였다.

```sql
CREATE INDEX idx_user_id_status ON orders_with_index(user_id, status);
```

---

#### 실험 쿼리

```sql
-- A. user_id, status 모두 조건 포함 (순서: user_id → status)
EXPLAIN SELECT * FROM orders_with_index WHERE user_id = 100 AND status = 'COMPLETED';
EXPLAIN ANALYZE SELECT * FROM orders_with_index WHERE user_id = 100 AND status = 'COMPLETED';

-- B. 순서를 바꿨지만 같은 조건 (status → user_id)
EXPLAIN SELECT * FROM orders_with_index WHERE status = 'COMPLETED' AND user_id = 100;
EXPLAIN ANALYZE SELECT * FROM orders_with_index WHERE status = 'COMPLETED' AND user_id = 100;

-- C. status 단독 조건
EXPLAIN SELECT * FROM orders_with_index WHERE status = 'COMPLETED';
EXPLAIN ANALYZE SELECT * FROM orders_with_index WHERE status = 'COMPLETED';

-- D. user_id 단독 조건
EXPLAIN SELECT * FROM orders_with_index WHERE user_id = 100;
EXPLAIN ANALYZE SELECT * FROM orders_with_index WHERE user_id = 100;
```

---

### 실행 계획 및 성능 비교

| 조건 조합                         | type  | key                   | rows     | Extra               | 실행 시간  |
|----------------------------------|-------|------------------------|----------|---------------------|----------------------------|
| `user_id = 100 AND status = ...` | ref   | idx_user_id_status     | 28       | Index lookup        | 0.175 ~ 17.9 ms  |
| `status = ... AND user_id = ...` | ref   | idx_user_id_status     | 28       | Index lookup        | 0.175 ~ 0.229 ms           |
| `status = 'COMPLETED'`           | ALL   | null                   | 997,542  | Using where         | 1257 ~ 1455 ms             |
| `user_id = 100`                  | ref   | idx_user_id_status     | 91       | Filter (status)     | 0.737 ~ 704 ms             |

---

### 상세 EXPLAIN ANALYZE 결과 일부

####  복합 조건 (user_id + status)

```text
-> Index lookup on orders_with_index using idx_user_id_status (user_id=100, status='COMPLETED')
(cost=30.6 rows=28) (actual time=0.175..0.229 rows=28 loops=1)
```

####  status 단독 조건

```text
-> Filter: (orders_with_index.status = 'COMPLETED')
(cost=103546 rows=99754) (actual time=8.48..1455 rows=334169 loops=1)
-> Table scan on orders_with_index
(cost=103546 rows=997542) (actual time=8.43..1257 rows=1e+6 loops=1)
```

####  user_id 단독 조건

```text
-> Filter: (orders_with_index.status = 'COMPLETED')
(cost=101281 rows=99754) (actual time=0.755..704 rows=334169 loops=1)
-> Table scan on orders_with_index
(cost=101281 rows=997542) (actual time=0.737..586 rows=1e+6 loops=1)
```

---

###  B+Tree 동작 원리와 해석

- 복합 인덱스 `(user_id, status)`는 **B+Tree** 구조로 정렬되어 있으며,  
  `user_id` 값이 먼저 정렬되기 때문에 `user_id` 조건이 **인덱스 탐색의 진입점 역할**을 함.
- `user_id`가 포함된 조건에서는 인덱스를 탐색할 수 있으나, `status` 단독 조건은 B+Tree의 탐색 경로를 정할 수 없어 **Full Scan** 발생
- `user_id`만 있는 경우에도 인덱스를 사용할 수 있지만, `status`는 인덱스 범위 밖의 조건이라서 추가 Filter가 발생함.

---

### 결론

- 복합 인덱스는 반드시 **선행 컬럼(user_id)** 조건이 포함되어야 인덱스를 사용할 수 있다.
- WHERE 조건의 순서는 실행 계획에 영향을 주지 않음 (MySQL 옵티마이저가 정규화함)
- 단, **조건이 포함되었느냐(특히 선행 컬럼)**는 성능에 절대적인 영향을 끼친다.
- B+Tree 구조상 선행 키부터 탐색하므로, **복합 인덱스 설계 시 컬럼 순서가 가장 중요하다.**

---

B+Tree는 정렬된 키 값으로 이루어진 노드에 실제 row 데이터를 포함하거나 row 포인터를 포함하고 있으며,  
이 덕분에 **Index Range Scan**, **Covering Index Lookup**과 같은 효율적인 실행 계획이 가능하다.

---

## Consequences

- 인덱스가 없는 경우 대부분의 쿼리가 full scan + filter 방식으로 처리되어 응답 속도 200~300ms 이상 소요됨
- 인덱스를 적용하면 대부분의 쿼리가 `range` 또는 `ref` 방식으로 최적화되어 0.1~2ms 수준으로 실행됨
- 데이터가 수백만 건으로 증가하면 성능 차이는 더 커질 수 있음

---

## 향후 고려 중인 구조

현재 products 테이블에 재고 필드(quantity)가 포함되어 있으나,
재고 정보의 실시간 수정이 빈번하고 동시성 이슈가 발생하기 쉬운 영역이라는 점에서 다음과 같은 리팩토링을 고려하고 있음:
stocks 테이블을 별도로 분리하여 product_id와 quantity만을 관리
재고 수량에 대한 동시성 처리 및 업데이트 트랜잭션을 명확히 분리할 수 있음
재고 조회는 커버링 인덱스로 최적화 가능하며, write/read path를 분리함으로써 성능 향상 기대

또한, 트래픽에 민감하거나 배치 기반의 집계가 필요한 조회 성능 향상을 위해:
별도의 통계 전용 테이블(e.g. product_sales_stat)을 분리 생성하는 방식도 고려
N일간 누적 판매 수량 등은 주기적인 배치/스케줄러 또는 트리거를 통해 갱신
이로 인해 주문 테이블에 대한 직접적인 join 없이도 통계성 조회가 가능

이러한 분리는 데이터 모델의 관심사를 명확히 구분하고, 성능 병목 지점에서의 write-lock 충돌과 full scan 비용을 줄이기 위한 구조적 개선으로 이어질 수 있음.