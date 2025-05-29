package kr.hhplus.be.server.application.event


import org.springframework.kafka.core.KafkaTemplate
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener


@Component
class DataPlatformSyncListener(
    private val kafkaTemplate: KafkaTemplate<String, OrderDataPlatformSyncEvent>
) {

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleOrderDataPlatformSyncEvent(event: OrderDataPlatformSyncEvent) {
        kafkaTemplate.send("order-topic", OrderDataPlatformSyncEvent(event.orderId, event.userId, event.totalPrice))
    }

}