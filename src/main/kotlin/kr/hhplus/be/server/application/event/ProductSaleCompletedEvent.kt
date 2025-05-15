package kr.hhplus.be.server.application.event

data class ProductSaleCompletedEvent(
    val productId: Long,
    val quantity: Int
)
