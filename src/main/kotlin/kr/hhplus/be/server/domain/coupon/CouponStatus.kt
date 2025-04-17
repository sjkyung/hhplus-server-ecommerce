package kr.hhplus.be.server.domain.coupon

enum class CouponStatus(val description: String) {
    AVAILABLE("사용가능"),
    USED("사용"),
    EXPIRED("만료"),
}