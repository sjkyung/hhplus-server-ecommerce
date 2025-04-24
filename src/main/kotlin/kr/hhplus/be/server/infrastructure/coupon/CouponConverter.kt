package kr.hhplus.be.server.infrastructure.coupon

import kr.hhplus.be.server.domain.coupon.Coupon

object CouponConverter {

    fun toDomain(couponEntity: CouponEntity): Coupon {
        return Coupon(
            couponEntity.id,
            couponEntity.name,
            couponEntity.discountAmount,
            couponEntity.quantity,
            couponEntity.expiredAt
        )
    }

    fun toEntity(coupon: Coupon): CouponEntity {
        return CouponEntity(
            coupon.couponId,
            coupon.name,
            coupon.discountAmount,
            coupon.quantity,
            coupon.expiredAt
        )
    }
}