package kr.hhplus.be.server.interfaces.coupon

import kr.hhplus.be.server.application.coupon.CouponResult
import java.time.LocalDateTime

data class CouponFindResponse(
    val userCouponId: Long,
    val name: String,
    val status: String,
    val discountAmount: Long,
    val issuedAt: LocalDateTime,
    val usedAt: LocalDateTime?,
    val expiredAt: LocalDateTime
) {
    companion object {
        fun from(couponResult: CouponResult): CouponFindResponse {
            return CouponFindResponse(
                couponResult.userCouponId,
                couponResult.name,
                couponResult.status,
                couponResult.discountAmount,
                couponResult.issuedAt,
                couponResult.usedAt,
                couponResult.expiredAt
            )
        }
    }
}
