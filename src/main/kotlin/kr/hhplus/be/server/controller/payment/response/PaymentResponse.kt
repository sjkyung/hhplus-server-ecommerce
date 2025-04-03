package kr.hhplus.be.server.controller.payment.response

import java.time.LocalDateTime

data class PaymentResponse(
    val orderId: Long,
    val status: String,
    val amount: Long,
    val paidAt: LocalDateTime,
)
