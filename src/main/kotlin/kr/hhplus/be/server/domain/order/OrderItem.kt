package kr.hhplus.be.server.domain.order


data class OrderItem (
    val orderId: Long,
    val productId: Long,
    val price: Long,
    val quantity: Int,
){
    fun withOrderId(orderId: Long): OrderItem {
        return OrderItem(
            orderId,
            productId,
            price,
            quantity
        )
    }
}