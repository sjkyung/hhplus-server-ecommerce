package kr.hhplus.be.server.application.coupon

data class CouponCommand(
    val couponId: Long,
    val userId : Long,
)
