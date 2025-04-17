package kr.hhplus.be.server.domain.order

interface OrderRepository {
    fun findById(orderId: Long): Order
    fun save(order: Order): Order
}