package kr.hhplus.be.server.infrastructure.payment

import kr.hhplus.be.server.domain.payment.Payment

object PaymentConverter {

    fun toDomain(paymentEntity: PaymentEntity): Payment {
        return Payment(
            id = paymentEntity.id,
            orderId = paymentEntity.orderId,
            status = paymentEntity.status,
            paymentAmount = paymentEntity.paymentAmount,
            paidAt = paymentEntity.paidAt,
        )
    }


    fun toEntity(payment: Payment): PaymentEntity {
        return PaymentEntity(
            id = payment.id,
            orderId = payment.orderId,
            status = payment.status,
            paymentAmount = payment.paymentAmount,
            paidAt = payment.paidAt,
        )
    }
}