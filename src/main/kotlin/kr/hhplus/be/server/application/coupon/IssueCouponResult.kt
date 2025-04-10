package kr.hhplus.be.server.application.coupon

import kr.hhplus.be.server.domain.coupon.Coupon
import kr.hhplus.be.server.domain.coupon.UserCoupon
import java.time.LocalDateTime

data class IssueCouponResult(
    val couponId: Long,
    val name: String,
    val discountAmount: Long,
    val status: String,
    val usedAt: LocalDateTime?,
    val expiredAt: LocalDateTime,
    val createdAt: LocalDateTime
) {
    companion object{
        fun from(
            userCoupon: UserCoupon,
            coupon: Coupon
        ):IssueCouponResult{
            return IssueCouponResult(
                userCoupon.userCouponId,
                coupon.name,
                coupon.discountAmount,
                userCoupon.couponStatus.toString(),
                userCoupon.usedAt,
                coupon.expiredAt,
                userCoupon.issuedAt
            )
        }
    }
}