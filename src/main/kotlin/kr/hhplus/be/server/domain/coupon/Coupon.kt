package kr.hhplus.be.server.domain.coupon

import java.time.LocalDateTime

data class Coupon(
    val couponId: Long,
    val name: String,
    val discountAmount: Long,
    var quantity : Long,
    val expiredAt : LocalDateTime,
) {
    fun decrease(): Coupon {
        check(quantity > 0){"쿠폰의 수량이 부족합니다"}
        return Coupon(
            couponId = couponId,
            name = name,
            discountAmount = discountAmount,
            quantity = quantity - 1,
            expiredAt = expiredAt
        )
    }

    fun calculateDiscountAmount(
        orderAmount: Long
    ): Long{
        require(orderAmount > 0){ "주문 금액이 0보다 작을 수 없습니다." }
        check(orderAmount >= discountAmount){ "할인된 금액이 주문 금액보다 클 수 없습니다." }
        return orderAmount - discountAmount
    }
}