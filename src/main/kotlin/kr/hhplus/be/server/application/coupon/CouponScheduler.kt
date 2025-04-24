package kr.hhplus.be.server.application.coupon

import jakarta.transaction.Transactional
import kr.hhplus.be.server.domain.coupon.CouponRepository
import kr.hhplus.be.server.domain.coupon.CouponStatus
import kr.hhplus.be.server.domain.coupon.UserCouponRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class CouponScheduler(
    private val couponRepository: CouponRepository,
    private val userCouponRepository: UserCouponRepository
) {

    @Scheduled(cron = "0 0 0 * * *") // 매일 자정마다 실행
    @Transactional
    fun expireCoupons() {
        val now = LocalDateTime.now()

        val expiredCouponIds = couponRepository.findExpiredCouponIds(now)

        if (expiredCouponIds.isNotEmpty()) {
            val expiredUserCoupons = userCouponRepository.findAllByCouponIdInAndStatus(
                expiredCouponIds, CouponStatus.AVAILABLE
            )

            expiredUserCoupons.forEach { it.expire() }

            userCouponRepository.saveAll(expiredUserCoupons)
        }
    }
}