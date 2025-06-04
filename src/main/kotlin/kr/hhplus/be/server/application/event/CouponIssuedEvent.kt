package kr.hhplus.be.server.application.event

data class CouponIssuedEvent(
    val couponId: Long,
    val userId: Long
) {
}