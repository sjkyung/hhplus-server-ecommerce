package kr.hhplus.be.server.controller.order.request

data class OrderRequest(
    val products: List<OrderItemRequest>,
    val couponId: Long?
)
