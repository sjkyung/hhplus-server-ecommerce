package kr.hhplus.be.server.interfaces.payment

import kr.hhplus.be.server.domain.payment.Payment
import java.time.LocalDateTime

data class PaymentResponse(
    val orderId: Long,
    val status: String,
    val amount: Long,
    val paidAt: LocalDateTime,
){
    companion object {
        fun from(payment : Payment): PaymentResponse {
            return PaymentResponse(
                orderId = payment.orderId,
                status = payment.status.toString(),
                amount = payment.paymentAmount,
                paidAt = payment.createdAt
            )
        }
    }
}
