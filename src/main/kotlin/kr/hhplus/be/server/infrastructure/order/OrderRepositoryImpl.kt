package kr.hhplus.be.server.infrastructure.order

import kr.hhplus.be.server.domain.order.Order
import kr.hhplus.be.server.domain.order.OrderRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class OrderRepositoryImpl(
    private val jpaOrderRepository: JpaOrderRepository
) : OrderRepository {
    override fun findById(orderId: Long): Order {
        return jpaOrderRepository.findById(orderId).orElseThrow()
            .let {
                OrderConverter.toDomain(it)
            }
    }

    @Transactional
    override fun save(order: Order): Order {
        val orderEntity = jpaOrderRepository.save(
            OrderConverter.toEntity(order)
        )
        return OrderConverter.toDomain(orderEntity)
    }
}