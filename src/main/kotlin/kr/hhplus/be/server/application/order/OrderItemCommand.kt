package kr.hhplus.be.server.application.order


data class OrderItemCommand(
    val productId: Long,
    val quantity: Int
) {
}