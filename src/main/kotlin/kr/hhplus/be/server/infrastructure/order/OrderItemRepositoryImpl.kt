package kr.hhplus.be.server.infrastructure.order

import kr.hhplus.be.server.domain.order.OrderItem
import kr.hhplus.be.server.domain.order.OrderItemRepository
import org.springframework.stereotype.Repository

@Repository
class OrderItemRepositoryImpl: OrderItemRepository {
    override fun saveAll(orderItem: List<OrderItem>): List<OrderItem> {
        TODO("Not yet implemented")
    }

    override fun findByOrderId(orderId: Long): List<OrderItem> {
        TODO("Not yet implemented")
    }
}