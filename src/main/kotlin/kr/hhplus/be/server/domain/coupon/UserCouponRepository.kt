package kr.hhplus.be.server.domain.coupon

import java.time.LocalDateTime

interface UserCouponRepository{
    fun findByUserId(userId: Long): List<UserCoupon>
    fun findById(userCouponId: Long): UserCoupon
    fun save(userCoupon: UserCoupon): UserCoupon
    fun existsByUserIdAndCouponId(userId: Long, couponId: Long): Boolean
    fun findAllByCouponIdInAndStatus(couponIds: List<Long>, status: CouponStatus): List<UserCoupon>
    fun saveAll(coupons: List<UserCoupon>): List<UserCoupon>
}
