package kr.hhplus.be.server.interfaces.order

import kr.hhplus.be.server.application.order.OrderCommand
import kr.hhplus.be.server.domain.order.Order
import java.time.LocalDateTime

data class OrderResponse(
    val orderId: Long,
    val userId: Long,
    val totalPrice: Long,
    val orderedAt: LocalDateTime,
){
    companion object {
        fun from(order: Order): OrderResponse {
            return OrderResponse(
                order.id,
                order.userId,
                order.totalPrice,
                orderedAt = LocalDateTime.now()
            )
        }
    }
}
