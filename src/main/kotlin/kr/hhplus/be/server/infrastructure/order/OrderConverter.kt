package kr.hhplus.be.server.infrastructure.order

import kr.hhplus.be.server.domain.order.Order

object OrderConverter {

    fun toDomain(orderEntity: OrderEntity): Order{
        return Order(
            orderEntity.id,
            orderEntity.userId,
            orderEntity.userCouponId,
            orderEntity.status,
            orderEntity.totalPrice
        )
    }


    fun toEntity(order: Order): OrderEntity {
        return OrderEntity(
            order.id,
            order.userId,
            order.userCouponId,
            order.status,
            order.totalPrice
        )
    }
}