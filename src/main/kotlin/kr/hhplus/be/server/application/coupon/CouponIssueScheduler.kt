package kr.hhplus.be.server.application.coupon

import kr.hhplus.be.server.domain.coupon.CouponRepository
import kr.hhplus.be.server.domain.coupon.UserCoupon
import kr.hhplus.be.server.domain.coupon.UserCouponRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class CouponIssueScheduler(
    private val couponRedisRepository: CouponRepository,
    private val userCouponRepository: UserCouponRepository
) {


    @Scheduled(fixedDelay = 1000)
    fun persistIssuedCoupons() {
        repeat (100) {
            val entry = couponRedisRepository.popPendingCoupon() ?: return

            val (userId, couponId) = entry.split(":").map { it.toLong() }
            val userCoupon = UserCoupon.issue(userId, couponId)
            userCouponRepository.save(userCoupon)

            println("발급 저장 완료: userId=$userId, couponId=$couponId")
        }
    }
}