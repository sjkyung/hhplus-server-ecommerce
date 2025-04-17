package kr.hhplus.be.server.infrastructure.coupon

import kr.hhplus.be.server.domain.coupon.UserCoupon

object UserCouponConverter {

    fun toDomain(userCouponEntity: UserCouponEntity): UserCoupon{
        return UserCoupon(
            userCouponId = userCouponEntity.id,
            userId = userCouponEntity.userId,
            couponId = userCouponEntity.couponId,
            couponStatus = userCouponEntity.status,
            issuedAt = userCouponEntity.createdAt,
            usedAt = userCouponEntity.usedAt
        )
    }

    fun toEntity(userCoupon: UserCoupon): UserCouponEntity{
        return UserCouponEntity(
            id = userCoupon.userCouponId,
            userId = userCoupon.userId,
            couponId = userCoupon.couponId,
            status = userCoupon.couponStatus,
            usedAt = userCoupon.usedAt,
            createdAt = userCoupon.issuedAt
        )
    }
}