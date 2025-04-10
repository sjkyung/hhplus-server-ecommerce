 # 🎟️ 쿠폰 목록 조회 시퀀스 다이어그램

```mermaid
sequenceDiagram
    actor User
    participant Coupon

    Note over User, Coupon: 보유 쿠폰 목록 조회
    User->>+Coupon: 쿠폰을 조회 요청
    Coupon->>Coupon: 쿠폰 조회
    Coupon-->>-User: 쿠폰 조회 반환
```


✅ 위 다이어그램은 쿠폰 목록 조회을 보여줍니다.
- `GET /api/v1/coupons`: 보유 쿠폰 목록 조회
---

# 🎟️ 선착순 쿠폰 발급 시퀀스 다이어그램

```mermaid
sequenceDiagram
    actor User
    participant Coupon

    Note over User, Coupon: 쿠폰 발급
    User->>+Coupon: 쿠폰 발급 요청
    alt 쿠폰 수량 없음
        Coupon-->>User: 409 CONFLICT (쿠폰 소진)
    else 쿠폰 수량 있음
        Coupon->>Coupon: 쿠폰 발급
        Coupon-->>-User: 쿠폰 발급 반환
    end

```

✅ 위 다이어그램은 선착순 쿠폰 발급을 보여줍니다.
- `POST /api/v1/coupons/{couponId}/claim`: 선착순 쿠폰 발급 요청


---
# 쿠폰 만료 처리 스케줄러 시퀀스 다이어그램
```mermaid
sequenceDiagram
    participant Scheduler
    participant Coupon


    Scheduler->>+Coupon: 유효기간 만료 대상 쿠폰 조회
    Coupon-->>-Scheduler: 유효기간 만료 대상 쿠폰 리스트 반환

    loop 쿠폰별 처리
        Scheduler->>+Coupon: 쿠폰 만료 변경 요청
        Coupon->>Coupon: 쿠폰 만료 변경
        Coupon-->>-Scheduler: 쿠폰 만료 변경 완료
    end
```
✅ 위 다이어그램은 사용되지 않고 기한이 만료된 쿠폰을 스케줄러가
쿠폰 상태를 만료로 변경하는 흐름을 설명합니다.

---