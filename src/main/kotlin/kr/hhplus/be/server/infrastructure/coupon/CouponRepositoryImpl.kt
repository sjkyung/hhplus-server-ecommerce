package kr.hhplus.be.server.infrastructure.coupon

import kr.hhplus.be.server.domain.coupon.Coupon
import kr.hhplus.be.server.domain.coupon.CouponRepository
import org.springframework.stereotype.Repository

@Repository
class CouponRepositoryImpl(
    private val jpaCouponRepository: JpaCouponRepository
): CouponRepository {

    override fun findById(couponId: Long): Coupon {
        return jpaCouponRepository.findById(couponId).orElseThrow().let{
            CouponConverter.toDomain(it)
        }
    }

    override fun save(coupon: Coupon): Coupon {
        val couponEntity= jpaCouponRepository.save(
            CouponConverter.toEntity(coupon)
        )
        return  CouponConverter.toDomain(couponEntity)
    }

    override fun findByIds(couponIds: List<Long>): List<Coupon> {
        val couponEntityList = jpaCouponRepository.findAllById(couponIds)
        return couponEntityList.map{
            CouponConverter.toDomain(it)
        }
    }
}
