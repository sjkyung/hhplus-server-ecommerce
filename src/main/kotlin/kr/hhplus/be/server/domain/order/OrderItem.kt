package kr.hhplus.be.server.domain.order

import java.time.LocalDateTime


data class OrderItem (
    val orderId: Long,
    val productId: Long,
    val price: Long,
    val quantity: Int,
    val createdAt: LocalDateTime,
){
}