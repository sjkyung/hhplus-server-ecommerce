package kr.hhplus.be.server.infrastructure.stat

import kr.hhplus.be.server.infrastructure.order.OrderItemEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime


interface JpaStatQueryRepository: JpaRepository<OrderItemEntity, Long> {
    @Query("""
        SELECT new kr.hhplus.be.server.infrastructure.stat.ProductStatDto(
            orderItem.productId, SUM(orderItem.quantity)
        )
        FROM OrderItemEntity orderItem, OrderEntity order
        WHERE order.id = orderItem.orderId
        AND order.status = 'COMPLETED'
        AND orderItem.createdAt >= :threeDaysAgo
        GROUP BY orderItem.productId
        ORDER BY SUM(orderItem.quantity) DESC
    """)
    fun findProductSales(@Param("threeDaysAgo",) threeDaysAgo: LocalDateTime,pageable: Pageable): List<ProductStatDto>
}