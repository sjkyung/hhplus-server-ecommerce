package kr.hhplus.be.server.interfaces.point

import kr.hhplus.be.server.application.point.PointChargeCommand

data class ChargePointRequest(
    val amount: Long,
) {
    fun toCommand(
        userId: Long
    ): PointChargeCommand {
        return PointChargeCommand(
            userId = userId,
            amount = amount
        )
    }
}