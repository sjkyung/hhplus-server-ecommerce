package kr.hhplus.be.server.infrastructure.payment

import org.springframework.data.jpa.repository.JpaRepository

interface JpaPaymentRepository: JpaRepository<PaymentEntity, Long> {
}