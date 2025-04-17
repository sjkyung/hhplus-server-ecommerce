package kr.hhplus.be.server.coupon

import kr.hhplus.be.server.application.coupon.CouponCommand
import kr.hhplus.be.server.application.coupon.CouponService
import kr.hhplus.be.server.domain.coupon.CouponRepository
import kr.hhplus.be.server.domain.coupon.UserCouponRepository
import kr.hhplus.be.server.support.IntegrationTestBase
import kr.hhplus.be.server.support.TestFixtures
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class CouponConcurrentTest @Autowired constructor(
    private val couponService: CouponService,
    private val userCouponRepository: UserCouponRepository,
    private val couponRepository: CouponRepository
) : IntegrationTestBase() {

    @Test
    fun `동시에 쿠폰을 발급하면 중복 발급되어 에러가 발생할 수 있다`() {
        val userId = 1L
        val couponId = 1L
        val coupon = TestFixtures.coupon("동시성 쿠폰", 1000, 100)
        couponRepository.save(coupon)

        val command = CouponCommand(userId = userId, couponId = couponId)

        TestFixtures.runConcurrently(100, Runnable {
            try {
                couponService.issue(command)
            } catch (e: Exception) {
                println("에러 발생: ${e.javaClass.simpleName} - ${e.message}")
            }
        })

        val userCoupons = userCouponRepository.findByUserId(userId)
        println("발급된 쿠폰 수: ${userCoupons.size}")

        // 의도적으로 실패: 쿠폰이 1개만 있어야 정상
        assertThat(userCoupons.size).isEqualTo(1)
    }
}