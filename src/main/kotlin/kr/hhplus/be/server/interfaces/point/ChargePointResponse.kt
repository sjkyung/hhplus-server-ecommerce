package kr.hhplus.be.server.interfaces.point

import kr.hhplus.be.server.domain.point.UserPoint
import java.time.LocalDateTime

data class ChargePointResponse(
    val userId: Long,
    val amount: Int,
    val point: Long,
    val chargedAt: LocalDateTime,
) {
    companion object {
        fun from(
            point: UserPoint,
            chargedAmount: Long
        ): ChargePointResponse {
            return ChargePointResponse(
                userId = point.id,
                amount = chargedAmount.toInt(),
                point = point.point,
                chargedAt = point.updatedAt
            )
        }
    }
}