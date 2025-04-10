package kr.hhplus.be.server.interfaces.order


import kr.hhplus.be.server.application.order.OrderItemCommand

data class OrderItemRequest(
    val productId: Long,
    val quantity: Int
){
    fun toCommand(): OrderItemCommand {
        return OrderItemCommand(
            productId,
            quantity
        )
    }
}
