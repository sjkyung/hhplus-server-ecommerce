package kr.hhplus.be.server.infrastructure.order

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "order_item")
class OrderItemEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val orderId: Long,
    val productId: Long,
    val price: Long,
    val quantity: Int,
    val createdAt: LocalDateTime,
) {
}