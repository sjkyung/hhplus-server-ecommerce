package kr.hhplus.be.server.application.coupon

import kr.hhplus.be.server.domain.coupon.Coupon
import kr.hhplus.be.server.domain.coupon.UserCoupon
import java.time.LocalDateTime

data class CouponResult(
    val userCouponId: Long,
    val name: String,
    val status: String,
    val discountAmount: Long,
    val issuedAt : LocalDateTime,
    val usedAt: LocalDateTime?,
    val expiredAt: LocalDateTime
) {
    companion object {
        fun from(userCoupons: List<UserCoupon>, coupons: List<Coupon>): List<CouponResult> {

            return userCoupons.map { userCoupon ->
                val coupon = coupons.find { it.couponId == userCoupon.couponId }
                CouponResult(
                    userCouponId = userCoupon.userCouponId,
                    name = coupon!!.name,
                    status = userCoupon.couponStatus.toString(),
                    discountAmount = coupon.discountAmount,
                    issuedAt = userCoupon.issuedAt,
                    usedAt = userCoupon.usedAt,
                    expiredAt = coupon.expiredAt
                )
            }
        }
    }
}