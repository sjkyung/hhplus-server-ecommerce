package kr.hhplus.be.server.interfaces.coupon

import kr.hhplus.be.server.application.coupon.IssueCouponResult
import kr.hhplus.be.server.domain.coupon.UserCoupon
import java.time.LocalDateTime

data class CouponIssueResponse(
    val couponId: Long,
    val discountAmount: Long,
    val status: String,
    val usedAt: LocalDateTime?,
    val expiredAt: LocalDateTime,
    val createdAt: LocalDateTime,
){
    companion object{
        fun from(issueCouponResult: IssueCouponResult): CouponIssueResponse {
            return CouponIssueResponse(
                issueCouponResult.couponId,
                issueCouponResult.discountAmount,
                issueCouponResult.status,
                issueCouponResult.usedAt,
                issueCouponResult.expiredAt,
                issueCouponResult.createdAt
            )
        }
    }
}
