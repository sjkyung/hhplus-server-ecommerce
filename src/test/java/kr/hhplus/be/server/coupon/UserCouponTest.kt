package kr.hhplus.be.server.domain.coupon

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class UserCouponTest {

    @Test
    fun `쿠폰 발행은 올바른 사용자와 쿠폰 ID로 AVAILABLE 상태의 쿠폰을 생성해야 한다`() {
        // given
        val userId = 1L
        val couponId = 100L

        // when
        val issuedCoupon = UserCoupon.issue(userId, couponId)

        // then
        assertThat(issuedCoupon.userId).isEqualTo(userId)
        assertThat(issuedCoupon.couponId).isEqualTo(couponId)
        assertThat(issuedCoupon.couponStatus).isEqualTo(CouponStatus.AVAILABLE)
        assertThat(issuedCoupon.usedAt).isNull()
    }

    @Test
    fun `isAvailable은 쿠폰 상태가 AVAILABLE일 경우 예외를 발생시켜야 한다`() {
        // given
        val issuedCoupon = UserCoupon.issue(1L, 100L)

        // when & then
        assertThatThrownBy { issuedCoupon.isAvailable() }
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessageContaining("쿠폰이 이미 사용되었거나 만료되었습니다")
    }

    @Test
    fun `isAvailable은 쿠폰 상태가 AVAILABLE이 아닐 경우 예외를 발생시키지 않아야 한다`() {
        // given
        val usedCoupon = UserCoupon(
            userCouponId = 10L,
            userId = 1L,
            couponId = 100L,
            couponStatus = CouponStatus.USED,
            issuedAt = LocalDateTime.now().minusDays(1),
            usedAt = LocalDateTime.now()
        )

        // when & then
        assertThatCode { usedCoupon.isAvailable() }
            .doesNotThrowAnyException()
    }

    @Test
    fun `apply는 AVAILABLE 상태의 쿠폰에 대해 호출되면 USED 상태의 새로운 쿠폰 인스턴스를 반환해야 한다`() {
        // given
        val issuedAt = LocalDateTime.now().minusDays(1)
        val availableCoupon = UserCoupon(
            userCouponId = 10L,
            userId = 1L,
            couponId = 100L,
            couponStatus = CouponStatus.AVAILABLE,
            issuedAt = issuedAt,
            usedAt = null
        )

        // when
        val appliedCoupon = availableCoupon.apply()

        // then
        assertThat(appliedCoupon.couponStatus).isEqualTo(CouponStatus.USED)
        assertThat(appliedCoupon.userId).isEqualTo(availableCoupon.userId)
        assertThat(appliedCoupon.couponId).isEqualTo(availableCoupon.couponId)
        assertThat(appliedCoupon.issuedAt).isEqualTo(availableCoupon.issuedAt)
        assertThat(appliedCoupon.usedAt).isNotNull()
    }

    @Test
    fun `apply는 AVAILABLE 상태가 아닌 쿠폰에 대해 호출되면 예외를 발생시켜야 한다`() {
        // given
        val usedCoupon = UserCoupon(
            userCouponId = 10L,
            userId = 1L,
            couponId = 100L,
            couponStatus = CouponStatus.USED,
            issuedAt = LocalDateTime.now().minusDays(1),
            usedAt = LocalDateTime.now()
        )

        // when & then
        assertThatThrownBy { usedCoupon.apply() }
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessageContaining("쿠폰이 이미 사용 되었거나 만료되었습니다")
    }
}