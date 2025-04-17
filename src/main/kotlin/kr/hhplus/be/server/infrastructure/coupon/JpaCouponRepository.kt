package kr.hhplus.be.server.infrastructure.coupon

import org.springframework.data.jpa.repository.JpaRepository

interface JpaCouponRepository: JpaRepository<CouponEntity, Long> {
}