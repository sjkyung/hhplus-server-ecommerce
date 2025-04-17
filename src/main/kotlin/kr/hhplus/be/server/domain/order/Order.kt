package kr.hhplus.be.server.domain.order

import kr.hhplus.be.server.domain.base.Timestamped


data class Order(
    val id: Long,
    val userId: Long,
    val userCouponId: Long?,
    val status : OrderStatus,
    val totalPrice: Long
): Timestamped() {

    companion object{
        fun create(userId: Long,
                   totalPrice: Long,
                   userCouponId: Long?
        ): Order{
            return Order(
                id = 0L,
                userId = userId,
                userCouponId = userCouponId,
                status = OrderStatus.PENDING,
                totalPrice = totalPrice
            )
        }
    }

    fun complete(): Order {
        check (status == OrderStatus.PENDING) {
            throw IllegalStateException("결제가 가능한 상태가 아닙니다.")
        }
        return Order(
            id = id,
            userId = userId,
            userCouponId = userCouponId,
            status = OrderStatus.COMPLETED,
            totalPrice = totalPrice
        )
    }

}