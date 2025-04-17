package kr.hhplus.be.server.infrastructure.order

import kr.hhplus.be.server.domain.order.OrderItem

object OrderItemConverter {

    fun toDomain(orderItemEntity: OrderItemEntity): OrderItem {
        return OrderItem(
            orderItemEntity.orderId,
            orderItemEntity.productId,
            orderItemEntity.price,
            orderItemEntity.quantity,
            orderItemEntity.createdAt
        )
    }

    fun toEntity(orderItem: OrderItem): OrderItemEntity {
        return OrderItemEntity(
            orderId = orderItem.orderId,
            productId = orderItem.productId,
            price = orderItem.price,
            quantity = orderItem.quantity,
            createdAt = orderItem.createdAt
        )
    }

    fun toEntityList(domains: List<OrderItem>): List<OrderItemEntity> {
        return domains.map { toEntity(it) }
    }

    fun toDomainList(entities: List<OrderItemEntity>): List<OrderItem> {
        return entities.map { toDomain(it) }
    }
}