package kr.hhplus.be.server.application.event


import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component


@Component
class CouponIssuedKafkaPublisher(
    private val kafkaTemplate: KafkaTemplate<String, CouponIssuedEvent>
) {

    fun publishCouponIssuedEvent(event: CouponIssuedEvent) {
        kafkaTemplate.send("coupon-issued-topic", event)
    }

}