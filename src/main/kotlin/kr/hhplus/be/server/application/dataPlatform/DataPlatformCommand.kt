package kr.hhplus.be.server.application.dataPlatform

import java.time.LocalDateTime

data class DataPlatformCommand(
    val orderId : Long,
    val userId: Long,
    val totalPrice: Long,
    val sendTime: LocalDateTime
){

}
