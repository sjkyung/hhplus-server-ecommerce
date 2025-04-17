package kr.hhplus.be.server.infrastructure.order

import kr.hhplus.be.server.domain.order.OrderItem
import kr.hhplus.be.server.domain.order.OrderItemRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class OrderItemRepositoryImpl(
    private val jpaOrderItemRepository: JpaOrderItemRepository
): OrderItemRepository {
    @Transactional
    override fun saveAll(orderItem: List<OrderItem>): List<OrderItem> {
        val orderItemEntities = jpaOrderItemRepository.saveAll(
            OrderItemConverter.toEntityList(orderItem)
        )

        return OrderItemConverter.toDomainList(orderItemEntities)
    }

    override fun findByOrderId(orderId: Long): List<OrderItem> {
        val orderItemEntity= jpaOrderItemRepository.findByOrderId(orderId)
        return OrderItemConverter.toDomainList(orderItemEntity)
    }
}