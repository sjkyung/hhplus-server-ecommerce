package kr.hhplus.be.server.domain.order

interface OrderItemRepository {
    fun saveAll(orderItem: List<OrderItem>): List<OrderItem>
    fun findByOrderId(orderId: Long): List<OrderItem>
}