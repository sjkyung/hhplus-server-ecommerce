package kr.hhplus.be.server.interfaces.coupon.event

import kr.hhplus.be.server.application.coupon.CouponCommand
import kr.hhplus.be.server.application.coupon.CouponService
import kr.hhplus.be.server.application.event.CouponIssuedEvent
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class CouponIssuedKafkaListener(
    private val couponService: CouponService
) {

    @KafkaListener(topics = ["coupon-issued-topic"])
    @Transactional
    fun consume(event: CouponIssuedEvent) {
        val couponCommand = CouponCommand(
            event.couponId,
            event.userId
        )
        couponService.issuedCoupon(couponCommand)
    }
}