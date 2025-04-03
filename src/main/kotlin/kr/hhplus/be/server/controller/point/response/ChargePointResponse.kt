package kr.hhplus.be.server.controller.point.response

import java.time.LocalDateTime

data class ChargePointResponse(
    val userId: Long,
    val amount: Int,
    val point: Int,
    val chargedAt: LocalDateTime,
) {
}