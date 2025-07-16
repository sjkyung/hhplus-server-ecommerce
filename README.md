## 이커머스 프로젝트
### 요구사항
* [요구사항 문서](https://github.com/sjkyung/hhplus-server-ecommerce/blob/main/docs/%EC%9A%94%EA%B5%AC%EC%82%AC%ED%95%AD%EB%B6%84%EC%84%9D.md)

### 기술 보고서
* [동시성 이슈](https://github.com/sjkyung/hhplus-server-ecommerce/blob/main/docs/%EB%8F%99%EC%8B%9C%EC%84%B1%EC%9D%B4%EC%8A%88%20%EB%B3%B4%EA%B3%A0%EC%84%9C.md)
* [조회 성능](https://github.com/sjkyung/hhplus-server-ecommerce/blob/main/docs/Adr.md)
* [캐시 전략](https://github.com/sjkyung/hhplus-server-ecommerce/blob/main/docs/%EB%A0%88%EB%94%94%EC%8A%A4%20%EC%BA%90%EC%8B%9C%20%EC%84%B1%EB%8A%A5%20%EA%B0%9C%EC%84%A0.md)
* [쿠폰 발급, 랭킹 - 레디스 기반 설계](https://github.com/sjkyung/hhplus-server-ecommerce/blob/main/docs/Redis%20%EA%B8%B0%EB%B0%98%20%EC%8B%A4%EC%8B%9C%EA%B0%84%20%EB%9E%AD%ED%82%B9%20%26%20%EC%BF%A0%ED%8F%B0%20%EB%B0%9C%EA%B8%89%20%EC%8B%9C%EC%8A%A4%ED%85%9C%20%EC%84%A4%EA%B3%84%20%EB%B0%8F%20%EA%B0%9C%EB%B0%9C%20%ED%9A%8C%EA%B3%A0.md)
* [MSA 기반 설계](https://github.com/sjkyung/hhplus-server-ecommerce/blob/main/docs/MSA%20%EB%B6%84%EC%82%B0%20%ED%8A%B8%EB%9E%9C%EC%9E%AD%EC%85%98%20%EC%84%A4%EA%B3%84.md)
* [쿠폰 발급 - 카프카 기반 설계](https://github.com/sjkyung/hhplus-server-ecommerce/blob/main/docs/%EC%BF%A0%ED%8F%B0%20%EB%B0%9C%EA%B8%89%20%EA%B8%B0%EB%8A%A5%20kafka%20%EC%84%A4%EA%B3%84.md)
* [부하테스트](https://github.com/sjkyung/hhplus-server-ecommerce/blob/main/docs/%EC%BF%A0%ED%8F%B0%20%EB%B6%80%ED%95%98%ED%85%8C%EC%8A%A4%ED%8A%B8%20%EA%B2%B0%EA%B3%BC%20%EB%B0%8F%20%EC%9E%A5%EC%95%A0%20%EB%8C%80%EC%9D%91%EB%AC%B8%EC%84%9C.md)

### 다이어그램
* 시퀀스 다이어그램  
[쿠폰](https://github.com/sjkyung/hhplus-server-ecommerce/blob/main/docs/CouponSequenceDiagram.md), [포인트](https://github.com/sjkyung/hhplus-server-ecommerce/blob/main/docs/PointSequenceDiagram.md), [주문/결제](https://github.com/sjkyung/hhplus-server-ecommerce/blob/main/docs/Order-PaymentSequenceDiagram.md), [상품](https://github.com/sjkyung/hhplus-server-ecommerce/blob/main/docs/ProductSequenceDiagram.md)  
* [ERD](https://github.com/sjkyung/hhplus-server-ecommerce/blob/main/docs/EntityRelationshipDiagram.md)
  

#### Running Docker Containers

`local` profile 로 실행하기 위하여 인프라가 설정되어 있는 Docker 컨테이너를 실행해주셔야 합니다.

```bash
docker-compose up -d
```


