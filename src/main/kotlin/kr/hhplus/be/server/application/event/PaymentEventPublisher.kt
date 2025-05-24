package kr.hhplus.be.server.application.event

interface PaymentEventPublisher {
    fun publishOrder(orderDataPlatformSyncEvent: OrderDataPlatformSyncEvent)
}