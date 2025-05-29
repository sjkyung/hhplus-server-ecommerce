package kr.hhplus.be.server.application.coupon


import kr.hhplus.be.server.application.event.CouponIssuedKafkaPublisher
import kr.hhplus.be.server.application.event.CouponIssuedEvent
import kr.hhplus.be.server.domain.coupon.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock

@Service
class CouponService(
    private val couponRepository: CouponRepository,
    private val userCouponRepository: UserCouponRepository,
    private val couponIssuedKafkaPublisher: CouponIssuedKafkaPublisher,
) {

    private val userCouponLock = ConcurrentHashMap <Long, ReentrantLock>()

    fun getCoupons(userId: Long): List<CouponResult> {
        val userCoupons = userCouponRepository.findByUserId(userId)
        val couponIds = userCoupons.map { it.couponId }
        val coupons = couponRepository.findByIds(couponIds)
        return CouponResult.from(userCoupons,coupons)
    }

    @Transactional
    fun issue(couponCommand :CouponCommand): IssueCouponResult {
        val coupon = couponRepository.findById(couponCommand.couponId);
        check(userCouponRepository.existsByUserIdAndCouponId(couponCommand.userId,couponCommand.couponId).not()){
            "이미 발급된 쿠폰입니다."
        }
        val updatedCoupon = coupon.decrease()
        val issuedCoupon = UserCoupon.issue(couponCommand.userId,couponCommand.couponId)
        val saveCoupon = couponRepository.save(updatedCoupon)
        val saveUserCoupon = userCouponRepository.save(issuedCoupon)
        return IssueCouponResult.from(saveUserCoupon,saveCoupon)
    }

    @Transactional
    fun issuePessimistic(couponCommand :CouponCommand): IssueCouponResult {
        val coupon = couponRepository.findWithLockById(couponCommand.couponId);
        check(userCouponRepository.existsByUserIdAndCouponId(couponCommand.userId,couponCommand.couponId).not()){
            "이미 발급된 쿠폰입니다."
        }
        val updatedCoupon = coupon.decrease()
        val issuedCoupon = UserCoupon.issue(couponCommand.userId,couponCommand.couponId)
        val saveCoupon = couponRepository.save(updatedCoupon)
        val saveUserCoupon = userCouponRepository.save(issuedCoupon)
        return IssueCouponResult.from(saveUserCoupon,saveCoupon)
    }


    @Transactional
    fun issueWithLock(couponCommand :CouponCommand): IssueCouponResult {
        val lock = userCouponLock.computeIfAbsent(couponCommand.couponId) { ReentrantLock() }
        lock.lock()
        try {
            val coupon = couponRepository.findById(couponCommand.couponId);
            check(userCouponRepository.existsByUserIdAndCouponId(couponCommand.userId,couponCommand.couponId).not()){
                "이미 발급된 쿠폰입니다."
            }
            val updatedCoupon = coupon.decrease()
            val issuedCoupon = UserCoupon.issue(couponCommand.userId,couponCommand.couponId)
            val saveCoupon = couponRepository.save(updatedCoupon)
            val saveUserCoupon = userCouponRepository.save(issuedCoupon)
            return IssueCouponResult.from(saveUserCoupon,saveCoupon)
        }finally {
            lock.unlock()
        }
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    fun issueSerializable(couponCommand :CouponCommand): IssueCouponResult {
        val coupon = couponRepository.findById(couponCommand.couponId);
        check(userCouponRepository.existsByUserIdAndCouponId(couponCommand.userId,couponCommand.couponId).not()){
            "이미 발급된 쿠폰입니다."
        }
        val updatedCoupon = coupon.decrease()
        val issuedCoupon = UserCoupon.issue(couponCommand.userId,couponCommand.couponId)
        val saveCoupon = couponRepository.save(updatedCoupon)
        val saveUserCoupon = userCouponRepository.save(issuedCoupon)
        return IssueCouponResult.from(saveUserCoupon,saveCoupon)
    }

    fun applyCoupon(userId: Long, couponId: Long): Boolean {
        if (!couponRepository.checkDuplicate(userId)){
            throw IllegalStateException("이미 발급된 쿠폰입니다.")
        }
        if (!couponRepository.decreaseStock(userId)){
            throw IllegalStateException("쿠폰의 수량이 부족합니다.")
        }
        couponRepository.saveToPending(userId, couponId)
        return true
    }


    fun verifyAndPublishEvent(couponCommand :CouponCommand) {
        val coupon = couponRepository.findById(couponCommand.couponId);
        check(userCouponRepository.existsByUserIdAndCouponId(couponCommand.userId,couponCommand.couponId).not()){
            "이미 발급된 쿠폰입니다."
        }
        couponIssuedKafkaPublisher.publishCouponIssuedEvent(CouponIssuedEvent(
            userId = couponCommand.userId,
            couponId = coupon.couponId,
        ))
    }

    @Transactional
    fun issuedCoupon(couponCommand :CouponCommand): IssueCouponResult {
        val coupon = couponRepository.findById(couponCommand.couponId);
        val updatedCoupon = coupon.decrease()
        val issuedCoupon = UserCoupon.issue(couponCommand.userId,couponCommand.couponId)
        val saveCoupon = couponRepository.save(updatedCoupon)
        val saveUserCoupon = userCouponRepository.save(issuedCoupon)
        return IssueCouponResult.from(saveUserCoupon,saveCoupon)
    }

}