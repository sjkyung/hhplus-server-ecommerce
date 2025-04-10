package kr.hhplus.be.server.infrastructure.coupon

import kr.hhplus.be.server.domain.coupon.UserCoupon
import kr.hhplus.be.server.domain.coupon.UserCouponRepository
import org.springframework.stereotype.Repository

@Repository
class UserCouponRepositoryImpl: UserCouponRepository {
    override fun findByUserId(userId: Long): List<UserCoupon> {
        TODO("Not yet implemented")
    }

    override fun findById(userCouponId: Long): UserCoupon {
        TODO("Not yet implemented")
    }

    override fun save(userCoupon: UserCoupon): UserCoupon {
        TODO("Not yet implemented")
    }
}