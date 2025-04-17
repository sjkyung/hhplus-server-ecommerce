package kr.hhplus.be.server.order

import kr.hhplus.be.server.domain.order.Order
import kr.hhplus.be.server.domain.order.OrderStatus
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class OrderTest {

    @Test
    fun `주문 생성 시 대기 상태 확인 테스트`() {
        // given
        val userId = 1L
        val totalPrice = 5000L
        val userCouponId = 100L

        // when
        val order = Order.create(userId, totalPrice, userCouponId)

        // then
        assertThat(order.id).isEqualTo(0L)
        assertThat(order.userId).isEqualTo(userId)
        assertThat(order.userCouponId).isEqualTo(userCouponId)
        assertThat(order.status).isEqualTo(OrderStatus.PENDING)
        assertThat(order.totalPrice).isEqualTo(totalPrice)
    }

    @Test
    fun `주문 대기 상태에서 complete 호출 시 완료 상태로 변경 테스트`() {
        // given
        val order = Order.create(userId = 1L, totalPrice = 5000L, userCouponId = 100L)

        // when
        val completedOrder = order.complete()

        // then
        assertThat(completedOrder.status).isEqualTo(OrderStatus.COMPLETED)
        assertThat(completedOrder.id).isEqualTo(order.id)
        assertThat(completedOrder.userId).isEqualTo(order.userId)
        assertThat(completedOrder.userCouponId).isEqualTo(order.userCouponId)
        assertThat(completedOrder.totalPrice).isEqualTo(order.totalPrice)
    }

    @Test
    fun `주문이 대기 상태가 아닌 경우 complete 호출 시 예외 발생 테스트`() {
        // given
        val order = Order.create(userId = 1L, totalPrice = 5000L, userCouponId = 100L)
        val completedOrder = order.complete()

        // when & then
        assertThatThrownBy {
            completedOrder.complete()
        }.isInstanceOf(IllegalStateException::class.java)
            .hasMessageContaining("결제가 가능한 상태가 아닙니다.")
    }
}