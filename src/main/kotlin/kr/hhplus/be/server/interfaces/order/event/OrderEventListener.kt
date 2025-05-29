package kr.hhplus.be.server.interfaces.order.event

import kr.hhplus.be.server.application.dataPlatform.DataPlatformCommand
import kr.hhplus.be.server.application.dataPlatform.DataPlatformService
import kr.hhplus.be.server.application.event.OrderDataPlatformSyncEvent
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class OrderEventListener(
    private val dataPlatformService: DataPlatformService
) {
    @KafkaListener(topics = ["order-topic"])
    fun consumeOrder(event : OrderDataPlatformSyncEvent) {
        println("[KafkaListener] Consumed event: $event")
        val dataPlatformCommand= DataPlatformCommand(
            orderId = event.orderId,
            userId = event.userId,
            totalPrice = event.totalPrice,
            sendTime = LocalDateTime.now()
        )
        dataPlatformService.send(dataPlatformCommand);
    }

}