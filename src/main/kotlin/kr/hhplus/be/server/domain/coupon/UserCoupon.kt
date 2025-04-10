package kr.hhplus.be.server.domain.coupon

import java.time.LocalDateTime

data class UserCoupon(
    val userCouponId: Long,
    val userId: Long,
    val couponId: Long,
    val couponStatus: CouponStatus,
    val issuedAt: LocalDateTime,
    val usedAt: LocalDateTime?,
) {
    companion object{
        fun issue(userId: Long,
                  couponId: Long)
                : UserCoupon {
            return UserCoupon(
                userCouponId = 0L,
                userId = userId,
                couponId = couponId,
                couponStatus = CouponStatus.AVAILABLE,
                issuedAt = LocalDateTime.now(),
                usedAt = null
            )
        }
    }

    fun isAvailable(){
        check(couponStatus == CouponStatus.AVAILABLE){
            throw IllegalStateException("쿠폰이 이미 사용되었거나 만료되었습니다.")
        }
    }

    fun apply(): UserCoupon {
        check(couponStatus == CouponStatus.AVAILABLE){
            "쿠폰이 이미 사용 되었거나 만료되었습니다."
        }
        return UserCoupon(
            userCouponId = userCouponId,
            userId = userId,
            couponId = couponId,
            couponStatus = CouponStatus.USED,
            issuedAt,
            usedAt = LocalDateTime.now(),
        )
    }
}