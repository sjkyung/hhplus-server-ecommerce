package kr.hhplus.be.server.infrastructure.coupon

import kr.hhplus.be.server.domain.coupon.Coupon
import kr.hhplus.be.server.domain.coupon.CouponRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class CouponRepositoryImpl(
    private val jpaCouponRepository: JpaCouponRepository,
    private val couponRedisRepository: RedisCouponRepository
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

    override fun findWithLockById(couponId: Long): Coupon {
        return CouponConverter.toDomain(
            jpaCouponRepository.findWithLockById(couponId)
        )
    }

    override fun findExpiredCouponIds(now: LocalDateTime): List<Long> {
        TODO("Not yet implemented")
    }

    override fun checkDuplicate(userId: Long): Boolean {
        return couponRedisRepository.checkDuplicate(userId)
    }

    override fun decreaseStock(userId: Long): Boolean {
        return couponRedisRepository.decreaseStock(userId)
    }

    override fun saveToPending(userId: Long, couponId: Long): Boolean {
        return couponRedisRepository.saveToPending(userId, couponId)
    }

    override fun popPendingCoupon(): String? {
        return couponRedisRepository.popPendingCoupon()
    }
}
