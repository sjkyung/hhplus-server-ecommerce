package kr.hhplus.be.server.application.event

data class OrderDataPlatformSyncEvent(
    val orderId: Long,
    val userId: Long,
    val totalPrice: Long

)
