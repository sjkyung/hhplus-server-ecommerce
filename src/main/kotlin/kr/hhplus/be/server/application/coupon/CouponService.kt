package kr.hhplus.be.server.application.coupon

import kr.hhplus.be.server.domain.coupon.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CouponService(
    private val couponRepository: CouponRepository,
    private val userCouponRepository: UserCouponRepository
) {

    fun getCoupons(userId: Long): List<CouponResult> {
        val userCoupons = userCouponRepository.findByUserId(userId)
        val couponIds = userCoupons.map { it.couponId }
        val coupons = couponRepository.findByIds(couponIds)
        return CouponResult.from(userCoupons,coupons)
    }

    @Transactional
    fun issue(couponCommand :CouponCommand): IssueCouponResult {
        val coupon = couponRepository.findById(couponCommand.couponId);
        coupon.decrease()
        val issuedCoupon = UserCoupon.issue(couponCommand.userId,couponCommand.couponId)
        val saveCoupon = couponRepository.save(coupon)
        val saveUserCoupon = userCouponRepository.save(issuedCoupon)
        return IssueCouponResult.from(saveUserCoupon,saveCoupon)
    }
}