package kr.hhplus.be.server.interfaces.order

import kr.hhplus.be.server.application.order.OrderCommand

data class OrderRequest(
    val products: List<OrderItemRequest>,
    val userCouponId: Long?
){
    fun toCommand(userId: Long): OrderCommand {
        return OrderCommand(
            userId = userId,
            items = products.map { it.toCommand() },
            userCouponId = userCouponId
        )
    }
}
