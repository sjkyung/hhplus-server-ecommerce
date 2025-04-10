package kr.hhplus.be.server.infrastructure.payment

import kr.hhplus.be.server.domain.payment.Payment
import kr.hhplus.be.server.domain.payment.PaymentRepository
import org.springframework.stereotype.Repository

@Repository
class PaymentRepositoryImpl: PaymentRepository {
    override fun save(payment: Payment): Payment {
        TODO("Not yet implemented")
    }
}