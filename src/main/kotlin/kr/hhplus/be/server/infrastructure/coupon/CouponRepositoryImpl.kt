package kr.hhplus.be.server.infrastructure.coupon

import kr.hhplus.be.server.domain.coupon.Coupon
import kr.hhplus.be.server.domain.coupon.CouponRepository
import org.springframework.stereotype.Repository

@Repository
class CouponRepositoryImpl: CouponRepository {

    override fun findById(couponId: Long): Coupon {
        TODO("Not yet implemented")
    }

    override fun save(coupon: Coupon): Coupon {
        TODO("Not yet implemented")
    }

    override fun findByIds(couponIds: List<Long>): List<Coupon> {
        TODO("Not yet implemented")
    }
}
