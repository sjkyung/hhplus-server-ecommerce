package kr.hhplus.be.server.infrastructure.order

import org.springframework.data.jpa.repository.JpaRepository

interface JpaOrderRepository: JpaRepository<OrderEntity, Long> {
}