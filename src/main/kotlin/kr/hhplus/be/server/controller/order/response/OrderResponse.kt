package kr.hhplus.be.server.controller.order.response

import java.time.LocalDateTime

data class OrderResponse(
    val orderId: Long,
    val userId: Long,
    val totalPrice: Long,
    val orderedAt: LocalDateTime,
)
