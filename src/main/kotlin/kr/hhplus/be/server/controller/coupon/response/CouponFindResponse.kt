package kr.hhplus.be.server.controller.coupon.response

import java.time.LocalDateTime

data class CouponFindResponse(
    val couponId: Long,
    val name: String,
    val discountAmount: Long,
    val expiredAt: LocalDateTime
)
