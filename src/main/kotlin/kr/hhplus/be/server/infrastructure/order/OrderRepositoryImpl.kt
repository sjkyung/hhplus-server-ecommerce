package kr.hhplus.be.server.infrastructure.order

import kr.hhplus.be.server.domain.order.Order
import kr.hhplus.be.server.domain.order.OrderRepository
import org.springframework.stereotype.Repository

@Repository
class OrderRepositoryImpl: OrderRepository {
    override fun findById(orderId: Long): Order {
        TODO("Not yet implemented")
    }

    override fun save(order: Order): Order {
        TODO("Not yet implemented")
    }
}