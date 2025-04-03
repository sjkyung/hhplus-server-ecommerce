# 🛒 주문 시퀀스 다이어그램

```mermaid
sequenceDiagram
    autonumber
    actor User
    participant Order
    participant Product
    participant Coupon
    participant Point

    User->>+Order: 주문 요청

    Order->>+Product: 상품 정보 및 재고 검증 요청
    Product->>Product: 재고 검증
    alt 상품 재고 부족
        Product-->>Order: 재고 부족
        Order-->>User: 실패 응답 (상품 재고 부족)
    else 재고 있음
        Product-->>-Order: 재고 검증 반환
    end


    
    Order->>+Coupon: 쿠폰 검증 요청
    Coupon->>Coupon: 쿠폰 검증
    alt 만료 쿠폰 or 이미 사용된 쿠폰
        Coupon-->>Order: 쿠폰 사용 불가
        Order-->>User: 실패 응답 (쿠폰 사용 불가)
    else 쿠폰 사용 가능
        Coupon-->>-Order: 쿠폰 유효성 확인 반환
    end
    

    Order->>+Point: 잔고 검증 요청
    Point->>Point: 잔고 검증
    alt 잔고 부족
        Point-->>Order: 잔고 부족
        Order-->>User: 실패 응답 (잔고 부족)
    else 충분한 잔고
        Point-->>-Order: 잔고 검증 반환
    end

    Order-->>-User: 주문 생성 완료
```

주문 기능 전체 흐름을 설명합니다.
- `POST /api/v1/orders`: 주문 요청 (상품 목록, 쿠폰, 잔액)
- 주문 전 재고,쿠폰(선택),잔액 확인 -> 주문 생성 완료


---
# 🛒 결제 시퀀스 다이어그램

```mermaid
sequenceDiagram
    autonumber
    actor User
    participant Payment
    participant Order
    participant Product
    participant Coupon
    participant Point
    participant DataPlatForm

    User->>+Payment: 결제를 요청

    Payment->>+Order: 주문정보 상태 확인 요청
    alt 주문이 존재하지 않음
        Order-->>Payment: 주문 존재하지 않음
        Payment-->>User: 실패 응답 (주문 존재하지 않음)
    else 유효한 주문
        Order->>-Payment: 주문 생성 확인 완료
    end

    Payment->>+Product: 상품 재고 차감 요청
    Product->>Product: 재고 차감
    Product-->>-Payment: 재고 차감 완료
    
    opt 쿠폰 사용 시
        Payment->>+Coupon: 쿠폰 사용 처리 요청
        Coupon->>Coupon: 쿠폰 사용
        Coupon-->>-Payment: 쿠폰 사용 완료
    end

    Payment->>+Point: 잔고 차감 요청
    Point->>Point: 잔고 차감
    Point-->>-Payment: 잔고 차감 완료

    Payment->>+Order: 주문 완료 요청
    Order->>Order: 주문 완료 처리
    Order-->>-Payment: 주문 완료

    Payment->>+DataPlatForm: 주문정보 전송
    DataPlatForm-->>-Payment: 전송 확인

    Payment-->>-User: 결제 성공
```

결제 기능 전체 흐름을 설명합니다.
- `POST /api/v1/payments`: 결제 요청 (주문 정보)
- 결제 전 주문 생성 확인 → 잔액 차감 -> 쿠폰 사용 → 재고 차감 → 주문 데이터 전송


---
# 주문 만료 처리 스케줄러 시퀀스 다이어그램 
```mermaid
sequenceDiagram
    participant Scheduler
    participant Order


    Scheduler->>+Order: 만료 대상 주문 조회
    Order-->>-Scheduler: 만료 대상 주문 리스트 반환

    loop 주문별 처리
        Scheduler->>+Order: 주문 상태 변경 요청
        Order->>Order: 주문 상태 변경
        Order-->>-Scheduler: 상태 변경 완료
    end
```
결제되지 않고 일정 시간(15분)이 경과한 주문을 스케줄러가
주문 상태를 만료로 변경하는 흐름을 설명합니다.

---