package kr.hhplus.be.server.infrastructure.point

import kr.hhplus.be.server.domain.point.UserPoint


object UserPointConverter {

    fun toDomain(userPointEntity: UserPointEntity): UserPoint {
        return UserPoint(
            id = userPointEntity.id,
            userId = userPointEntity.userId,
            point = userPointEntity.point,
        )
    }

    fun toEntity(userPoint: UserPoint): UserPointEntity {
        return UserPointEntity(
            id = userPoint.id,
            userId = userPoint.userId,
            point = userPoint.point,
        )
    }
}