package kr.hhplus.be.server.infrastructure.order

import org.springframework.data.jpa.repository.JpaRepository

interface JpaOrderItemRepository: JpaRepository<OrderItemEntity, Long> {
    fun findByOrderId(orderId: Long): List<OrderItemEntity>
}