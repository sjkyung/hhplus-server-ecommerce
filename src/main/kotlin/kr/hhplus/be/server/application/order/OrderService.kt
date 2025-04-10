package kr.hhplus.be.server.application.order

import kr.hhplus.be.server.domain.coupon.UserCouponRepository
import kr.hhplus.be.server.domain.order.Order
import kr.hhplus.be.server.domain.order.OrderItemRepository
import kr.hhplus.be.server.domain.order.OrderRepository
import kr.hhplus.be.server.domain.product.ProductRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository,
    private val userCouponRepository: UserCouponRepository,
    private val orderItemRepository: OrderItemRepository
) {

    @Transactional
    fun create(orderCommand: OrderCommand): Order {
        val products = productRepository.findByIds(orderCommand.items.map { it.productId })
        val userCoupon = orderCommand.userCouponId?.let { userCouponRepository.findById(it) }

        orderCommand.items.forEach { item ->
            val product = products.find { it.id == item.productId }
                ?: throw IllegalStateException("해당 상품을 찾을 수 없습니다.")
            product.validateStock(item.quantity)
        }
        userCoupon?.isAvailable()

        val totalPrice = products.sumOf { it.price }

        val order = Order.create(
            orderCommand.userId,
            totalPrice,
            orderCommand.userCouponId,
        )
        val saveOrder = orderRepository.save(order)

        val orderItems = orderCommand.toDomain(order.id,products)

        orderItemRepository.saveAll(orderItems)

        return saveOrder
    }

}