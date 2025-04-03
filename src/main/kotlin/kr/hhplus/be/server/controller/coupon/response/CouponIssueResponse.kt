package kr.hhplus.be.server.controller.coupon.response

import java.time.LocalDateTime

data class CouponIssueResponse(
    val couponId: Long,
    val discountAmount: Long,
    val status: String,
    val usedAt: LocalDateTime?,
    val expiredAt: LocalDateTime,
    val createdAt: LocalDateTime,
)
