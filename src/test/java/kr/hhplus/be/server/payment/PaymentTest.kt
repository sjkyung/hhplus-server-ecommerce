package kr.hhplus.be.server.payment

import kr.hhplus.be.server.domain.payment.Payment
import kr.hhplus.be.server.domain.payment.PaymentStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class PaymentTest {

    @Test
    fun `결제를 생성 시 올바른 값 반환 테스트`() {
        // given
        val orderId = 123L
        val paymentAmount = 1000L
        val beforeCreation = LocalDateTime.now()

        // when
        val payment = Payment.create(orderId, paymentAmount)
        val afterCreation = LocalDateTime.now()

        // then
        assertThat(payment.id).isEqualTo(0)
        assertThat(payment.orderId).isEqualTo(orderId)
        assertThat(payment.status).isEqualTo(PaymentStatus.SUCCESS)
        assertThat(payment.paymentAmount).isEqualTo(paymentAmount)
        assertThat(payment.paidAt).isNotNull
        assertThat(payment.paidAt).isBetween(beforeCreation, afterCreation)
    }


}