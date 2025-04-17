package kr.hhplus.be.server.application.order

import kr.hhplus.be.server.domain.order.OrderItem
import kr.hhplus.be.server.domain.product.Product
import java.time.LocalDateTime


data class OrderCommand(
    val userId: Long,
    val items: List<OrderItemCommand>,
    val userCouponId: Long?
) {
    fun toDomain(
        orderId: Long,
        products: List<Product>
    ): List<OrderItem> {
        val productMap = products.associateBy { it.id }
        return items.map { item ->
            val product = productMap[item.productId]!!
            OrderItem(
                orderId,
                item.productId,
                product.price,
                item.quantity,
                LocalDateTime.now(),
            )
        }
    }
}