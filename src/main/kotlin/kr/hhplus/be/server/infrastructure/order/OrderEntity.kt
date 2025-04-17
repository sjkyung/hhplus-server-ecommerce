package kr.hhplus.be.server.infrastructure.order

import jakarta.persistence.*
import kr.hhplus.be.server.domain.order.OrderStatus


@Entity
@Table(name = "orders")
class OrderEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,
    val userId: Long,
    val userCouponId: Long?,
    val status : OrderStatus,
    val totalPrice: Long
) {
}