package kr.hhplus.be.server.infrastructure.coupon

import kr.hhplus.be.server.domain.coupon.UserCoupon
import org.springframework.data.jpa.repository.JpaRepository

interface JpaUserCouponRepository : JpaRepository<UserCouponEntity, Long> {
    fun findByUserId(userId: Long): List<UserCouponEntity>
}