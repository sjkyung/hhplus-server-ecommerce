package kr.hhplus.be.server.coupon

import kr.hhplus.be.server.domain.coupon.Coupon
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class CouponTest {

    @Test
    fun `decrease 함수 호출 시 쿠폰의 수량이 1 감소해야 한다`() {
        // given
        val coupon = Coupon(
            couponId = 1L,
            name = "테스트 쿠폰",
            discountAmount = 1000L,
            quantity = 10,
            expiredAt = LocalDateTime.now().plusDays(1)
        )
        // when
        val decreasedCoupon = coupon.decrease()

        // then
        assertThat(decreasedCoupon.quantity).isEqualTo(9)
    }

    @Test
    fun `decrease 함수 호출 시 쿠폰 수량이 부족하면 IllegalStateException을 발생시켜야 한다`() {
        // given
        val coupon = Coupon(
            couponId = 1L,
            name = "테스트 쿠폰",
            discountAmount = 1000L,
            quantity = 0,
            expiredAt = LocalDateTime.now().plusDays(1)
        )
        // when & then
        assertThatThrownBy {
            coupon.decrease()
        }.isInstanceOf(IllegalStateException::class.java)
            .hasMessageContaining("쿠폰의 수량이 부족합니다")
    }

    @Test
    fun `calculateDiscountAmount 함수는 주문 금액에서 할인 금액을 차감한 값을 반환해야 한다`() {
        // given
        val coupon = Coupon(
            couponId = 1L,
            name = "테스트 쿠폰",
            discountAmount = 2000L,
            quantity = 5,
            expiredAt = LocalDateTime.now().plusDays(1)
        )
        val orderAmount = 10000L

        // when
        val finalAmount = coupon.calculateDiscountAmount(orderAmount)

        // then
        assertThat(finalAmount).isEqualTo(8000L)
    }

    @Test
    fun `calculateDiscountAmount 함수는 음수 주문 금액에 대해 예외를 발생시켜야 한다`() {
        // given
        val coupon = Coupon(
            couponId = 1L,
            name = "테스트 쿠폰",
            discountAmount = 1000L,
            quantity = 5,
            expiredAt = LocalDateTime.now().plusDays(1)
        )
        val orderAmount = -500L

        // when & then
        assertThatThrownBy { coupon.calculateDiscountAmount(orderAmount) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("주문 금액이 0보다 작을 수 없습니다")
    }

    @Test
    fun `calculateDiscountAmount 할인된 금액은 음수이면 예외를 발생시켜야 한다`() {
        // given
        val coupon = Coupon(
            couponId = 1L,
            name = "테스트 쿠폰",
            discountAmount = 1000L,
            quantity = 5,
            expiredAt = LocalDateTime.now().plusDays(1)
        )
        val orderAmount = 999L

        // when & then
        assertThatThrownBy { coupon.calculateDiscountAmount(orderAmount) }
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessageContaining("할인된 금액이 주문금액보다 클 수 없습니다")
    }
}