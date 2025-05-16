package kr.hhplus.be.server.coupon

import kr.hhplus.be.server.application.coupon.CouponCommand
import kr.hhplus.be.server.application.coupon.CouponService
import kr.hhplus.be.server.domain.coupon.CouponRepository
import kr.hhplus.be.server.domain.coupon.CouponStatus
import kr.hhplus.be.server.domain.coupon.UserCouponRepository
import kr.hhplus.be.server.support.IntegrationTestBase
import kr.hhplus.be.server.support.TestFixtures
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.redisson.api.RedissonClient
import org.redisson.client.codec.StringCodec
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime


class CouponServiceIntegrationTest @Autowired constructor(
    private val couponService: CouponService,
    private val userCouponRepository: UserCouponRepository,
    private val couponRepository: CouponRepository,
    private val redissonClient: RedissonClient,
) : IntegrationTestBase() {


    @Test
    fun `쿠폰을 발급할 수 있다`() {
        //given
        val expiredAt = LocalDateTime.now().plusDays(5)
        couponRepository.save(
            TestFixtures.coupon(
                "테스트 쿠폰",
                10000,
                300,
                expiredAt = expiredAt,
            )
        )
        val command = CouponCommand(userId = 1L, couponId = 1L)

        //when
        val result = couponService.issue(command)

        //then
        assertAll(
            {
                assertThat(result.status).isEqualTo(CouponStatus.AVAILABLE.toString())
                assertThat(result.discountAmount).isEqualTo(10000)
                assertThat(result.expiredAt).isEqualTo(expiredAt)
            }
        )

        val userCoupon = userCouponRepository.findByUserId(1)
        assertThat(userCoupon).isNotEmpty
    }


    @Test
    fun `유저의 쿠폰 목록을 조회할 수 있다`() {
        //given
        couponRepository.save(
            TestFixtures.coupon(
                "테스트 쿠폰",
                10000,
                300
            )
        )
        val userId = 1L
        val command = CouponCommand(userId = 1L, couponId = 1L)
        couponService.issue(command)

        //when
        val result = couponService.getCoupons(userId)

        //then
        assertAll({
            assertThat(result).hasSize(1)
            assertThat(result[0].name).isEqualTo("테스트 쿠폰")
        })
    }



    @Test
    fun `쿠폰 수량이 3개일 때 2개 발급되면 Redis에 1개가 남는다`() {
        // given
        val couponStockKey = "coupon:stock"
        val couponAppliedKey = "coupon:applied"
        val couponPendingKey = "coupon:issued:pending"

        val stockList = redissonClient.getList<String>(couponStockKey, StringCodec.INSTANCE)
        val appliedSet = redissonClient.getSet<String>(couponAppliedKey, StringCodec.INSTANCE)
        val pendingList = redissonClient.getList<String>(couponPendingKey, StringCodec.INSTANCE)

        // Redis 초기화
        stockList.clear()
        appliedSet.clear()
        pendingList.clear()

        // 수량 3개 세팅
        stockList.addAll(listOf("token1", "token2", "token3"))

        // when
        val result1 = couponService.applyCoupon(1L, 100L)
        val result2 = couponService.applyCoupon(2L, 100L)

        // then
        assertThat(result1).isTrue()
        assertThat(result2).isTrue()

        // Redis 재고는 1개 남아야 함
        assertThat(stockList.size).isEqualTo(1)
        assertThat(appliedSet).containsExactlyInAnyOrder("1", "2")
        assertThat(pendingList).containsExactlyInAnyOrder("1:100", "2:100")
    }

}