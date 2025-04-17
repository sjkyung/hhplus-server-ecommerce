package kr.hhplus.be.server.domain.point

import kr.hhplus.be.server.domain.base.Timestamped
import java.time.LocalDateTime


data class UserPoint(
    val id: Long,
    val userId: Long,
    var point: Long,
): Timestamped() {

    fun charge(amount: Long): UserPoint{
        require(amount > 0) {
            "충전할 포인트는 0보다 커야 합니다."
        }

        updatedAt = LocalDateTime.now()
        return UserPoint(
            id,
            userId,
            point + amount
        )
    }

    fun use(amount: Long): UserPoint{
        require(amount > 0) { "사용할 포인트는 0보다 커야 합니다."}
        check(point >= amount) { "포인트 잔액이 부족합니다. (요청: $amount, 잔액: $point)"}
        updatedAt = LocalDateTime.now()
        return UserPoint(
            id,
            userId,
            point - amount
        )
    }

    fun validateAvailable(
        usedPoint : Long
    ){
        check(point >= usedPoint) {
            "사용 가능한 포인트가 부족합니다. (보유: $point, 필요: $usedPoint)"
        }
    }
}