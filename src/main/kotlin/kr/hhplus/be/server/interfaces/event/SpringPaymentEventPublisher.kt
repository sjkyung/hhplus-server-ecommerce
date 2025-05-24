package kr.hhplus.be.server.interfaces.event


import kr.hhplus.be.server.application.event.OrderDataPlatformSyncEvent
import kr.hhplus.be.server.application.event.PaymentEventPublisher
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class SpringPaymentEventPublisher(
   private val applicationEventPublisher: ApplicationEventPublisher
): PaymentEventPublisher {

    override fun publishOrder(orderDataPlatformSyncEvent: OrderDataPlatformSyncEvent) {
        applicationEventPublisher.publishEvent(orderDataPlatformSyncEvent)
    }

}