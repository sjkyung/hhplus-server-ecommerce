# 📦 상품 조회 시퀀스 다이어그램

```mermaid
sequenceDiagram
    actor User
    participant Product

    User->>+Product: 사용자가 상품 정보를 조회한다.
    Product->>Product: 상품 정보 조회
    Product-->>-User: 상품 정보를 반환 (id, name, price, stock)
```

사용자가 상품 목록을 조회하는 흐름을 설명합니다.
- `GET /api/v1/product` : 전체 상품 목록 조회 요청

--- 
# 📈 상위 인기 상품 조회 시퀀스 다이어그램
```mermaid
sequenceDiagram
    actor  User
    participant Product
    participant Order

    User->>+Product: 사용자가 인기 상품 목록 조회 요청한다.
    Product->>+Order: 최근 3일간 판매량 기준 top 5 조회 요청
    Order->>Order: 최근 3일간 판매량 기준 top5 조회
    Order-->>-Product: 최근 3일간 판매량 기준 top 5 반환
    Product-->>-User: 인기 상품 목록 조회 반환 (id, name, salesCount)
```
최근 3일간 가장 많이 팔린 상위 5개 상품을 조회하는 흐름을 설명합니다.
- `GET /api/v1/products/rank`: 인기 상품 목록 요청
- Order 통해 판매 통계 기반 인기 상품 집계