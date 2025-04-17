package kr.hhplus.be.server.infrastructure.coupon

import kr.hhplus.be.server.domain.coupon.UserCoupon
import kr.hhplus.be.server.domain.coupon.UserCouponRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class UserCouponRepositoryImpl(
    private val jpaUserCouponRepository: JpaUserCouponRepository
): UserCouponRepository {

    override fun findByUserId(userId: Long): List<UserCoupon> {
        return jpaUserCouponRepository.findByUserId(userId).map{
            UserCouponConverter.toDomain(it)
        }
    }

    override fun findById(userCouponId: Long): UserCoupon {
        return jpaUserCouponRepository.findById(userCouponId).orElseThrow()
            .let {
                UserCouponConverter.toDomain(it)
            }
    }

    @Transactional
    override fun save(userCoupon: UserCoupon): UserCoupon {
        val userCouponEntity = jpaUserCouponRepository.save(
            UserCouponConverter.toEntity(userCoupon)
        )
        return  UserCouponConverter.toDomain(userCouponEntity)
    }
}