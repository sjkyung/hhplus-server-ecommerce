package kr.hhplus.be.server.application.event


import kr.hhplus.be.server.application.dataPlatform.DataPlatformCommand
import kr.hhplus.be.server.application.dataPlatform.DataPlatformService
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener
import java.time.LocalDateTime

@Component
class DataPlatformSyncListener(
    private val dataPlatformService: DataPlatformService
) {

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleOrderDataPlatformSyncEvent(event: OrderDataPlatformSyncEvent) {
        //데이터 플랫폼 전송
        val dataPlatformCommand= DataPlatformCommand(
            orderId = event.orderId,
            userId = event.userId,
            totalPrice = event.totalPrice,
            sendTime = LocalDateTime.now()
        )
        dataPlatformService.send(dataPlatformCommand);
    }

}