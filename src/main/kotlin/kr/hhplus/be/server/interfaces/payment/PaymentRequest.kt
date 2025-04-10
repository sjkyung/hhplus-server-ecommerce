package kr.hhplus.be.server.interfaces.payment

import kr.hhplus.be.server.application.payment.PaymentCommand

data class PaymentRequest(
    val orderId: Long,
){
    fun toCommand(): PaymentCommand {
        return PaymentCommand(orderId)
    }
}
