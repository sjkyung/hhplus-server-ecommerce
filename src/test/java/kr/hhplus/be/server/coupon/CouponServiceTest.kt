package kr.hhplus.be.server.coupon

import kr.hhplus.be.server.application.coupon.CouponCommand
import kr.hhplus.be.server.application.coupon.CouponService
import kr.hhplus.be.server.application.coupon.IssueCouponResult
import kr.hhplus.be.server.domain.coupon.Coupon
import kr.hhplus.be.server.domain.coupon.CouponRepository
import kr.hhplus.be.server.domain.coupon.UserCoupon
import kr.hhplus.be.server.domain.coupon.UserCouponRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.doThrow
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import java.lang.RuntimeException

class CouponServiceTest {

    private lateinit var couponRepository: CouponRepository
    private lateinit var userCouponRepository: UserCouponRepository
    private lateinit var couponService: CouponService

    @BeforeEach
    fun init() {
        couponRepository = mock(CouponRepository::class.java)
        userCouponRepository = mock(UserCouponRepository::class.java)
        couponService = CouponService(couponRepository, userCouponRepository)
    }

    @Test
    fun `정상 쿠폰 발급 시 issued coupon 반환 테스트`() {
        // given
        val couponCommand = CouponCommand(couponId = 100L, userId = 10L)
        val coupon = mock(Coupon::class.java)
        `when`(couponRepository.findById(100L)).thenReturn(coupon)
        doNothing().`when`(coupon).decrease()
        `when`(couponRepository.save(coupon)).thenReturn(coupon)

        val expectedIssuedCoupon = UserCoupon.issue(10L, 100L)
        `when`(userCouponRepository.save(any(UserCoupon::class.java))).thenReturn(expectedIssuedCoupon)

        // when
        val result = couponService.issue(couponCommand)

        // then
        assertThat(result).isEqualTo(IssueCouponResult.from(expectedIssuedCoupon,coupon))
        verify(couponRepository).findById(100L)
        verify(coupon).decrease()
        verify(couponRepository).save(coupon)
        verify(userCouponRepository).save(any(UserCoupon::class.java))
    }

    @Test
    fun `쿠폰 decrease 호출 시 예외 발생 테스트`() {
        // given
        val couponCommand = CouponCommand(couponId = 100L, userId = 10L)
        val mockCoupon = mock(Coupon::class.java)
        `when`(couponRepository.findById(100L)).thenReturn(mockCoupon)
        doThrow(IllegalStateException("쿠폰 재고 부족")).`when`(mockCoupon).decrease()

        // when & then
        assertThatThrownBy { couponService.issue(couponCommand) }
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessageContaining("쿠폰 재고 부족")

        verify(couponRepository).findById(100L)
        verify(mockCoupon).decrease()
        verify(couponRepository, never()).save(any(Coupon::class.java))
        verify(userCouponRepository, never()).save(any(UserCoupon::class.java))
    }

    @Test
    fun `쿠폰 저장 실패 시 예외 발생 테스트`() {
        // given
        val couponCommand = CouponCommand(couponId = 100L, userId = 10L)
        val coupon = mock(Coupon::class.java)
        `when`(couponRepository.findById(100L)).thenReturn(coupon)
        doNothing().`when`(coupon).decrease()
        // couponRepository.save() 호출 시 예외 발생
        `when`(couponRepository.save(coupon)).thenThrow(RuntimeException("쿠폰 저장 실패"))

        // when & then
        assertThatThrownBy { couponService.issue(couponCommand) }
            .isInstanceOf(RuntimeException::class.java)
            .hasMessageContaining("쿠폰 저장 실패")

        verify(couponRepository).findById(100L)
        verify(coupon).decrease()
        verify(couponRepository).save(coupon)
        verify(userCouponRepository, never()).save(any(UserCoupon::class.java))
    }

    @Test
    fun `발급 쿠폰 저장 실패 시 예외 발생 테스트`() {
        // given
        val couponCommand = CouponCommand(couponId = 100L, userId = 10L)
        val coupon = mock(Coupon::class.java)
        `when`(couponRepository.findById(100L)).thenReturn(coupon)
        doNothing().`when`(coupon).decrease()
        `when`(couponRepository.save(coupon)).thenReturn(coupon)

        `when`(userCouponRepository.save(any(UserCoupon::class.java)))
            .thenThrow(RuntimeException("발급 쿠폰 저장 실패"))

        // when & then
        assertThatThrownBy { couponService.issue(couponCommand) }
            .isInstanceOf(RuntimeException::class.java)
            .hasMessageContaining("발급 쿠폰 저장 실패")

        verify(couponRepository).findById(100L)
        verify(coupon).decrease()
        verify(couponRepository).save(coupon)
        verify(userCouponRepository).save(any(UserCoupon::class.java))
    }
}