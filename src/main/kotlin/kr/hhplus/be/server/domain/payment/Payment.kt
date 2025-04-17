package kr.hhplus.be.server.domain.payment

import kr.hhplus.be.server.domain.base.Timestamped
import java.time.LocalDateTime

data class Payment(
    val id: Long,
    val orderId: Long,
    val status: PaymentStatus,
    val paymentAmount: Long,
    val paidAt: LocalDateTime?,
): Timestamped() {

    companion object {
        fun create(
            orderId: Long,
            paymentAmount: Long,
        ): Payment {
            return Payment(
                id = 0,
                orderId = orderId,
                status = PaymentStatus.SUCCESS,
                paymentAmount = paymentAmount,
                paidAt = LocalDateTime.now()
            )
        }
    }

}