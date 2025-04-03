package kr.hhplus.be.server.controller.order.request

data class OrderItemRequest(
    val productId: Long,
    val quantity: Long
)
