package kr.hhplus.be.server.coupon

import kr.hhplus.be.server.application.coupon.CouponCommand
import kr.hhplus.be.server.application.coupon.CouponService
import kr.hhplus.be.server.domain.coupon.CouponRepository
import kr.hhplus.be.server.domain.coupon.UserCouponRepository
import kr.hhplus.be.server.infrastructure.coupon.JpaUserCouponRepository
import kr.hhplus.be.server.support.IntegrationTestBase
import kr.hhplus.be.server.support.TestFixtures
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import java.util.Collections

class CouponConcurrentTest @Autowired constructor(
    private val couponService: CouponService,
    private val userCouponRepository: UserCouponRepository,
    private val jpaCouponRepository: JpaUserCouponRepository,
    private val couponRepository: CouponRepository
) : IntegrationTestBase() {

    @Test
    fun `재고가 100개인 쿠폰을 1명의 유저가 동시에 3번 발급 요청하면 중복 발급이 발생하고 재고는 1개만 줄어든다`() {
        val userId = 1L
        val couponId = 1L
        val coupon = TestFixtures.coupon("동시성 쿠폰", 1000, 100)
        couponRepository.save(coupon)

        val command = CouponCommand(userId = userId, couponId = couponId)

        TestFixtures.runConcurrently(3, Runnable {
            try {
                couponService.issue(command)
            } catch (e: Exception) {
                println("에러 발생: ${e.javaClass.simpleName} - ${e.message}")
            }
        })

        val checkCoupon = couponRepository.findById(couponId)
        val userCoupons = userCouponRepository.findByUserId(userId)

        println("쿠폰 재고 수: ${checkCoupon.quantity}")
        println("발급된 쿠폰 수: ${userCoupons.size}")

        // 쿠폰 재고는 99개만 있어야 정상
        assertThat(checkCoupon.quantity).isEqualTo(99)
        // 의도적으로 실패: 쿠폰이 1개만 있어야 정상
        assertThat(userCoupons.size).isEqualTo(1)
    }

    @Test
    fun `비관적 락을 사용하여 동시에 쿠폰을 3개 발급 요청하면 1개만 발급된다`() {
        val userId = 1L
        val couponId = 1L
        val coupon = TestFixtures.coupon("동시성 쿠폰", 1000, 100)
        couponRepository.save(coupon)

        val command = CouponCommand(userId = userId, couponId = couponId)

        TestFixtures.runConcurrently(3, Runnable {
            try {
                couponService.issuePessimistic(command)
            } catch (e: Exception) {
                println("에러 발생: ${e.javaClass.simpleName} - ${e.message}")
            }
        })

        val checkCoupon = couponRepository.findById(couponId)
        val userCoupons = userCouponRepository.findByUserId(userId)
        println("쿠폰 재고 수: ${checkCoupon.quantity}")
        println("발급된 쿠폰 수: ${userCoupons.size}")

        // 쿠폰 재고는 99개만 있어야 정상
        assertThat(checkCoupon.quantity).isEqualTo(99)
        // 쿠폰이 1개만 있어야 정상
        assertThat(userCoupons.size).isEqualTo(1)
    }


    @Test
    fun `ReentrantLock 락을 사용하여 동시에 쿠폰을 3개 발급 요청하면 1개만 발급된다`() {
        val userId = 1L
        val couponId = 1L
        val coupon = TestFixtures.coupon("동시성 쿠폰", 1000, 100)
        couponRepository.save(coupon)

        val command = CouponCommand(userId = userId, couponId = couponId)

        TestFixtures.runConcurrently(3, Runnable {
            try {
                couponService.issueWithLock(command)
            } catch (e: Exception) {
                println("에러 발생: ${e.javaClass.simpleName} - ${e.message}")
            }
        })

        val checkCoupon = couponRepository.findById(couponId)
        val userCoupons = userCouponRepository.findByUserId(userId)
        println("쿠폰 재고 수: ${checkCoupon.quantity}")
        println("발급된 쿠폰 수: ${userCoupons.size}")

        // 쿠폰 재고는 99개만 있어야 정상
        assertThat(checkCoupon.quantity).isEqualTo(99)
        // 쿠폰이 1개만 있어야 정상
        assertThat(userCoupons.size).isEqualTo(1)
    }

    @Test
    fun `Serializable 트랜잭션에서 동시에 쿠폰 발급하면 데드락 발생 후 1개만 발급된다`() {
        val userId = 1L
        val couponId = 1L
        val coupon = TestFixtures.coupon("동시성 쿠폰", 1000, 100)
        couponRepository.save(coupon)

        val command = CouponCommand(userId = userId, couponId = couponId)

        TestFixtures.runConcurrently(3, Runnable {
            try {
                couponService.issueSerializable(command)
            } catch (e: Exception) {
                println("에러 발생: ${e.javaClass.simpleName} - ${e.message}")
            }
        })

        val checkCoupon = couponRepository.findById(couponId)
        val userCoupons = userCouponRepository.findByUserId(userId)
        println("쿠폰 재고 수: ${checkCoupon.quantity}")
        println("발급된 쿠폰 수: ${userCoupons.size}")

        // 쿠폰 재고는 99개만 있어야 정상
        assertThat(checkCoupon.quantity).isEqualTo(99)
        // 쿠폰이 1개만 있어야 정상
        assertThat(userCoupons.size).isEqualTo(1)
    }

    @Test
    fun `재고가 50개인 쿠폰을 100명의 유저가 동시에 발급 요청하면 재고가 맞지 않는다`() {

        val couponId = 1L
        val coupon = TestFixtures.coupon("동시성 쿠폰", 1000, 50)
        couponRepository.save(coupon)

        val userIds = (1L..100L)
        val tasks = userIds.map { userId ->
            Runnable {
                try {
                    val command = CouponCommand(userId = userId, couponId = couponId)
                    couponService.issue(command)
                } catch (e: Exception) {
                    println("에러 발생: ${e.javaClass.simpleName} - ${e.message}")
                }
            }
        }.toTypedArray()

        TestFixtures.runTaskConcurrently(*tasks)

        val checkCoupon = couponRepository.findById(couponId)
        println("쿠폰 재고 수: ${checkCoupon.quantity}")

        // 쿠폰 재고는 0개만 있어야 정상
        assertThat(checkCoupon.quantity).isEqualTo(0)
    }


    @Test
    fun `비관적 락을 사용하여 재고가 50개인 쿠폰을 100명의 유저가 동시에 발급 요청하면 재고는 0개가 된다`() {

        val couponId = 1L
        val coupon = TestFixtures.coupon("동시성 쿠폰", 1000, 50)
        couponRepository.save(coupon)

        // 예외 수집용 리스트 (동기화 필요)
        val exceptions = Collections.synchronizedList(mutableListOf<Exception>())
        val userIds = (1L..100L)
        val tasks = userIds.map { userId ->
            Runnable {
                try {
                    val command = CouponCommand(userId = userId, couponId = couponId)
                    couponService.issuePessimistic(command)
                } catch (e: Exception) {
                    exceptions.add(e)
                    println("에러 발생: ${e.javaClass.simpleName} - ${e.message}")
                }
            }
        }.toTypedArray()

        TestFixtures.runTaskConcurrently(*tasks)

        val checkCoupon = couponRepository.findById(couponId)
        val issuedCoupon = jpaCouponRepository.findAll()
        println("쿠폰 재고 수: ${checkCoupon.quantity}")
        println("발급 쿠폰 수: ${issuedCoupon.size}")
        println("예외 발생 수: ${exceptions.size}")


        assertAll({
            // 쿠폰 재고는 0개만 있어야 정상
            assertThat(checkCoupon.quantity).isEqualTo(0)
            // 발급 쿠폰 50개만 있어야 정상
            assertThat(issuedCoupon.size).isEqualTo(50)
            // 예외 발생 수는 50개가 있어야 정상
            assertThat(exceptions.size).isEqualTo(50)
        })
    }


    @Test
    fun `ReentrantLock을 사용하여 재고가 50개인 쿠폰을 100명의 유저가 동시에 발급 요청하면 재고는 0개가 된다`() {

        val couponId = 1L
        val coupon = TestFixtures.coupon("동시성 쿠폰", 1000, 50)
        couponRepository.save(coupon)

        // 예외 수집용 리스트 (동기화 필요)
        val exceptions = Collections.synchronizedList(mutableListOf<Exception>())
        val userIds = (1L..100L)
        val tasks = userIds.map { userId ->
            Runnable {
                try {
                    val command = CouponCommand(userId = userId, couponId = couponId)
                    couponService.issueWithLock(command)
                } catch (e: Exception) {
                    exceptions.add(e)
                    println("에러 발생: ${e.javaClass.simpleName} - ${e.message}")
                }
            }
        }.toTypedArray()

        TestFixtures.runTaskConcurrently(*tasks)

        val checkCoupon = couponRepository.findById(couponId)
        println("쿠폰 재고 수: ${checkCoupon.quantity}")
        val userCoupon = jpaCouponRepository.findAll()
        println("발급 쿠폰 수: ${userCoupon.size}")


        assertAll({
            // 쿠폰 재고는 0개만 있어야 정상
            assertThat(checkCoupon.quantity).isEqualTo(0)
            // 예외 발생 수는 50개가 있어야 정상
            assertThat(userCoupon.size).isEqualTo(50)
        })
    }
}