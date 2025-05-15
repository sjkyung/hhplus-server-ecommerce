package kr.hhplus.be.server.domain.coupon

import java.time.LocalDateTime

interface CouponRepository {
    fun findById(couponId: Long): Coupon
    fun save(coupon: Coupon): Coupon
    fun findByIds(couponIds: List<Long>): List<Coupon>
    fun findWithLockById(couponId: Long): Coupon
    fun findExpiredCouponIds(now: LocalDateTime): List<Long>
    fun checkDuplicate(userId: Long): Boolean
    fun decreaseStock(userId: Long): Boolean
    fun saveToPending(userId: Long, couponId: Long): Boolean
    fun popPendingCoupon(): String?
}