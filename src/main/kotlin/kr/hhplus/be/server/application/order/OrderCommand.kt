package kr.hhplus.be.server.application.order

import kr.hhplus.be.server.domain.order.OrderItem
import kr.hhplus.be.server.domain.product.Product


data class OrderCommand(
    val userId: Long,
    val items: List<OrderItemCommand>,
    val userCouponId: Long?
) {
    fun toDomain(
        orderId: Long,
        products: List<Product>
    ): List<OrderItem> {
        return items.map { item ->
            OrderItem(
                orderId,
                item.productId,
                products.sumOf { it.price },
                item.quantity
            )
        }
    }
}