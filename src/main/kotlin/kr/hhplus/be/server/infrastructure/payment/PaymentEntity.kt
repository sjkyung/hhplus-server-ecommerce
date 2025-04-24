package kr.hhplus.be.server.infrastructure.payment

import jakarta.persistence.*
import kr.hhplus.be.server.domain.payment.PaymentStatus
import java.time.LocalDateTime

@Entity
@Table(name = "payment")
class PaymentEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val orderId: Long,
    @Enumerated(EnumType.STRING)
    val status: PaymentStatus,
    val paymentAmount: Long,
    val paidAt: LocalDateTime? = null,
) {
}